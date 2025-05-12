package com.github.dhirabayashi.bookmanager.application.service

import com.github.dhirabayashi.bookmanager.application.exception.EntityNotFoundException
import com.github.dhirabayashi.bookmanager.domain.check.ValidationException
import com.github.dhirabayashi.bookmanager.domain.check.validate
import com.github.dhirabayashi.bookmanager.domain.model.Author
import com.github.dhirabayashi.bookmanager.domain.model.Book
import com.github.dhirabayashi.bookmanager.domain.model.DraftBook
import com.github.dhirabayashi.bookmanager.domain.reposiroty.AuthorRepository
import com.github.dhirabayashi.bookmanager.domain.reposiroty.BookRepository
import com.github.dhirabayashi.bookmanager.domain.service.BookDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 書籍のユースケース
 */
@Service
class BookService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
    private val bookDomainService: BookDomainService,
) {
    /**
     * 著者に紐づく書籍の一覧を取得する
     * @param authorId 検索キーとなる著者ID
     * @return 書籍の一覧
     */
    @Transactional(readOnly = true)
    fun retrieveBooksByAuthorId(authorId: String): List<BookWithAuthorsDto> {
        // 書籍の取得
        val books = bookRepository.findByAuthorId(authorId)

        // 書籍ごとに著者の情報も取得して返す（これだと遅い場合はCQRSなどを導入して最適化することを検討する）
        return books.map { book ->
            val authors = authorRepository.findByIds(book.authorIds)

            BookWithAuthorsDto(
                id = book.id,
                title = book.title,
                price = book.price,
                authors = authors.map { AuthorDto.of(it) },
                publishingStatus = book.publishingStatus,
            )
        }
    }

    /**
     * 書籍を登録する
     *
     * @param book 登録する書籍
     * @return 登録された書籍
     * @throws ValidationException 存在しない著者が指定された場合
     */
    @Transactional(rollbackFor = [Exception::class])
    fun add(book: DraftBook): BookWithAuthorsDto {
        // 著者を取得
        val authors = findAuthorsWithCheck(book.authorIds)

        // 書籍を登録
        val createdBook = bookRepository.add(book)

        return BookWithAuthorsDto(
            id = createdBook.id,
            title = createdBook.title,
            price = createdBook.price,
            authors = authors.map { AuthorDto.of(it) },
            publishingStatus = createdBook.publishingStatus,
        )
    }

    /**
     * 書籍を更新する
     *
     * @param book 更新する書籍
     * @return 更新された書籍
     * @throws EntityNotFoundException IDに対する書籍が存在しない場合
     * @throws ValidationException 出版済み書籍を未出版に更新しようとした場合、存在しない著者が指定された場合
     */
    @Transactional(rollbackFor = [Exception::class])
    fun update(book: Book): BookWithAuthorsDto {
        // 出版ステータスのチェック
        val currentBook = bookRepository.findById(book.id)
            ?: throw EntityNotFoundException("書籍", book.id)

        validate(bookDomainService.canUpdateBook(currentBook, book)) {
            "出版済みの書籍を未出版に更新することはできません"
        }

        // 著者を取得
        val authors = findAuthorsWithCheck(book.authorIds)

        // 書籍を更新
        val updatedBook = bookRepository.update(book)
            ?: throw EntityNotFoundException("書籍", book.id)

        return BookWithAuthorsDto(
            id = updatedBook.id,
            title = updatedBook.title,
            price = updatedBook.price,
            authors = authors.map { AuthorDto.of(it) },
            publishingStatus = updatedBook.publishingStatus,
        )
    }

    /**
     * 著者を取得する。
     *
     * @param authorIds 取得対象の著者IDリスト
     * @return 著者
     * @throws ValidationException 存在しない著者が指定された場合
     */
    private fun findAuthorsWithCheck(authorIds: List<String>): List<Author> {
        // 著者を取得して返す（著者も取得したほうがAPIとして使いやすいかもしれず、また余分なレスポンス用クラスを作らなくて済む）
        val authors = authorRepository.findByIds(authorIds)

        // 存在しない著者が指定されていたらエラー
        validate(authors.size == authorIds.size) {
            val notExistentAuthorIds = (authorIds - authors.map { it.id }.toSet()).joinToString(", ")
            "著者が存在しません: id[$notExistentAuthorIds]"
        }

        return authors
    }
}
