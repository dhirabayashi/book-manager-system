package com.github.dhirabayashi.bookmanager.application.service

import com.github.dhirabayashi.bookmanager.domain.model.Book
import com.github.dhirabayashi.bookmanager.domain.reposiroty.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 書籍のユースケース
 */
@Service
class BookService(
    private val authorBooksRepository: BookRepository,
) {
    /**
     * 著者に紐づく書籍の一覧を取得する
     * @param authorId 検索キーとなる著者ID
     * @return 書籍の一覧
     */
    @Transactional(readOnly = true)
    fun retrieveBooksByAuthorId(authorId: String): List<Book> {
        return authorBooksRepository.findBooksByAuthorId(authorId)
    }
}
