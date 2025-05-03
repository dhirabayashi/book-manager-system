CREATE TABLE IF NOT EXISTS author_books (
    book_id INTEGER NOT NULL REFERENCES books(id),
    author_id INTEGER NOT NULL REFERENCES authors(id),
    PRIMARY KEY (book_id, author_id)
);

CREATE INDEX idx_author_books_book_id ON author_books(book_id);
CREATE INDEX idx_author_books_author_id ON author_books(author_id);
