package com.ucaba.reservas.service;

import com.ucaba.reservas.dto.ForecastRequest;
import com.ucaba.reservas.dto.ForecastResponse;
import com.ucaba.reservas.dto.TrainRequest;
import com.ucaba.reservas.dto.TrainResponse;

public interface ForecastService {
    TrainResponse train(TrainRequest request);

    ForecastResponse forecast(ForecastRequest request);
}

