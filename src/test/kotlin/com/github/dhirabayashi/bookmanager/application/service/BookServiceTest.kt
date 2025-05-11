package com.github.dhirabayashi.bookmanager.application.service

import com.github.dhirabayashi.bookmanager.application.exception.EntityNotFoundException
import com.github.dhirabayashi.bookmanager.domain.check.ValidationException
import com.github.dhirabayashi.bookmanager.domain.enum.PublishingStatus
import com.github.dhirabayashi.bookmanager.domain.model.Author
import com.github.dhirabayashi.bookmanager.domain.model.Book
import com.github.dhirabayashi.bookmanager.domain.reposiroty.AuthorRepository
import com.github.dhirabayashi.bookmanager.domain.reposiroty.BookRepository
import com.github.dhirabayashi.bookmanager.domain.service.BookDomainService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class BookServiceTest {

    @Mock
    private lateinit var bookRepository: BookRepository

    @Mock
    private lateinit var authorRepository: AuthorRepository

    @Mock
    private lateinit var bookDomainService: BookDomainService

    @InjectMocks
    private lateinit var bookService: BookService

    @Test
    @DisplayName("著者に紐づく書籍一覧を取得できること")
    fun retrieveBooksByAuthorId() {
        // テストデータ準備
        val books = createTestBooks()
        val bookOneAuthors = createTestAuthors(listOf("author1"))
        val bookTwoAuthors = createTestAuthors(listOf("author1", "author2"))

        // モック設定
        whenever(bookRepository.findByAuthorId("author1")).thenReturn(books)
        whenever(authorRepository.findByIds(listOf("author1"))).thenReturn(bookOneAuthors)
        whenever(authorRepository.findByIds(listOf("author1", "author2"))).thenReturn(bookTwoAuthors)

        // 実行
        val result = bookService.retrieveBooksByAuthorId("author1")

        assertThat(result).hasSize(2)

        // アサーション
        assertBook(result[0], books[0])
        assertBook(result[1], books[1])

        assertAuthor(result[0].authors[0], bookOneAuthors[0])
        assertAuthor(result[1].authors[0], bookTwoAuthors[0])
        assertAuthor(result[1].authors[1], bookTwoAuthors[1])

        verify(bookRepository, times(1)).findByAuthorId("author1")
        verify(authorRepository, times(1)).findByIds(listOf("author1"))
        verify(authorRepository, times(1)).findByIds(listOf("author1", "author2"))
    }


    @Test
    @DisplayName("書籍を登録できること")
    fun addBook() {
        // テストデータ準備
        val book = createTestBooks().first()
        val createdBook = Book.create(
            id = "newId",
            title = book.title,
            price = book.price,
            authorIds = book.authorIds,
            publishingStatus = book.publishingStatus
        )

        val authors = createTestAuthors(book.authorIds)

        // モック設定
        whenever(bookRepository.add(book)).thenReturn(createdBook)
        whenever(authorRepository.findByIds(book.authorIds)).thenReturn(authors)

        // 実行
        val result = bookService.add(book)

        // 検証
        assertBook(result, createdBook)

        assertThat(result.authors).hasSize(authors.size)
        result.authors.forEachIndexed { index, authorDto ->
            assertAuthor(authorDto, authors[index])
        }

        verify(bookRepository, times(1)).add(book)
        verify(authorRepository, times(1)).findByIds(book.authorIds)
    }

    @Test
    @DisplayName("ID未指定で更新を実行した場合は例外が投げられること")
    fun updateBook_shouldThrowExceptionWhenIdIsNull() {
        val book = Book.create(null, "Test Book", 1000, listOf("author1"), PublishingStatus.UNPUBLISHED)

        assertThatThrownBy { bookService.update(book) }
            .isInstanceOf(ValidationException::class.java)
    }

    @Test
    @DisplayName("存在しないIDで更新を実行した場合は例外が投げられること")
    fun updateBook_shouldThrowExceptionWhenBookNotFound() {
        val book = Book.create("nonexistence", "Test Book", 1000, listOf("author1"), PublishingStatus.UNPUBLISHED)

        whenever(bookRepository.findById(book.id!!)).thenReturn(null)

        assertThatThrownBy { bookService.update(book) }
            .isInstanceOf(EntityNotFoundException::class.java)

        verify(bookRepository, times(1)).findById(book.id!!)
    }

    @Test
    @DisplayName("更新実行時、更新データの整合性チェック処理が呼ばれていること")
    fun updateBook_shouldCallCanUpdateBook() {
        val book = createTestBooks().first()
        val currentBook = book
        val authors = createTestAuthors(book.authorIds)

        whenever(bookRepository.findById(book.id!!)).thenReturn(currentBook)
        whenever(bookDomainService.canUpdateBook(currentBook, book)).thenReturn(true)
        whenever(bookRepository.update(book)).thenReturn(book)
        whenever(authorRepository.findByIds(book.authorIds)).thenReturn(authors)

        // 実行
        bookService.update(book)

        verify(bookDomainService, times(1)).canUpdateBook(currentBook, book)
    }

    @Test
    @DisplayName("書籍を更新できること")
    fun updateBook_shouldUpdateBook() {
        val book = createTestBooks().last()
        val currentBook = Book.create(
            id = book.id,
            title = "currentTitle",
            price = 99999,
            authorIds = listOf("currentAuthor"),
            publishingStatus = PublishingStatus.UNPUBLISHED,
        )

        whenever(bookRepository.findById(book.id!!)).thenReturn(currentBook)
        whenever(bookDomainService.canUpdateBook(currentBook, book)).thenReturn(true)
        whenever(bookRepository.update(book)).thenReturn(book)

        val authors = createTestAuthors(book.authorIds)
        whenever(authorRepository.findByIds(book.authorIds)).thenReturn(authors)

        // 実行
        val result = bookService.update(book)

        assertBook(result, book)

        assertThat(authors).hasSize(authors.size)
        result.authors.forEachIndexed { index, authorDto ->
            assertAuthor(authorDto, authors[index])
        }

        verify(bookRepository, times(1)).findById(book.id!!)
        verify(bookDomainService, times(1)).canUpdateBook(currentBook, book)
        verify(bookRepository, times(1)).update(book)
        verify(authorRepository, times(1)).findByIds(book.authorIds)
    }

    private fun createTestBooks(): List<Book> = listOf(
        Book.create(
            id = "book1",
            title = "Test Book",
            price = 1000,
            authorIds = listOf("author1"),
            publishingStatus = PublishingStatus.UNPUBLISHED
        ),
        Book.create(
            id = "book2",
            title = "Test Book2",
            price = 1200,
            authorIds = listOf("author1", "author2"),
            publishingStatus = PublishingStatus.PUBLISHED
        )
    )

    private fun createTestAuthors(ids: List<String>): List<Author> = ids.mapIndexed { i, id ->
        Author.create(
            id = id,
            name = "Author $id",
            birthDate = LocalDate.of(1990 + i, 1, 1)
        )
    }

    private fun assertBook(actual: BookWithAuthorsDto, expected: Book) {
        assertThat(actual.id).isEqualTo(expected.id)
        assertThat(actual.title).isEqualTo(expected.title)
        assertThat(actual.price).isEqualTo(expected.price)
        assertThat(actual.publishingStatus).isEqualTo(expected.publishingStatus)
        assertThat(actual.authors.map { it.id }).isEqualTo(expected.authorIds)
    }

    private fun assertAuthor(actual: AuthorDto, expected: Author) {
        assertThat(actual.id).isEqualTo(expected.id)
        assertThat(actual.name).isEqualTo(expected.name)
        assertThat(actual.birthDate).isEqualTo(expected.birthDate)
    }
}
