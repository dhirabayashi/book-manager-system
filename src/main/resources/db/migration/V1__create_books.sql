CREATE TABLE IF NOT EXISTS books (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    price INTEGER NOT NULL,
    publishing_status VARCHAR(10) NOT NULL CHECK (publishing_status IN ('UNPUBLISHED', 'PUBLISHED'))
);
