# Contrato REST (MVP)

## Backend Java (`http://localhost:8080`)

| Metodo | Camino | Descripcion | Request | Response |
| --- | --- | --- | --- | --- |
| GET | `/api/persons` | Listar personas | - | `200` lista `PersonResponse` |
| POST | `/api/persons` | Crear persona | `PersonRequest` | `201` `PersonResponse` |
| PUT | `/api/persons/{id}` | Actualizar | `PersonRequest` | `200` `PersonResponse` |
| DELETE | `/api/persons/{id}` | Borrar | - | `204` |
| GET | `/api/rooms` | Listar salas | - | `200` lista |
| POST | `/api/rooms` | Crear sala | `RoomRequest` | `201` |
| GET | `/api/items` | Listar articulos | - | `200` |
| POST | `/api/items` | Crear articulo | `ItemRequest` | `201` |
| GET | `/api/reservations` | Listar reservas ordenadas | - | `200` lista `ReservationResponse` |
| POST | `/api/reservations` | Nueva reserva | `ReservationRequest` | `201` |
| GET | `/api/reservations/availability` | Disponibilidad | Query `resourceType`, `resourceId`, `start`, `end` | `200` `AvailabilityResponse` |
| GET | `/api/reservations/history` | Historial para predictor | Query `resourceType`, `resourceId`, `startDate?`, `endDate?` | `200` `HistoryResponse` |
| POST | `/api/forecast/train` | Exporta historia y solicita entrenamiento | `TrainRequest` | `202` `TrainResponse` |
| POST | `/api/forecast` | Pide pronostico | `ForecastRequest` | `200` `ForecastResponse` |

### Ejemplos

**ReservationRequest**
```json
{
  "personId": 1,
  "roomId": 2,
  "startDateTime": "2025-09-15T10:00:00",
  "endDateTime": "2025-09-15T11:30:00"
}
```

**AvailabilityResponse**
```json
{
  "resourceType": "ROOM",
  "resourceId": 2,
  "available": true,
  "requestedStart": "2025-09-15T10:00:00",
  "requestedEnd": "2025-09-15T11:30:00"
}
```

## Predictor Python (`http://localhost:8000`)

| Metodo | Camino | Descripcion | Request | Response |
| --- | --- | --- | --- | --- |
| GET | `/health` | Ping de vida | - | `200` `{ "status": "ok", "detail": "predictor online" }` |
| POST | `/train` | Entrena modelo y guarda `.pkl` | `TrainPayload` | `202` `TrainingResult` |
| POST | `/predict` | Genera pronostico usando modelo guardado | `PredictPayload` | `200` `ForecastResult` |

**TrainPayload**
```json
{
  "series_id": "ROOM-2",
  "data": [
    {"date": "2025-07-01", "y": 3},
    {"date": "2025-07-02", "y": 4}
  ]
}
```

**ForecastResult**
```json
{
  "series_id": "ROOM-2",
  "model_type": "baseline",
  "metrics": {"mape": 9.2, "smape": 8.7},
  "predictions": [
    {"date": "2025-09-16", "yhat": 4.1},
    {"date": "2025-09-17", "yhat": 4.0}
  ]
}
```

## Errores comunes

- `400 Bad Request`: validacion de negocio (rango horario incorrecto, recurso invalido).
- `404 Not Found`: id inexistente o modelo no entrenado.
- `409 Conflict`: reserva se superpone o recurso marcado como no disponible.
- `422 Unprocessable Entity`: payload con tipos o formatos erroneos.

