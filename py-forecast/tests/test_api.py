from datetime import date, timedelta

from fastapi.testclient import TestClient

from app.main import app, get_trainer
from app.services.storage import ModelStorage
from app.services.trainer import ForecastTrainer


def build_payload(days: int = 90, start: date | None = None):
    start = start or date.today() - timedelta(days=days)
    data = []
    for idx in range(days):
        current = start + timedelta(days=idx)
        data.append({"date": current.isoformat(), "y": 10 + idx % 5})
    return {"series_id": "test-series", "data": data}


def test_health_returns_ok():
    client = TestClient(app)
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json()["status"] == "ok"


def test_train_persists_model(tmp_path):
    storage = ModelStorage(base_path=tmp_path)
    trainer = ForecastTrainer(storage)
    app.dependency_overrides[get_trainer] = lambda: trainer
    client = TestClient(app)

    response = client.post("/train", json=build_payload())
    assert response.status_code == 202
    body = response.json()
    assert body["model_type"] in {"baseline", "sarimax"}
    assert body["metrics"]["mape"] >= 0
    assert storage.exists("test-series")

    app.dependency_overrides.clear()


def test_predict_without_training(tmp_path):
    storage = ModelStorage(base_path=tmp_path)
    trainer = ForecastTrainer(storage)
    app.dependency_overrides[get_trainer] = lambda: trainer
    client = TestClient(app)

    response = client.post("/predict", json={"series_id": "missing", "horizon_days": 7})
    assert response.status_code == 404

    app.dependency_overrides.clear()
