# Arquitectura MVP

```mermaid
 flowchart LR
     subgraph Cliente
         UI[UI / Postman]
     end
     subgraph Java[Spring Boot API]
         Controller[REST Controllers]
         Service[Services]
         Repo[JPA Repositories]
         H2[(H2 DB)]
     end
     subgraph Python[FastAPI Predictor]
         FastAPI[FastAPI]
         Trainer[Selector de modelo]
         Store[(Modelos .pkl)]
     end
     UI -->|HTTP JSON| Controller
     Controller --> Service
     Service --> Repo
     Repo --> H2
     Service -->|/train /predict| FastAPI
     FastAPI --> Trainer
     Trainer --> Store
 ```

## Decisiones tecnicas

- **Spring Boot 3 + H2**: habilita feedback rapido y deja preparada la migracion a un RDBMS productivo.
- **Capas Controller/Service/Repository**: aisla la logica y facilita tests unitarios.
- **FastAPI + SARIMAX**: permite exponer el predictor como microservicio y usar baseline cuando no hay suficiente historia.
- **Correlacion cross-service**: todas las requests cargan `X-Correlation-Id` para rastrear issues entre Java↔Python.
- **Docker Compose**: simplifica la demo integrando ambos servicios con URLs correctas.

## Observabilidad y calidad

- Logs con correlacion por request en ambos servicios.
- Validaciones de payload via Bean Validation (Java) y Pydantic (Python).
- Tests minimos: service-layer (Java) y endpoints (Python) para cubrir casos felices y bordes.

## Proximos pasos sugeridos

1. Agregar autenticacion (Keycloak/JWT) sobre `/api/**`.
2. Externalizar persistencia a Postgres y usar Flyway para versionar el esquema.
3. Crear UI ligera con calendario + tarjetas de ocupacion.
4. Incorporar cache en `/api/forecast` reutilizando el ultimo resultado por recurso.

