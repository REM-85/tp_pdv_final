package com.ucaba.reservas.controller;

import com.ucaba.reservas.dto.ForecastRequest;
import com.ucaba.reservas.dto.ForecastResponse;
import com.ucaba.reservas.dto.TrainRequest;
import com.ucaba.reservas.dto.TrainResponse;
import com.ucaba.reservas.service.ForecastService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/forecast")
public class ForecastController {

    private final ForecastService forecastService;

    public ForecastController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    @PostMapping("/train")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TrainResponse train(@Valid @RequestBody TrainRequest request) {
        return forecastService.train(request);
    }

    @PostMapping
    public ForecastResponse forecast(@Valid @RequestBody ForecastRequest request) {
        return forecastService.forecast(request);
    }
}

