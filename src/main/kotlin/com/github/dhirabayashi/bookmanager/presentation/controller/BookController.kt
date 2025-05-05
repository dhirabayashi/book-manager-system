package com.github.dhirabayashi.bookmanager.presentation.controller

import com.github.dhirabayashi.bookmanager.application.service.BookService
import com.github.dhirabayashi.bookmanager.presentation.form.BookListResponse
import com.github.dhirabayashi.bookmanager.presentation.form.BookResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 書籍コントローラー
 *
 * @property bookService 書籍のユースケース
 */
@RestController
@RequestMapping("books")
class BookController(
    private val bookService: BookService,
) {
    /**
     * 著者IDをもとに書籍の一覧を取得する
     *
     * @param authorId 著者ID
     * @return 書籍の一覧レスポンス
     */
    @GetMapping("/author_id/{author_id}")
    fun getBooksByAuthorId(@PathVariable("author_id") authorId: String): BookListResponse {
        val bookList = bookService.retrieveBooksByAuthorId(authorId)
            .map { BookResponse.of(it) }

        return BookListResponse(bookList)
    }
}
