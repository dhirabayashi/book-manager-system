package com.github.dhirabayashi.bookmanager.infrastructure.repository

import com.github.dhirabayashi.bookmanager.domain.IdGenerator
import com.github.dhirabayashi.bookmanager.domain.enum.PublishingStatus
import com.github.dhirabayashi.bookmanager.domain.model.Author
import com.github.dhirabayashi.bookmanager.domain.model.Book
import com.github.dhirabayashi.bookmanager.domain.reposiroty.AuthorRepository
import com.github.dhirabayashi.bookmanager.domain.reposiroty.BookRepository
import com.github.dhirabayashi.bookmanager.infrastructure.db.Tables.AUTHORS
import com.github.dhirabayashi.bookmanager.infrastructure.db.Tables.AUTHOR_BOOKS
import com.github.dhirabayashi.bookmanager.infrastructure.db.Tables.BOOKS
import com.github.dhirabayashi.bookmanager.test.TestcontainersConfig
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import kotlin.test.Test

@SpringBootTest(classes = [TestcontainersConfig::class])
@Transactional
class BookRepositoryImplTest {

    @Autowired
    private lateinit var create: DSLContext

    @Autowired
    private lateinit var idGenerator: IdGenerator

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    private lateinit var sut: BookRepository

    @BeforeEach
    fun setUp() {
        sut = BookRepositoryImpl(create, idGenerator)
    }

    @Test
    @DisplayName("書籍を登録し、IDで取得できること")
    fun add_find() {
        // テストデータ
        val author = authorRepository.add(Author.create(null, "name", LocalDate.now().minusDays(1)))

        val book = Book.create(
            id = null,
            title = "Kotlinで始めるDDD",
            price = 2200,
            authorIds = listOf(author.id!!),
            publishingStatus = PublishingStatus.UNPUBLISHED
        )

        val saved = sut.add(book)
        val found = sut.findById(saved.id!!)

        assertThat(found).isNotNull
        assertThat(found!!.title).isEqualTo("Kotlinで始めるDDD")
        assertThat(found.authorIds).containsExactly(author.id!!)
    }

    @Test
    @DisplayName("著者に紐づく書籍一覧が取得できること")
    fun findByAuthorId() {
        prepareFindByAuthorIdTestData()

        val books = sut.findByAuthorId("author1")

        assertThat(books).hasSize(2)
        assertThat(books[0].title).isEqualTo("Kotlin Basics")
        assertThat(books[0].authorIds).containsExactly("author1")
        assertThat(books[1].title).isEqualTo("Advanced Kotlin")
        assertThat(books[1].authorIds).containsExactly("author1")
    }

    @Test
    @DisplayName("書籍の更新で、booksとauthor_booksが更新できること")
    fun update() {
        prepareUpdateTestData()

        val updatedBook = Book.create(
            id = "book1",
            title = "Kotlinで学ぶアーキテクチャ",
            price = 2500,
            authorIds = listOf("author3", "author4"),
            publishingStatus = PublishingStatus.PUBLISHED
        )

        // 実行
        val result = sut.update("book1", updatedBook)

        // 検証
        assertThat(result).isNotNull
        assertThat(result!!.title).isEqualTo("Kotlinで学ぶアーキテクチャ")

        val bookRecord = create.select().from(BOOKS).where(BOOKS.ID.eq("book1")).fetchOne()
        assertThat(bookRecord!!.get(BOOKS.TITLE)).isEqualTo("Kotlinで学ぶアーキテクチャ")
        assertThat(bookRecord.get(BOOKS.PRICE)).isEqualTo(2500)

        val authorBooks = create.select().from(AUTHOR_BOOKS).where(AUTHOR_BOOKS.BOOK_ID.eq("book1"))
            .orderBy(AUTHOR_BOOKS.AUTHOR_ID)
            .fetch()
        assertThat(authorBooks.size).isEqualTo(2)
        assertThat(authorBooks[0].get(AUTHOR_BOOKS.AUTHOR_ID)).isEqualTo("author3")
        assertThat(authorBooks[1].get(AUTHOR_BOOKS.AUTHOR_ID)).isEqualTo("author4")

        // 余分なデータが更新されてないかも一応確認
        val anotherBook = create.select().from(BOOKS).where(BOOKS.ID.eq("book2")).fetchOne()
        assertThat(anotherBook!!.get(BOOKS.TITLE)).isNotEqualTo("Kotlinで学ぶアーキテクチャ")
    }

    private fun prepareFindByAuthorIdTestData() {
        create.execute("INSERT INTO authors (id, name, birth_date) VALUES ('author1', 'Author One', '1989-01-31')")
        create.execute("INSERT INTO authors (id, name, birth_date) VALUES ('author2', 'Author Two', '1999-04-30')")

        // 取れる
        create.execute(
            """
            INSERT INTO books (id, title, price, publishing_status)
             VALUES ('book1', 'Kotlin Basics', 2000, 'UNPUBLISHED')
             """
                .trimIndent()
        )
        // 取れる
        create.execute(
            """
            INSERT INTO books (id, title, price, publishing_status)
             VALUES ('book2', 'Advanced Kotlin', 2500, 'PUBLISHED')
             """
                .trimIndent()
        )
        // 取れない
        create.execute(
            """
            INSERT INTO books (id, title, price, publishing_status)
             VALUES ('book3', 'Kotlin Ultimate', 9900, 'PUBLISHED')
             """
                .trimIndent()
        )

        create.execute("INSERT INTO author_books (author_id, book_id) VALUES ('author1', 'book1')")
        create.execute("INSERT INTO author_books (author_id, book_id) VALUES ('author1', 'book2')")
        create.execute("INSERT INTO author_books (author_id, book_id) VALUES ('author2', 'book3')")
    }

    private fun prepareUpdateTestData() {
        create.insertInto(AUTHORS)
            .columns(AUTHORS.ID, AUTHORS.NAME, AUTHORS.BIRTH_DATE)
            .values("author1", "Author1", LocalDate.now().minusDays(1))
            .values("author2", "Author2", LocalDate.now().minusDays(1))
            .values("author3", "Author3", LocalDate.now().minusDays(1))
            .values("author4", "Author4", LocalDate.now().minusDays(1))
            .execute()

        create.insertInto(BOOKS)
            .columns(BOOKS.ID, BOOKS.TITLE, BOOKS.PRICE, BOOKS.PUBLISHING_STATUS)
            .values("book1", "Kotlinで始めるDDD", 2200, "UNPUBLISHED")
            .values("book2", "Kotlinで始める単体テスト", 1400, "UNPUBLISHED")
            .execute()

        create.insertInto(AUTHOR_BOOKS)
            .columns(AUTHOR_BOOKS.AUTHOR_ID, AUTHOR_BOOKS.BOOK_ID)
            .values("author1", "book1")
            .values("author2", "book1")
            .execute()
    }
}
