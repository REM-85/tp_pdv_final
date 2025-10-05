from __future__ import annotations

import logging
from fastapi import Depends, FastAPI, Header, HTTPException, status
from fastapi.responses import JSONResponse

from app.schemas import (
    ForecastResult,
    HealthResponse,
    PredictPayload,
    TrainPayload,
    TrainingResult,
)
from app.services.storage import ModelStorage
from app.services.trainer import ForecastTrainer


logger = logging.getLogger("forecast")
logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s [%(name)s] %(message)s")

app = FastAPI(title="Reserva Predictor", version="0.1.0")


def get_trainer() -> ForecastTrainer:
    return ForecastTrainer(ModelStorage())


@app.get("/health", response_model=HealthResponse)
def healthcheck() -> HealthResponse:
    return HealthResponse(detail="predictor online")


@app.post("/train", response_model=TrainingResult, status_code=status.HTTP_202_ACCEPTED)
def train(payload: TrainPayload, trainer: ForecastTrainer = Depends(get_trainer),
         x_correlation_id: str | None = Header(default=None, alias="X-Correlation-Id")) -> TrainingResult:
    logger.info("training series %s corr=%s", payload.series_id, x_correlation_id)
    try:
        return trainer.train(payload)
    except ValueError as exc:
        raise HTTPException(status_code=status.HTTP_422_UNPROCESSABLE_ENTITY, detail=str(exc)) from exc


@app.post("/predict", response_model=ForecastResult)
def predict(payload: PredictPayload, trainer: ForecastTrainer = Depends(get_trainer),
           x_correlation_id: str | None = Header(default=None, alias="X-Correlation-Id")) -> ForecastResult:
    logger.info("predicting series %s corr=%s", payload.series_id, x_correlation_id)
    try:
        return trainer.predict(payload)
    except FileNotFoundError as exc:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=str(exc)) from exc


@app.exception_handler(Exception)
async def handle_generic(request, exc):
    logger.exception("Unhandled exception")
    return JSONResponse(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, content={"detail": "Error interno"})

