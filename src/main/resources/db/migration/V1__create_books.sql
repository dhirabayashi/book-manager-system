CREATE TABLE IF NOT EXISTS books (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    price INTEGER NOT NULL,
    status VARCHAR(10) NOT NULL CHECK (status IN ('未出版', '出版済み'))
);
