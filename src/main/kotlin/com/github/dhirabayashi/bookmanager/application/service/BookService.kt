package com.github.dhirabayashi.bookmanager.application.service

import com.github.dhirabayashi.bookmanager.domain.reposiroty.AuthorRepository
import com.github.dhirabayashi.bookmanager.domain.reposiroty.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 書籍のユースケース
 */
@Service
class BookService(
    private val booksRepository: BookRepository,
    private val authorRepository: AuthorRepository,
) {
    /**
     * 著者に紐づく書籍の一覧を取得する
     * @param authorId 検索キーとなる著者ID
     * @return 書籍の一覧
     */
    @Transactional(readOnly = true)
    fun retrieveBooksByAuthorId(authorId: String): List<BookWithAuthorsDto> {
        // 書籍の取得
        val books = booksRepository.findByAuthorId(authorId)

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
}
