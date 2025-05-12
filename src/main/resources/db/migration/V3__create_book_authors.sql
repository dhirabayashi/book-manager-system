CREATE TABLE IF NOT EXISTS book_authors (
    book_id VARCHAR(26) NOT NULL REFERENCES books(id) DEFERRABLE INITIALLY DEFERRED,
    author_id VARCHAR(26) NOT NULL REFERENCES authors(id) DEFERRABLE INITIALLY DEFERRED,
    PRIMARY KEY (book_id, author_id)
);

CREATE INDEX idx_book_authors_book_id ON book_authors(book_id);
CREATE INDEX idx_book_authors_author_id ON book_authors(author_id);
