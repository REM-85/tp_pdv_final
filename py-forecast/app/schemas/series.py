from datetime import date as date_type
from typing import List, Optional

from pydantic import BaseModel, Field, model_validator


class DataPoint(BaseModel):
    date: date_type = Field(..., description="Fecha del dato (dia)")
    y: float = Field(..., description="Valor observado")


class TrainPayload(BaseModel):
    series_id: str = Field(..., min_length=1, max_length=120)
    data: List[DataPoint] = Field(..., min_length=10)
    params: Optional[dict] = Field(default=None, description="Hiperparametros opcionales")

    @model_validator(mode="after")
    def validate_unique_dates(self):
        dates = {point.date for point in self.data}
        if len(dates) != len(self.data):
            raise ValueError("Las fechas deben ser unicas")
        return self


class PredictPayload(BaseModel):
    series_id: str = Field(..., min_length=1, max_length=120)
    horizon_days: int = Field(..., ge=1, le=90)
    exogenous: Optional[dict] = None


class MetricResponse(BaseModel):
    mape: float
    smape: float


class TrainingResult(BaseModel):
    series_id: str
    model_type: str
    metrics: MetricResponse


class ForecastPoint(BaseModel):
    date: date_type
    yhat: float
    yhat_lower: Optional[float] = None
    yhat_upper: Optional[float] = None


class ForecastResult(BaseModel):
    series_id: str
    model_type: str
    metrics: MetricResponse
    predictions: List[ForecastPoint]


class HealthResponse(BaseModel):
    status: str = "ok"
    detail: str
