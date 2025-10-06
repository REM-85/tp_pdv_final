# Plataforma de Gestion de Reservas (MVP 2025 2C)

Este repositorio contiene el backend Java y el microservicio de prediccion en Python para el TP integrador de Programacion de Vanguardia 2025 2C.

## Estructura

- `java-backend/`: API REST (Spring Boot) con CRUD, disponibilidad, historial y consumo del predictor.
- `py-forecast/`: Servicio FastAPI con endpoints `/train` y `/predict`, mas logica de seleccion automatica (baseline vs SARIMAX).
- `docs/`: Arquitectura, contrato de endpoints y guia para la demo/presentacion.
- `postman/`: Coleccion de requests para validar el flujo extremo a extremo.
- `data/`: Datasets de ejemplo para pruebas rapidas.

## Requisitos

- Java 17 y Maven 3.9+
- Python 3.11 con pip
- Docker / Docker Compose (opcional para levantar todo junto)

### Guía rápida para equipos con Windows

1. **Instalar dependencias básicas**
   - [Git for Windows](https://git-scm.com/download/win) (incluye Git Bash para usar los comandos tal como figuran aquí).
   - [Java 17 JDK](https://adoptium.net/) (Temurin u otra distribución) y agregar `JAVA_HOME`/`bin` al `PATH`.
   - [Apache Maven 3.9+](https://maven.apache.org/download.cgi); descomprimir y añadir la carpeta `bin` al `PATH`.
   - [Python 3.11](https://www.python.org/downloads/windows/) marcando la opción **Add python.exe to PATH**.
   - **Opcional:** [Docker Desktop](https://www.docker.com/products/docker-desktop/) para ejecutar todo con `docker compose`.

2. **Clonar el repositorio** (PowerShell o Git Bash):
   ```bash
   git clone https://github.com/<organizacion>/tp_pdv_final.git
   cd tp_pdv_final
   ```

3. **Levantar el backend Java**:
   ```bash
   cd java-backend
   mvn clean package
   java -jar target/reservas-backend-0.0.1-SNAPSHOT.jar
   ```
   El servicio queda disponible en `http://localhost:8080` y la consola H2 en `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:reservas`, usuario `sa`, sin contraseña).

4. **Levantar el predictor Python** (en otra ventana):
   ```bash
   cd py-forecast
   py -3.11 -m venv .venv
   .venv\Scripts\activate
   pip install -r requirements.txt
   uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
   ```
   El endpoint queda disponible en `http://localhost:8000/docs` para pruebas rápidas.

5. **O bien ejecutar todo con Docker Desktop**:
   ```bash
   cd tp_pdv_final
   docker compose up --build
   ```
   Asegurarse de que Docker Desktop esté corriendo antes de ejecutar el comando.

## Como correr solo el backend Java

```bash
cd java-backend
mvn clean package
java -jar target/reservas-backend-0.0.1-SNAPSHOT.jar
```

## Como correr solo el predictor en Python

```bash
cd py-forecast
python -m venv .venv
source .venv/bin/activate  # En Windows usar .venv\Scripts\activate
pip install -r requirements.txt
uvicorn app.main:app --reload
```

## Como correr usando Docker Compose

```bash
docker compose up --build
```

El backend quedara expuesto en `http://localhost:8080` y el predictor en `http://localhost:8000`.

## Tests

```bash
cd java-backend
mvn test

cd ../py-forecast
pytest
```

## Demo rapida / Postman

Importar `postman/Reserva-Predictor.postman_collection.json` y seguir el flujo:
1. Crear persona/sala/articulo.
2. Registrar reservas.
3. Consultar disponibilidad (`GET /api/reservations/availability`).
4. Obtener historial (`GET /api/reservations/history`).
5. Entrenar predictor (`POST /api/forecast/train`).
6. Solicitar pronostico (`POST /api/forecast`).

## Datos de ejemplo

- `java-backend/src/main/resources/data.sql`: seed inicial para H2.
- `py-forecast/data/sample_series.json`: serie sintetica con tendencia + estacionalidad semanal.

## Documentacion

- `docs/arquitectura.md`: diagrama ASCII/mermaid y decisiones tecnicas.
- `docs/endpoints.md`: contrato REST resumido.
- `docs/presentacion-outline.md`: guion sugerido para la defensa.
