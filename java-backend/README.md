# Reservas Backend — notas de desarrollo

Este pequeño README explica cómo ejecutar pruebas locales y menciona un par de puntos importantes sobre la inicialización de la base de datos y tests.

Requisitos
- JDK 17 (usado en el proyecto)
- Maven 3.8+ (se probó con Maven 3.9.x)

Comandos útiles

- Ejecutar pruebas:

```powershell
cd "java-backend"
mvn clean test
```

- Compilar sólo (sin tests):

```powershell
mvn -DskipTests package
```

Notas importantes

1) Inicialización de la base de datos (H2)
- El proyecto usa un `data.sql` en `src/main/resources` que se ejecuta al iniciar la app en modo `spring.sql.init.mode: always`.
- Para evitar errores al ejecutar el script varias veces (p.ej. durante pruebas locales o reinicios), el `data.sql` fue adaptado para ser idempotente: incluye `DROP TABLE IF EXISTS` antes de crear las tablas críticas (p. ej. `forecast_snapshots`).
- Alternativa: si prefieres que Hibernate gestione todo el esquema en tests, ajusta `src/test/resources/application-test.yml` y establece `spring.sql.init.mode: never` y `spring.jpa.hibernate.ddl-auto: create-drop`.

2) Tests y seguridad
- Para permitir tests de controladores que requieren autenticación se agregó la dependencia `spring-security-test` (scope `test`) en el `pom.xml`. Esto habilita anotaciones como `@WithMockUser`.
- En particular, `ForecastControllerTest` se ejecuta con `@WithMockUser(roles = "STAFF")` para probar las validaciones de entrada sin gestionar tokens JWT.

3) Cambios aplicados durante la sesión
- `src/main/resources/data.sql`: añadido `DROP TABLE IF EXISTS forecast_snapshots;` (idempotencia).
- `pom.xml`: añadida dependencia `org.springframework.security:spring-security-test` en scope `test`.
- `src/test/java/.../ForecastControllerTest.java`: ajustado para usar `@WithMockUser` y eliminada una sección de setup accidentalmente copiada.

Si querés, puedo:
- revertir la dependencia `spring-security-test` si preferís no subirla al repo, o moverla a un perfil de desarrollo/test.
- crear un `application-test.yml` con configuración recomendada para tests (si querés que Hibernate cree/limpie el esquema automáticamente).

Si querés que haga alguno de estos pasos adicionales, decímelo y lo aplico.
