from __future__ import annotations

from dataclasses import dataclass
from datetime import date, timedelta
from typing import Dict, List

import numpy as np
import pandas as pd
from statsmodels.tsa.statespace.sarimax import SARIMAX

from app.schemas import (
    ForecastPoint,
    ForecastResult,
    MetricResponse,
    PredictPayload,
    TrainPayload,
    TrainingResult,
)
from app.services.storage import ModelStorage
from app.utils.metrics import mape, smape


@dataclass
class BaselineModel:
    model_type: str
    last_date: date
    day_mean: Dict[int, float]
    global_mean: float

    def forecast(self, horizon: int) -> np.ndarray:
        preds: List[float] = []
        current = self.last_date
        for _ in range(horizon):
            current += timedelta(days=1)
            mean = self.day_mean.get(current.weekday(), self.global_mean)
            preds.append(mean)
        return np.array(preds, dtype=float)


class ForecastTrainer:
    """Encapsula la logica de seleccion de modelo y calculo de metricas."""

    def __init__(self, storage: ModelStorage | None = None) -> None:
        self.storage = storage or ModelStorage()

    def train(self, payload: TrainPayload) -> TrainingResult:
        series = self._build_series(payload)
        model_type = self._choose_model(series)
        validation_size = max(7, min(30, len(series) // 4))
        train_series = series.iloc[:-validation_size]
        val_series = series.iloc[-validation_size:]

        if model_type == "baseline":
            model = self._fit_baseline(train_series)
            forecast = model.forecast(validation_size)
        else:
            model = self._fit_sarimax(train_series)
            sarimax_forecast = model.get_forecast(steps=validation_size)
            forecast = sarimax_forecast.predicted_mean.to_numpy()

        metrics = MetricResponse(
            mape=mape(val_series.to_numpy(), forecast),
            smape=smape(val_series.to_numpy(), forecast),
        )

        self.storage.save(payload.series_id, {
            "model_type": model_type,
            "model": model,
            "metrics": metrics.model_dump(),
        })
        return TrainingResult(series_id=payload.series_id, model_type=model_type, metrics=metrics)

    def predict(self, payload: PredictPayload) -> ForecastResult:
        stored = self.storage.load(payload.series_id)
        model_type = stored["model_type"]
        model = stored["model"]
        stored_metrics = stored.get("metrics")
        metrics = MetricResponse(**stored_metrics) if stored_metrics else MetricResponse(mape=-1.0, smape=-1.0)

        if model_type == "baseline":
            forecast = model.forecast(payload.horizon_days)
            last_date = model.last_date
            dates = [last_date + timedelta(days=i + 1) for i in range(payload.horizon_days)]
            lower = upper = None
        else:
            sarimax_forecast = model.get_forecast(steps=payload.horizon_days)
            forecast_series = sarimax_forecast.predicted_mean
            conf_int = sarimax_forecast.conf_int(alpha=0.2)
            dates = [idx.date() for idx in forecast_series.index]
            forecast = forecast_series.to_numpy()
            lower = conf_int.iloc[:, 0].to_numpy()
            upper = conf_int.iloc[:, 1].to_numpy()

        predictions = []
        for idx, value in enumerate(forecast):
            point = ForecastPoint(
                date=dates[idx],
                yhat=float(value),
                yhat_lower=float(lower[idx]) if lower is not None else None,
                yhat_upper=float(upper[idx]) if upper is not None else None,
            )
            predictions.append(point)

        return ForecastResult(series_id=payload.series_id, model_type=model_type, metrics=metrics, predictions=predictions)

    def _build_series(self, payload: TrainPayload) -> pd.Series:
        df = pd.DataFrame([{"date": point.date, "y": point.y} for point in payload.data])
        df = df.sort_values("date").set_index("date")
        df = df.asfreq("D")
        df["y"].interpolate(method="linear", inplace=True)
        return df["y"]

    def _choose_model(self, series: pd.Series) -> str:
        if len(series) < 60:
            return "baseline"
        if len(series) > 180 and self._has_weekly_seasonality(series):
            return "sarimax"
        return "baseline"

    def _fit_baseline(self, series: pd.Series) -> BaselineModel:
        # Average per weekday para capturar estacionalidad semanal sin costo.
        stats = series.groupby(series.index.weekday).mean().to_dict()
        return BaselineModel(
            model_type="baseline",
            last_date=series.index[-1].to_pydatetime().date(),
            day_mean={int(k): float(v) for k, v in stats.items()},
            global_mean=float(series.mean()),
        )

    def _fit_sarimax(self, series: pd.Series):
        model = SARIMAX(series, order=(1, 1, 1), seasonal_order=(1, 0, 1, 7), enforce_stationarity=False)
        return model.fit(disp=False)

    def _has_weekly_seasonality(self, series: pd.Series) -> bool:
        autocorr = series.autocorr(lag=7)
        return bool(autocorr and autocorr > 0.3)


__all__ = ["ForecastTrainer"]

