CREATE TABLE users (
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL,
    whatsapp      VARCHAR(20)  NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    role          VARCHAR(10)  NOT NULL DEFAULT 'USER',
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT ck_users_role CHECK (role IN ('USER','ADMIN'))
);

CREATE TABLE courts (
    id         BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code       VARCHAR(10) NOT NULL,
    name       VARCHAR(50) NOT NULL,
    court_type VARCHAR(20) NOT NULL,
    CONSTRAINT uq_courts_code UNIQUE (code),
    CONSTRAINT ck_courts_type CHECK (court_type IN ('INDOOR','OUTDOOR'))
);

CREATE TABLE bookings (
    id             BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT      NOT NULL,
    court_id       BIGINT      NOT NULL,
    play_date      DATE        NOT NULL,
    start_hour     TINYINT     NOT NULL,
    duration_hours TINYINT     NOT NULL DEFAULT 1,
    status         VARCHAR(10) NOT NULL DEFAULT 'HOLD',
    payment_method VARCHAR(10) NULL,
    total_price    INT         NOT NULL,
    expires_at     TIMESTAMP   NULL,
    paid_at        TIMESTAMP   NULL,
    created_at     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_user  FOREIGN KEY (user_id)  REFERENCES users (id),
    CONSTRAINT fk_bookings_court FOREIGN KEY (court_id) REFERENCES courts (id),
    CONSTRAINT ck_bookings_status CHECK (status IN ('HOLD','PENDING','PAID','FAILED','EXPIRED','CANCELLED')),
    CONSTRAINT ck_bookings_payment CHECK (payment_method IS NULL OR payment_method IN ('QRIS','VA','EWALLET'))
);

CREATE TABLE booking_slots (
    id         BIGINT  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT  NOT NULL,
    court_id   BIGINT  NOT NULL,
    play_date  DATE    NOT NULL,
    slot_hour  TINYINT NOT NULL,
    CONSTRAINT uq_slot_court_date_hour UNIQUE (court_id, play_date, slot_hour),
    CONSTRAINT fk_slots_booking FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE CASCADE,
    CONSTRAINT fk_slots_court   FOREIGN KEY (court_id)   REFERENCES courts (id)
);

INSERT INTO courts (code, name, court_type) VALUES
    ('COURT-001', 'Bandeja', 'INDOOR'),
    ('COURT-002', 'Vibora',  'INDOOR'),
    ('COURT-003', 'Smash',   'OUTDOOR');