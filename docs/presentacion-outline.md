# Guion sugerido para la presentacion

1. **Contexto y problema** (1 slide)
   - Migracion forzosa de plataforma legacy (Java 8 + JDBC) hacia arquitectura moderna.
   - Necesidad de sumar prediccion de uso para optimizar recursos.

2. **Objetivos del MVP** (1 slide)
   - CRUD de recursos/personas/reservas con disponibilidad online.
   - Exportar historial y consumir pronosticos.
   - Docker Compose para demo reproducible.

3. **Arquitectura propuesta** (2 slides)
   - Diagrama alto nivel (Java + FastAPI + H2 + modelos pickle).
   - Justificacion de tecnologias (Spring Boot 3, FastAPI, SARIMAX baseline).

4. **Flujo funcional** (1 slide)
   - Secuencia: crear reserva → enviar historial → entrenar → pronosticar → mostrar ocupacion.

5. **Demo / evidencias** (2 slides)
   - Capturas de Postman (CRUD + forecast).
   - Grafico de serie vs pronostico (usar `py-forecast/data/sample_series.json`).

6. **Calidad & Observabilidad** (1 slide)
   - Validaciones, logs con correlation id, tests unitarios/contrato.

7. **Roadmap** (1 slide)
   - Autenticacion, Postgres, UI, cache de pronostico, metricas adicionales.

> Tip: convertir este outline a PPTX/PDF agregando capturas del sistema y metricas del predictor.

