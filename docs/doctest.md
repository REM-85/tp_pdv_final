## Diagrama de arquitectura

Diagrama Mermaid que muestra la interacción entre el backend Java y el servicio Python.

```mermaid
flowchart TB
  subgraph JavaBackend
    Client[Cliente / UI / Postman] --> ForecastC[ForecastController]
    Client --> ReservationC[ReservationController]
    Client --> PersonC[PersonController]
    Client --> RoomC[RoomController]
    Client --> ItemC[ItemController]

    ForecastC --> ForecastServiceImpl[ForecastServiceImpl]
    ReservationC --> ReservationServiceImpl[ReservationServiceImpl]
    PersonC --> PersonServiceImpl[PersonServiceImpl]
    RoomC --> RoomServiceImpl[RoomServiceImpl]
    ItemC --> ItemServiceImpl[ItemServiceImpl]

    ForecastServiceImpl --> ForecastClientHTTP[ForecastClientHTTP]
    ReservationServiceImpl --> ReservationRepository[ReservationRepository]
    PersonServiceImpl --> PersonRepository[PersonRepository]
    RoomServiceImpl --> RoomRepository[RoomRepository]
    ItemServiceImpl --> ItemRepository[ItemRepository]

    ReservationRepository --> DB[(Base de datos H2 / SQL Server)]
    PersonRepository --> DB
    RoomRepository --> DB
    ItemRepository --> DB

    ForecastClientHTTP --> PySvc[Python Predictor FastAPI]
  end

  subgraph PyForecast
    PySvc --> Trainer[servicios.trainer]
    PySvc --> Storage[servicios.storage]
    Trainer --> Storage
  end
```

Nota: si no ves el render en VS Code, instala la extensión "Mermaid Preview" o renombra el archivo a `.md` y utiliza un preview de Markdown con soporte para Mermaid.
