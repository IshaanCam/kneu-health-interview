CREATE TABLE medications (
    id     BIGSERIAL    PRIMARY KEY,
    name   VARCHAR(255) NOT NULL,
    dosage VARCHAR(255) NOT NULL
);

CREATE TABLE prescriptions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    time CHAR(5) NOT NULL, 
    medicine_id BIGINT NOT NULL, 
    FOREIGN KEY (medicine_id)
    REFERENCES medications(id)
);

INSERT INTO medications (id, name, dosage) VALUES
    (1, 'Aspirin',     '100mg'),
    (2, 'Metformin',   '500mg'),
    (3, 'Lisinopril',  '10mg'),
    (4, 'Amoxicillin', '250mg'),
    (5, 'Omeprazole',  '20mg');

ALTER SEQUENCE medications_id_seq RESTART WITH 6;
