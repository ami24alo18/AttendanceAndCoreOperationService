
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS vector;


CREATE TABLE IF NOT EXISTS employee
(
    id              UUID PRIMARY KEY,
    tenant_id       VARCHAR(255) NOT NULL,
    first_name      VARCHAR(255) NOT NULL,
    last_name       VARCHAR(255) NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    phone_number    VARCHAR(20),
    hire_date       DATE,
    job_title       VARCHAR(255),
    department      VARCHAR(255),
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS employee_biometric
(
    id                  UUID PRIMARY KEY,
    employee_id         UUID         NOT NULL,
    tenant_id           VARCHAR(255) NOT NULL,
    face_embedding      VECTOR(512),
    is_active           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employee (id)
);


CREATE TABLE IF NOT EXISTS attendance_log
(
    id              UUID PRIMARY KEY,
    employee_id     UUID         NOT NULL,
    tenant_id       VARCHAR(255) NOT NULL,
    check_in_time   TIMESTAMP WITH TIME ZONE,
    check_out_time  TIMESTAMP WITH TIME ZONE,
    log_date        DATE         NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employee (id)
);


CREATE INDEX IF NOT EXISTS idx_employee_biometric_face_embedding ON employee_biometric USING ivfflat (face_embedding vector_cosine_ops) WITH (lists = 100);
CREATE INDEX IF NOT EXISTS idx_attendance_log_log_date ON attendance_log(log_date);
CREATE INDEX IF NOT EXISTS idx_employee_tenant_id ON employee(tenant_id);
CREATE INDEX IF NOT EXISTS idx_employee_biometric_tenant_id ON employee_biometric(tenant_id);
CREATE INDEX IF NOT EXISTS idx_attendance_log_tenant_id ON attendance_log(tenant_id);
