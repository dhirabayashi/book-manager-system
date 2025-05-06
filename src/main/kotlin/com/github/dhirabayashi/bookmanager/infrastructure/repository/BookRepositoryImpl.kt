package com.github.dhirabayashi.bookmanager.infrastructure.repository

import com.github.dhirabayashi.bookmanager.domain.enum.PublishingStatus
import com.github.dhirabayashi.bookmanager.domain.model.Book
import com.github.dhirabayashi.bookmanager.domain.reposiroty.BookRepository
import com.github.dhirabayashi.bookmanager.infrastructure.db.Tables.AUTHOR_BOOKS
import com.github.dhirabayashi.bookmanager.infrastructure.db.Tables.BOOKS
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository

@Repository
class BookRepositoryImpl(
    private val dslContext: DSLContext,
) : BookRepository {
    override fun findByAuthorId(authorId: String): List<Book> {
        // 書籍の一覧
        val books = dslContext.select()
            .from(AUTHOR_BOOKS)
            .join(BOOKS)
            .on(BOOKS.ID.eq(AUTHOR_BOOKS.BOOK_ID))
            .where(AUTHOR_BOOKS.AUTHOR_ID.eq(authorId))
            .fetch()

        // 著者が複数いるかもしれないため、書籍ごとに著者を改めて取得する
        // これだと遅い場合はCQRSなどを導入して最適化することを検討する
        return books.map { book ->
            val authorIds = dslContext.select()
                .from(AUTHOR_BOOKS)
                .where(AUTHOR_BOOKS.BOOK_ID.eq(book.get(BOOKS.ID)))
                .fetch()
                .map { it.get(AUTHOR_BOOKS.AUTHOR_ID) }

            toModel(book, authorIds)
        }
    }

    /**
     * 書籍レコードを書籍のドメインモデルに変換する
     *
     * @param bookRecord 書籍レコード
     * @param authorIds 著者IDリスト
     * @return ドメインモデル
     */
    private fun toModel(bookRecord: Record, authorIds: List<String>) = Book(
        id = bookRecord.get(BOOKS.ID),
        title = bookRecord.get(BOOKS.TITLE),
        price = bookRecord.get(BOOKS.PRICE),
        authorIds = authorIds,
        publishingStatus = PublishingStatus.valueOf(bookRecord.get(BOOKS.PUBLISHING_STATUS)),
    )
}
