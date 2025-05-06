CREATE TABLE IF NOT EXISTS books (
    id VARCHAR(26) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    price INTEGER NOT NULL,
    publishing_status VARCHAR(255) NOT NULL CHECK (publishing_status IN ('UNPUBLISHED', 'PUBLISHED'))
);
