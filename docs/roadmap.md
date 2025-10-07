# Roadmap para funcionalidades solicitadas

## 1. Registro y autenticación de usuarios por roles
- Integrar un proveedor de identidad (Keycloak o Spring Authorization Server) siguiendo la sugerencia de la arquitectura para proteger `/api/**` con JWT y roles por recurso.
- Extender el modelo de datos en `java-backend` agregando entidades `User`, `Role` y tablas relacionales, externalizando la base a Postgres para persistencia duradera.
- Implementar endpoints de registro y administración de roles, complementados con un flujo de autenticación basado en OAuth2/OpenID Connect.
- Actualizar filtros y controladores existentes para validar tokens, aplicar `@PreAuthorize` y restringir operaciones sensibles (por ejemplo, creación de reservas) según el rol.

## 2. Ingreso y actualización de datos (manual y vía API)
- Mantener los endpoints REST existentes para carga automatizada desde integraciones externas (`/api/persons`, `/api/rooms`, `/api/items`, `/api/reservations`).
- Incorporar un frontend ligero (por ejemplo, React o Vue) como cliente web que consuma dichos endpoints y brinde formularios para alta/edición manual, tal como propone la documentación de arquitectura.
- Asegurar validaciones consistentes entre frontend y backend, reutilizando los esquemas ya definidos (`PersonRequest`, `RoomRequest`, etc.) para evitar discrepancias.
- Añadir flujos de carga masiva (CSV/Excel) y webhooks opcionales para sistemas terceros, reutilizando la lógica de servicios existente.

## 3. Monitor de predicción de reservas
- Construir una vista en el frontend que combine datos históricos (`/api/reservations/history`) y pronósticos (`/api/forecast`) para cada recurso reservado.
- Visualizar resultados en componentes de gráficos (línea/área) que muestren demanda proyectada y ocupación real, resaltando alertas cuando se supere la capacidad disponible.
- Exponer métricas adicionales desde el microservicio de predicción (por ejemplo, precisión, fecha de último entrenamiento) para enriquecer el tablero.
- Automatizar la orquestación entre backend y predictor (jobs programados para `/api/forecast/train`) y notificaciones ante cambios significativos en las predicciones.

## 4. Consideraciones transversales
- Extender observabilidad (logs correlacionados, métricas) para abarcar frontend y autenticación.
- Definir pruebas end-to-end que cubran registros, reservas y visualización del monitor para evitar regresiones.
- Documentar flujos de usuario y diagramas actualizados en la carpeta `docs/` para alinear a los equipos de desarrollo y negocio.
