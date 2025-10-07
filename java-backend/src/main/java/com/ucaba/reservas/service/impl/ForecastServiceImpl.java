package com.ucaba.reservas.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucaba.reservas.dto.ForecastPoint;
import com.ucaba.reservas.dto.ForecastRequest;
import com.ucaba.reservas.dto.ForecastResponse;
import com.ucaba.reservas.dto.HistoryPoint;
import com.ucaba.reservas.dto.HistoryResponse;
import com.ucaba.reservas.dto.ForecastSnapshotResponse;
import com.ucaba.reservas.dto.TrainRequest;
import com.ucaba.reservas.dto.TrainResponse;
import com.ucaba.reservas.exception.BadRequestException;
import com.ucaba.reservas.service.ForecastService;
import com.ucaba.reservas.service.ReservationService;
import com.ucaba.reservas.model.ForecastSnapshot;
import com.ucaba.reservas.repository.ForecastSnapshotRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ForecastServiceImpl implements ForecastService {

    private static final Logger log = LoggerFactory.getLogger(ForecastServiceImpl.class);

    private final ReservationService reservationService;
    private final ForecastSnapshotRepository snapshotRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final int defaultHorizon;
    private final int maxHorizon;

    public ForecastServiceImpl(ReservationService reservationService,
                               ForecastSnapshotRepository snapshotRepository,
                               RestTemplate restTemplate,
                               ObjectMapper objectMapper,
                               @Value("${predictor.base-url}") String baseUrl,
                               @Value("${reservas.forecast.default-horizon-days}") int defaultHorizon,
                               @Value("${reservas.forecast.max-horizon-days}") int maxHorizon) {
        this.reservationService = reservationService;
        this.snapshotRepository = snapshotRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl;
        this.defaultHorizon = defaultHorizon;
        this.maxHorizon = maxHorizon;
    }

    @Override
    public TrainResponse train(TrainRequest request) {
        HistoryResponse history = reservationService.buildHistory(
                request.getResourceType(), request.getResourceId(), request.getStartDate(), request.getEndDate());
        TrainPayload payload = new TrainPayload(history.getSeriesId(),
                history.getPoints().stream()
                        .map(point -> new TrainPoint(point.getDate().toLocalDate(), point.getValue()))
                        .collect(Collectors.toList()));
        try {
            HttpEntity<TrainPayload> entity = buildEntity(payload);
            TrainResult result = restTemplate.postForObject(baseUrl + "/train", entity, TrainResult.class);
            if (result == null) {
                throw new BadRequestException("Respuesta vacia del predictor");
            }
            return new TrainResponse(result.seriesId(), result.metrics());
        } catch (RestClientException ex) {
            log.error("Error llamando al predictor", ex);
            throw new BadRequestException("No se pudo entrenar el modelo");
        }
    }

    @Override
    public ForecastResponse forecast(ForecastRequest request) {
        int horizon = request.getHorizonDays() > 0 ? request.getHorizonDays() : defaultHorizon;
        if (horizon > maxHorizon) {
            throw new BadRequestException("Horizonte supera el maximo permitido");
        }
        HistoryResponse history = reservationService.buildHistory(
                request.getResourceType(), request.getResourceId(), null, null);
        ForecastPayload payload = new ForecastPayload(history.getSeriesId(), horizon);
        try {
            HttpEntity<ForecastPayload> entity = buildEntity(payload);
            ForecastResult result = restTemplate.postForObject(baseUrl + "/predict", entity, ForecastResult.class);
            if (result == null) {
                throw new BadRequestException("Respuesta vacia del predictor");
            }
            List<ForecastPoint> points = result.predictions().stream()
                    .map(p -> new ForecastPoint(p.date(), p.yhat(), p.yhatLower(), p.yhatUpper()))
                    .collect(Collectors.toList());
            ForecastResponse response = new ForecastResponse(result.seriesId(), result.metrics(), points);
            persistSnapshot(request, response);
            return response;
        } catch (RestClientException ex) {
            log.error("Error llamando al predictor", ex);
            throw new BadRequestException("No se pudo obtener el pronostico");
        }
    }

    @Override
    public List<ForecastSnapshotResponse> monitor() {
        return snapshotRepository.findTop20ByOrderByGeneratedAtDesc().stream()
                .map(this::toSnapshotResponse)
                .collect(Collectors.toList());
    }

    private <T> HttpEntity<T> buildEntity(T payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(payload, headers);
    }

    private void persistSnapshot(ForecastRequest request, ForecastResponse response) {
        ForecastSnapshot snapshot = new ForecastSnapshot();
        snapshot.setSeriesId(response.getSeriesId());
        snapshot.setResourceType(request.getResourceType());
        snapshot.setResourceId(request.getResourceId());
        snapshot.setGeneratedAt(LocalDateTime.now());
        snapshot.setHorizonDays(response.getPoints().size());
        snapshot.setMetricsJson(writeJson(response.getMetrics()));
        snapshot.setPointsJson(writeJson(response.getPoints()));
        snapshotRepository.save(snapshot);
    }

    private ForecastSnapshotResponse toSnapshotResponse(ForecastSnapshot snapshot) {
        Map<String, Double> metrics = readMetrics(snapshot.getMetricsJson());
        List<ForecastPoint> points = readPoints(snapshot.getPointsJson());
        return new ForecastSnapshotResponse(snapshot.getId(), snapshot.getSeriesId(), snapshot.getResourceType(),
                snapshot.getResourceId(), snapshot.getGeneratedAt(), snapshot.getHorizonDays(), metrics, points);
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new BadRequestException("No se pudo serializar el resultado del pronostico");
        }
    }

    private Map<String, Double> readMetrics(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Double>>() {
            });
        } catch (JsonProcessingException ex) {
            log.error("No se pudo parsear métricas de pronostico", ex);
            throw new BadRequestException("Error interno leyendo el monitoreo");
        }
    }

    private List<ForecastPoint> readPoints(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<ForecastPoint>>() {
            });
        } catch (JsonProcessingException ex) {
            log.error("No se pudo parsear puntos de pronostico", ex);
            throw new BadRequestException("Error interno leyendo el monitoreo");
        }
    }

    private record TrainPayload(@JsonProperty("series_id") String seriesId,
                                List<TrainPoint> data) {
    }

    private record TrainPoint(LocalDate date, double y) {
    }

    private record TrainResult(@JsonProperty("series_id") String seriesId,
                               Map<String, Double> metrics) {
    }

    private record ForecastPayload(@JsonProperty("series_id") String seriesId,
                                   @JsonProperty("horizon_days") int horizonDays) {
    }

    private record ForecastResult(@JsonProperty("series_id") String seriesId,
                                  Map<String, Double> metrics,
                                  List<ForecastEntry> predictions) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record ForecastEntry(LocalDate date,
                                 double yhat,
                                 @JsonProperty("yhat_lower") Double yhatLower,
                                 @JsonProperty("yhat_upper") Double yhatUpper) {
    }
}
