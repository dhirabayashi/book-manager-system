package com.github.dhirabayashi.bookmanager.infrastructure.repository

import com.github.dhirabayashi.bookmanager.domain.IdGenerator
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
    private val idGenerator: IdGenerator,
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

    override fun findById(id: String): Book? {
        val bookRecord = dslContext.select()
            .from(BOOKS)
            .where(BOOKS.ID.eq(id))
            .fetch()

        // 主キーでの検索なので実際の結果は0件か1件だが、データとしてはリスト
        if (bookRecord.isEmpty()) {
            return null
        }
        // データが2件以上ないかどうか一応チェック（起こらない想定）
        check(bookRecord.size != 1)

        // 著者の一覧
        val authorIds = dslContext.select()
            .from(AUTHOR_BOOKS)
            .where(AUTHOR_BOOKS.BOOK_ID.eq(id))
            .fetch()
            .map { it.get(AUTHOR_BOOKS.AUTHOR_ID) }

        // bookRecordの件数はチェック済なので必ず1件
        return toModel(bookRecord.first(), authorIds)
    }

    override fun add(book: Book): Book {
        // 書籍の登録
        val bookId = book.id ?: idGenerator.generate()
        dslContext.insertInto(BOOKS)
            .columns(BOOKS.ID, BOOKS.TITLE, BOOKS.PRICE, BOOKS.PUBLISHING_STATUS)
            .values(bookId, book.title, book.price, book.publishingStatus.name)
            .execute()

        // 書籍と著者のリレーションの登録
        dslContext.batch(
            book.authorIds.map { authorId ->
                dslContext.insertInto(AUTHOR_BOOKS)
                    .columns(AUTHOR_BOOKS.AUTHOR_ID, AUTHOR_BOOKS.BOOK_ID)
                    .values(authorId, bookId)
            }
        ).execute()

        return Book.create(
            id = bookId,
            title = book.title,
            price = book.price,
            authorIds = book.authorIds,
            publishingStatus = book.publishingStatus,
        )
    }

    override fun update(bookId: String, book: Book): Book? {
        // 書籍の更新
        val updateCount = dslContext.update(BOOKS)
            .set(BOOKS.TITLE, book.title)
            .set(BOOKS.PRICE, book.price)
            .set(BOOKS.PUBLISHING_STATUS, book.publishingStatus.name)
            .where(BOOKS.ID.eq(bookId))
            .execute()

        if (updateCount == 0) {
            return null
        }

        // 著者と書籍のリレーションの更新
        // 同時実行時に不整合が生じるかもしれないが、ユースケース的には実際にそうなる可能性が低いため排他制御は一旦考えない

        // 件数自体変わってるかもしれないため、DELETEとINSERTを使う
        dslContext.delete(AUTHOR_BOOKS)
            .where(AUTHOR_BOOKS.BOOK_ID.eq(bookId))
            .execute()

        dslContext.batch(
            book.authorIds.map { authorId ->
                dslContext.insertInto(AUTHOR_BOOKS)
                    .columns(AUTHOR_BOOKS.AUTHOR_ID, AUTHOR_BOOKS.BOOK_ID)
                    .values(authorId, bookId)
            }
        ).execute()

        return book
    }

    /**
     * 書籍レコードを書籍のドメインモデルに変換する
     *
     * @param bookRecord 書籍レコード
     * @param authorIds 著者IDリスト
     * @return ドメインモデル
     */
    private fun toModel(bookRecord: Record, authorIds: List<String>) = Book.create(
        id = bookRecord.get(BOOKS.ID),
        title = bookRecord.get(BOOKS.TITLE),
        price = bookRecord.get(BOOKS.PRICE),
        authorIds = authorIds,
        publishingStatus = PublishingStatus.valueOf(bookRecord.get(BOOKS.PUBLISHING_STATUS)),
    )
}
