-- La V1 original no podía aplicarse nunca (los INSERT de usuarios omitían dni y phone, que son NOT NULL),
-- así que no existe ningún entorno con ella aplicada y se corrige in situ en lugar de añadir una V2.
--  * BIGSERIAL: el enunciado pide IDs long incrementales (SERIAL es int4).
--  * Restricciones con nombre: el adaptador de persistencia las traduce a "error validation <campo>".
--  * Se eliminan los usuarios semilla: incumplían las reglas de negocio (nombre > 6, rol 'USER').

CREATE TABLE rooms (
    id   BIGSERIAL    PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT uk_rooms_name UNIQUE (name)
);

CREATE TABLE users (
    id      BIGSERIAL    PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    email   VARCHAR(150) NOT NULL,
    dni     VARCHAR(15)  NOT NULL,
    phone   VARCHAR(15)  NOT NULL,
    role    VARCHAR(20)  NOT NULL,
    room_id BIGINT,
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_dni   UNIQUE (dni),
    CONSTRAINT uk_users_phone UNIQUE (phone),
    CONSTRAINT ck_users_role  CHECK (role IN ('ADMIN', 'SUPERADMIN')),
    CONSTRAINT fk_users_room  FOREIGN KEY (room_id) REFERENCES rooms (id) ON DELETE SET NULL
);

CREATE INDEX idx_users_room_id ON users (room_id);

INSERT INTO rooms (name) VALUES ('Sala 1'), ('Sala 2');
