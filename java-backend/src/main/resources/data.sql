DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS persons;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS rooms;

CREATE TABLE persons (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL
);

CREATE TABLE items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    available BOOLEAN NOT NULL
);

CREATE TABLE rooms (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    capacity INT NOT NULL
);

CREATE TABLE reservations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    item_id BIGINT,
    room_id BIGINT,
    person_id BIGINT NOT NULL,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    CONSTRAINT fk_reservation_person FOREIGN KEY (person_id) REFERENCES persons(id),
    CONSTRAINT fk_reservation_item FOREIGN KEY (item_id) REFERENCES items(id),
    CONSTRAINT fk_reservation_room FOREIGN KEY (room_id) REFERENCES rooms(id)
);

CREATE TABLE forecast_snapshots (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    series_id VARCHAR(255) NOT NULL,
    resource_type VARCHAR(64) NOT NULL,
    resource_id BIGINT NOT NULL,
    generated_at TIMESTAMP NOT NULL,
    horizon_days INT NOT NULL,
    metrics_json CLOB NOT NULL,
    points_json CLOB NOT NULL
);

INSERT INTO persons (id, full_name, email, password_hash, role) VALUES
  (1, 'Ana Perez', 'ana.perez@organizacion.com', 'RAW:AnaSecure1!', 'STAFF'),
  (2, 'Juan Gomez', 'juan.gomez@organizacion.com', 'RAW:JuanSecure1!', 'STAFF'),
  (3, 'Maria Lopez', 'maria.lopez@organizacion.com', 'RAW:AdminSecure1!', 'ADMIN');
ALTER TABLE persons ALTER COLUMN id RESTART WITH 4;

INSERT INTO items (id, name, available) VALUES
  (1, 'Proyector Epson EB-X05', TRUE),
  (2, 'Laptop HP EliteBook', FALSE),
  (3, 'Camara Sony Alpha a6400', TRUE);
ALTER TABLE items ALTER COLUMN id RESTART WITH 4;

INSERT INTO rooms (id, name, capacity) VALUES
  (1, 'Sala de Reuniones 1A', 8),
  (2, 'Sala de Conferencias B2', 20),
  (3, 'Aula de Capacitacion C3', 15);
ALTER TABLE rooms ALTER COLUMN id RESTART WITH 4;

INSERT INTO reservations (id, item_id, room_id, person_id, start_date_time, end_date_time) VALUES
  (1, 1, NULL, 1, '2025-09-11T10:00:00', '2025-09-11T11:00:00'),
  (2, NULL, 2, 2, '2025-09-12T14:00:00', '2025-09-12T16:00:00'),
  (3, 2, NULL, 3, '2025-09-13T09:00:00', '2025-09-13T10:00:00');
ALTER TABLE reservations ALTER COLUMN id RESTART WITH 4;

ALTER TABLE forecast_snapshots ALTER COLUMN id RESTART WITH 1;
