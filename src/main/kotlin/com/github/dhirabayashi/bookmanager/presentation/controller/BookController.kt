package com.github.dhirabayashi.bookmanager.presentation.controller

import com.github.dhirabayashi.bookmanager.application.service.BookService
import com.github.dhirabayashi.bookmanager.domain.model.Book
import com.github.dhirabayashi.bookmanager.presentation.form.BookCreateRequest
import com.github.dhirabayashi.bookmanager.presentation.form.BookListResponse
import com.github.dhirabayashi.bookmanager.presentation.form.BookResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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

    /**
     * 書籍を登録する
     *
     * @param paramBook 登録する書籍
     * @return 登録された書籍
     */
    @PostMapping
    fun postBook(@RequestBody paramBook: BookCreateRequest): BookResponse {
        val book = Book.create(
            title = paramBook.title,
            price = paramBook.price,
            authorIds = paramBook.authorIds,
            publishingStatus = paramBook.publishingStatus.toModel(),
        )

        val created = bookService.save(book)

        return BookResponse.of(created)
    }
}
