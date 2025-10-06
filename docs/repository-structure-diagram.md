# Diagrama de archivos del repositorio

```
tp_pdv_final/
├── .git/ … Metadatos de Git para control de versiones (no se detalla su contenido interno).
├── .gitignore — Reglas para excluir artefactos (builds de Maven, virtualenvs, modelos, etc.).
├── README.md — Descripción general del proyecto, pasos de ejecución y guía rápida.
├── docker-compose.yml — Orquestación opcional para levantar backend Java, predictor Python y base de datos.
├── data/
│   └── sample-reservations.csv — Muestra de reservas históricas utilizada para demos o importaciones.
├── docs/
│   ├── arquitectura.md — Explica la arquitectura, capas y decisiones técnicas.
│   ├── endpoints.md — Documenta los endpoints REST expuestos por el backend Java.
│   ├── presentacion-outline.md — Guion de presentación con puntos clave del proyecto.
│   └── repository-structure-diagram.md — Este diagrama textual con la descripción de cada archivo.
├── java-backend/
│   ├── pom.xml — Configuración de Maven con dependencias, plugins y metadatos del servicio Java.
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/ucaba/reservas/
│       │   │       ├── ReservasBackendApplication.java — Punto de entrada Spring Boot.
│       │   │       ├── config/
│       │   │       │   ├── AppConfig.java — Define beans como RestTemplate con interceptor de correlación.
│       │   │       │   ├── CorrelationIdFilter.java — Filtro servlet que asegura y propaga el X-Correlation-Id.
│       │   │       │   └── RestExceptionHandler.java — Manejo centralizado de excepciones y respuestas de error.
│       │   │       ├── controller/
│       │   │       │   ├── ForecastController.java — Endpoints para entrenar modelos y obtener pronósticos.
│       │   │       │   ├── ItemController.java — CRUD de artículos reservables.
│       │   │       │   ├── PersonController.java — CRUD de personas con roles.
│       │   │       │   ├── ReservationController.java — Gestión de reservas, disponibilidad e históricos.
│       │   │       │   └── RoomController.java — CRUD de salas.
│       │   │       ├── dto/
│       │   │       │   ├── AvailabilityResponse.java — Respuesta con bloques disponibles por fecha.
│       │   │       │   ├── ErrorResponse.java — Formato estándar de errores HTTP.
│       │   │       │   ├── ForecastPoint.java — Punto individual dentro de un pronóstico devuelto.
│       │   │       │   ├── ForecastRequest.java — Payload para solicitar pronósticos al servicio Python.
│       │   │       │   ├── ForecastResponse.java — Respuesta de pronóstico agregada y formateada.
│       │   │       │   ├── HistoryPoint.java — Punto de histórico devuelto por ReservationService.
│       │   │       │   ├── HistoryResponse.java — Colección de históricos agrupados.
│       │   │       │   ├── ItemRequest.java — Datos de alta/edición para artículos.
│       │   │       │   ├── ItemResponse.java — Representación de artículo expuesta por la API.
│       │   │       │   ├── PersonRequest.java — Datos validados para crear/actualizar personas.
│       │   │       │   ├── PersonResponse.java — DTO de salida con información de personas.
│       │   │       │   ├── ReservationRequest.java — Solicitud para crear o editar reservas.
│       │   │       │   ├── ReservationResponse.java — Detalle de reservas expuestas al cliente.
│       │   │       │   ├── RoomRequest.java — Payload de creación/actualización de salas.
│       │   │       │   ├── RoomResponse.java — DTO de salida para salas.
│       │   │       │   ├── TrainRequest.java — Comando para iniciar entrenamiento desde Java.
│       │   │       │   └── TrainResponse.java — Resultado devuelto al invocar entrenamiento externo.
│       │   │       ├── exception/
│       │   │       │   ├── BadRequestException.java — Excepción para errores de validación.
│       │   │       │   ├── ConflictException.java — Excepción cuando se detectan solapamientos o duplicados.
│       │   │       │   └── NotFoundException.java — Excepción para recursos inexistentes.
│       │   │       ├── mapper/
│       │   │       │   ├── ItemMapper.java — Conversión entre entidades Item y DTOs.
│       │   │       │   ├── PersonMapper.java — Conversión entre entidades Person y DTOs.
│       │   │       │   ├── ReservationMapper.java — Traductor de reservas incluyendo recurso asociado.
│       │   │       │   └── RoomMapper.java — Conversión de entidades Room a DTOs y viceversa.
│       │   │       ├── model/
│       │   │       │   ├── Item.java — Entidad JPA para artículos con atributos de disponibilidad.
│       │   │       │   ├── Person.java — Entidad JPA de personas y su rol.
│       │   │       │   ├── Reservation.java — Entidad JPA que vincula recursos y rangos horarios.
│       │   │       │   ├── Room.java — Entidad JPA de salas.
│       │   │       │   └── UserRole.java — Enum con los roles permitidos para Person.
│       │   │       ├── repository/
│       │   │       │   ├── ItemRepository.java — JpaRepository para Items con consultas de disponibilidad.
│       │   │       │   ├── PersonRepository.java — JpaRepository de personas con búsqueda por email.
│       │   │       │   ├── ReservationRepository.java — JpaRepository que maneja solapamientos e históricos.
│       │   │       │   └── RoomRepository.java — JpaRepository de salas.
│       │   │       ├── service/
│       │   │       │   ├── ForecastService.java — Contrato para interacción con el predictor.
│       │   │       │   ├── ItemService.java — Interface de operaciones sobre artículos.
│       │   │       │   ├── PersonService.java — Interface para personas.
│       │   │       │   ├── ReservationService.java — Interface para reservas y reportes.
│       │   │       │   └── RoomService.java — Interface de salas.
│       │   │       └── service/impl/
│       │   │           ├── ForecastServiceImpl.java — Lógica para invocar API Python y transformar respuestas.
│       │   │           ├── ItemServiceImpl.java — Implementación CRUD con validaciones de negocio de artículos.
│       │   │           ├── PersonServiceImpl.java — Implementación CRUD de personas, validando duplicados y roles.
│       │   │           ├── ReservationServiceImpl.java — Reglas de reserva, disponibilidad y construcción de históricos.
│       │   │           └── RoomServiceImpl.java — Implementación CRUD de salas.
│       │   └── resources/
│       │       ├── application.yml — Configuraciones de Spring Boot, H2, logs y URLs del predictor.
│       │       └── data.sql — Script de carga inicial para la base H2.
│       └── test/
│           ├── java/com/ucaba/reservas/controller/ForecastControllerTest.java — Pruebas unitarias del controlador de pronósticos.
│           ├── java/com/ucaba/reservas/service/ReservationServiceTest.java — Tests de reglas críticas de reserva y disponibilidad.
│           └── resources/application-test.yml — Configuración de test para la base embebida.
├── postman/
│   └── Reserva-Predictor.postman_collection.json — Colección de endpoints para pruebas manuales en Postman.
└── py-forecast/
    ├── Dockerfile — Imagen para desplegar el microservicio de pronósticos.
    ├── requirements.txt — Dependencias de Python (FastAPI, statsmodels, etc.).
    ├── app/
    │   ├── __init__.py — Marca el paquete de la aplicación.
    │   ├── main.py — Define la aplicación FastAPI y sus endpoints.
    │   ├── schemas/
    │   │   ├── __init__.py — Exporta los modelos Pydantic disponibles.
    │   │   └── series.py — Esquemas Pydantic para requests/responses de entrenamiento y predicción.
    │   ├── services/
    │   │   ├── __init__.py — Exposición pública de servicios.
    │   │   ├── storage.py — Persistencia de modelos entrenados en disco mediante joblib.
    │   │   └── trainer.py — Selección de modelo (baseline o SARIMAX) y lógica de forecasting.
    │   └── utils/
    │       ├── __init__.py — Inicialización del subpaquete utilitario.
    │       └── metrics.py — Funciones de métrica (MAPE, SMAPE) usadas en entrenamiento.
    ├── data/
    │   └── sample_series.json — Serie de ejemplo para pruebas manuales.
    └── tests/
        ├── __init__.py — Inicializa el paquete de tests.
        └── test_api.py — Pruebas del API FastAPI (health, entrenamiento, error de predict sin modelo).
```

