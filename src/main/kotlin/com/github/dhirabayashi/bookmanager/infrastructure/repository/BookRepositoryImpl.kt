package com.github.dhirabayashi.bookmanager.infrastructure.repository

import com.github.dhirabayashi.bookmanager.domain.enum.PublishingStatus
import com.github.dhirabayashi.bookmanager.domain.model.Author
import com.github.dhirabayashi.bookmanager.domain.model.Book
import com.github.dhirabayashi.bookmanager.domain.reposiroty.BookRepository
import com.github.dhirabayashi.bookmanager.infrastructure.db.Tables.AUTHORS
import com.github.dhirabayashi.bookmanager.infrastructure.db.Tables.AUTHOR_BOOKS
import com.github.dhirabayashi.bookmanager.infrastructure.db.Tables.BOOKS
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository

@Repository
class BookRepositoryImpl(
    private val dslContext: DSLContext,
) : BookRepository {
    override fun findBooksByAuthorId(authorId: Int): List<Book> {
        // 書籍の一覧
        val books = dslContext.select()
            .from(AUTHOR_BOOKS)
            .join(BOOKS)
            .on(BOOKS.ID.eq(AUTHOR_BOOKS.BOOK_ID))
            .where(AUTHOR_BOOKS.AUTHOR_ID.eq(authorId))
            .fetch()

        // 著者が複数いるかもしれないため、書籍ごとに著者を改めて取得する
        return books.map { book ->
            val authors = dslContext.select()
                .from(AUTHOR_BOOKS)
                .join(AUTHORS)
                .on(AUTHORS.ID.eq(AUTHOR_BOOKS.AUTHOR_ID))
                .where(AUTHOR_BOOKS.BOOK_ID.eq(book.get(BOOKS.ID)))
                .fetch()

            toBookModel(book, authors)
        }
    }

    /**
     * 著者レコードを著者のドメインモデルに変換する
     *
     * @param record 著者レコード
     * @return ドメインモデル
     */
    private fun authorModel(record: Record) = Author(
        id = record.get(AUTHORS.ID),
        name = record.get(AUTHORS.NAME),
        birthDate = record.get(AUTHORS.BIRTH_DATE),
    )

    /**
     * 書籍レコードを書籍のドメインモデルに変換する
     *
     * @param bookRecord 書籍レコード
     * @param authorRecords 著者レコードリスト
     * @return ドメインモデル
     */
    private fun toBookModel(bookRecord: Record, authorRecords: List<Record>) = Book(
        id = bookRecord.get(BOOKS.ID),
        title = bookRecord.get(BOOKS.TITLE),
        price = bookRecord.get(BOOKS.PRICE),
        authors = authorRecords.map { authorModel(it) },
        publishingStatus = PublishingStatus.valueOf(bookRecord.get(BOOKS.PUBLISHING_STATUS)),
    )
}
