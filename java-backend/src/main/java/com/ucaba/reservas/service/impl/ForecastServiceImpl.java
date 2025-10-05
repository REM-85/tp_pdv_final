package com.ucaba.reservas.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ucaba.reservas.dto.ForecastPoint;
import com.ucaba.reservas.dto.ForecastRequest;
import com.ucaba.reservas.dto.ForecastResponse;
import com.ucaba.reservas.dto.HistoryPoint;
import com.ucaba.reservas.dto.HistoryResponse;
import com.ucaba.reservas.dto.TrainRequest;
import com.ucaba.reservas.dto.TrainResponse;
import com.ucaba.reservas.exception.BadRequestException;
import com.ucaba.reservas.service.ForecastService;
import com.ucaba.reservas.service.ReservationService;
import java.time.LocalDate;
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
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final int defaultHorizon;
    private final int maxHorizon;

    public ForecastServiceImpl(ReservationService reservationService,
                               RestTemplate restTemplate,
                               @Value("${predictor.base-url}") String baseUrl,
                               @Value("${reservas.forecast.default-horizon-days}") int defaultHorizon,
                               @Value("${reservas.forecast.max-horizon-days}") int maxHorizon) {
        this.reservationService = reservationService;
        this.restTemplate = restTemplate;
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
            return new ForecastResponse(result.seriesId(), result.metrics(), points);
        } catch (RestClientException ex) {
            log.error("Error llamando al predictor", ex);
            throw new BadRequestException("No se pudo obtener el pronostico");
        }
    }

    private <T> HttpEntity<T> buildEntity(T payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(payload, headers);
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
