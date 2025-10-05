from __future__ import annotations

import numpy as np


def mape(actual: np.ndarray, predicted: np.ndarray) -> float:
    """Use small epsilon to avoid division by zero on flat series."""
    actual = np.asarray(actual)
    predicted = np.asarray(predicted)
    epsilon = 1e-6
    return float(np.mean(np.abs((actual - predicted) / np.maximum(np.abs(actual), epsilon))) * 100.0)


def smape(actual: np.ndarray, predicted: np.ndarray) -> float:
    actual = np.asarray(actual)
    predicted = np.asarray(predicted)
    denominator = (np.abs(actual) + np.abs(predicted))
    denominator = np.where(denominator == 0, 1e-6, denominator)
    return float(np.mean(np.abs(actual - predicted) / denominator) * 100.0 * 2)

