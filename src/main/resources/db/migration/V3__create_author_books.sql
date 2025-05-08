CREATE TABLE IF NOT EXISTS author_books (
    book_id VARCHAR(26) NOT NULL REFERENCES books(id) DEFERRABLE INITIALLY DEFERRED,
    author_id VARCHAR(26) NOT NULL REFERENCES authors(id) DEFERRABLE INITIALLY DEFERRED,
    PRIMARY KEY (book_id, author_id)
);

CREATE INDEX idx_author_books_book_id ON author_books(book_id);
CREATE INDEX idx_author_books_author_id ON author_books(author_id);
