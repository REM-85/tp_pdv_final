from __future__ import annotations

import os
from pathlib import Path
from typing import Any

import joblib


class ModelStorage:
    """Persist models under /models/{series_id}.pkl and make path configurable."""

    def __init__(self, base_path: str = None) -> None:
        self.base_path = Path(base_path or Path(__file__).resolve().parents[1] / "models")
        self.base_path.mkdir(parents=True, exist_ok=True)

    def _path_for(self, series_id: str) -> Path:
        safe_name = series_id.replace("/", "_")
        return self.base_path / f"{safe_name}.pkl"

    def save(self, series_id: str, model: Any) -> None:
        joblib.dump(model, self._path_for(series_id))

    def load(self, series_id: str) -> Any:
        path = self._path_for(series_id)
        if not path.exists():
            raise FileNotFoundError(f"Modelo no entrenado para {series_id}")
        return joblib.load(path)

    def exists(self, series_id: str) -> bool:
        return self._path_for(series_id).exists()


__all__ = ["ModelStorage"]

