package com.github.dhirabayashi.bookmanager.infrastructure.repository

import com.github.dhirabayashi.bookmanager.domain.IdGenerator
import com.github.dhirabayashi.bookmanager.domain.enum.PublishingStatus
import com.github.dhirabayashi.bookmanager.domain.model.Book
import com.github.dhirabayashi.bookmanager.domain.model.DraftBook
import com.github.dhirabayashi.bookmanager.domain.reposiroty.BookRepository
import com.github.dhirabayashi.bookmanager.infrastructure.db.Tables.BOOKS
import com.github.dhirabayashi.bookmanager.infrastructure.db.Tables.BOOK_AUTHORS
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository

@Repository
class BookRepositoryImpl(
    private val create: DSLContext,
    private val idGenerator: IdGenerator,
) : BookRepository {
    override fun findByAuthorId(authorId: String): List<Book> {
        // 書籍の一覧
        val books = create.select(BOOKS.ID, BOOKS.TITLE, BOOKS.PRICE, BOOKS.PUBLISHING_STATUS)
            .from(BOOK_AUTHORS)
            .join(BOOKS)
            .on(BOOKS.ID.eq(BOOK_AUTHORS.BOOK_ID))
            .where(BOOK_AUTHORS.AUTHOR_ID.eq(authorId))
            .orderBy(BOOK_AUTHORS.BOOK_ID)
            .fetch()

        // 著者が複数いるかもしれないため、書籍ごとに著者を改めて取得する
        // これだと遅い場合はCQRSなどを導入して最適化することを検討する
        return books.map { book ->
            val authorIds = selectAuthorIds(book.get(BOOKS.ID))

            toModel(book, authorIds)
        }
    }

    override fun findById(id: String): Book? {
        val bookRecord = create.select(BOOKS.ID, BOOKS.TITLE, BOOKS.PRICE, BOOKS.PUBLISHING_STATUS)
            .from(BOOKS)
            .where(BOOKS.ID.eq(id))
            .fetch()

        // 主キーでの検索なので実際の結果は0件か1件だが、データとしてはリスト
        if (bookRecord.isEmpty()) {
            return null
        }
        // データが2件以上ないかどうか一応チェック（起こらない想定）
        check(bookRecord.size == 1)

        // 著者の一覧
        val authorIds = selectAuthorIds(id)

        // bookRecordの件数はチェック済なので必ず1件
        return toModel(bookRecord.first(), authorIds)
    }

    override fun add(book: DraftBook): Book {
        // 書籍の登録
        val bookId = idGenerator.generate()
        create.insertInto(BOOKS)
            .columns(BOOKS.ID, BOOKS.TITLE, BOOKS.PRICE, BOOKS.PUBLISHING_STATUS)
            .values(bookId, book.title, book.price, book.publishingStatus.name)
            .execute()

        // 書籍と著者のリレーションの登録
        insertBookAuthors(bookId, book.authorIds)

        return Book.create(
            id = bookId,
            title = book.title,
            price = book.price,
            authorIds = book.authorIds,
            publishingStatus = book.publishingStatus,
        )
    }

    override fun update(book: Book): Book? {
        // 書籍の更新
        val updateCount = create.update(BOOKS)
            .set(BOOKS.TITLE, book.title)
            .set(BOOKS.PRICE, book.price)
            .set(BOOKS.PUBLISHING_STATUS, book.publishingStatus.name)
            .where(BOOKS.ID.eq(book.id))
            .execute()

        if (updateCount == 0) {
            return null
        }

        // 書籍と著者のリレーションの更新
        // 同時実行時に不整合が生じるかもしれないが、ユースケース的には実際にそうなる可能性が低いため排他制御は一旦考えない

        // 件数自体変わってるかもしれないため、DELETEとINSERTを使う
        create.delete(BOOK_AUTHORS)
            .where(BOOK_AUTHORS.BOOK_ID.eq(book.id))
            .execute()

        insertBookAuthors(book.id, book.authorIds)

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

    /**
     * 著者IDを取得する
     *
     * @param bookId 対象の書籍IDの
     * @return 著者IDリスト
     */
    private fun selectAuthorIds(bookId: String): List<String> {
        return create.select(BOOK_AUTHORS.AUTHOR_ID)
            .from(BOOK_AUTHORS)
            .where(BOOK_AUTHORS.BOOK_ID.eq(bookId))
            .orderBy(BOOK_AUTHORS.AUTHOR_ID)
            .fetch()
            .map { it.get(BOOK_AUTHORS.AUTHOR_ID) }
    }

    /**
     * 書籍と著者のリレーションを登録する
     *
     * @param bookId 書籍ID
     * @param authorIds 著者IDリスト
     */
    private fun insertBookAuthors(bookId: String, authorIds: List<String>) {
        create.batch(
            authorIds.map { authorId ->
                create.insertInto(BOOK_AUTHORS)
                    .columns(BOOK_AUTHORS.BOOK_ID, BOOK_AUTHORS.AUTHOR_ID)
                    .values(bookId, authorId)
            }
        ).execute()
    }
}
