package com.github.dhirabayashi.bookmanager.presentation.controller

import com.github.dhirabayashi.bookmanager.application.service.BookService
import com.github.dhirabayashi.bookmanager.domain.model.Book
import com.github.dhirabayashi.bookmanager.domain.model.DraftBook
import com.github.dhirabayashi.bookmanager.presentation.form.book.BookCreateRequest
import com.github.dhirabayashi.bookmanager.presentation.form.book.BookResponse
import com.github.dhirabayashi.bookmanager.presentation.form.book.BookUpdateRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
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
     * 書籍を登録する
     *
     * @param paramBook 登録する書籍
     * @return 登録された書籍
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun postBook(@RequestBody paramBook: BookCreateRequest): BookResponse {
        val book = DraftBook.create(
            title = paramBook.title,
            price = paramBook.price,
            authorIds = paramBook.authorIds,
            publishingStatus = paramBook.publishingStatus.toModel(),
        )

        val created = bookService.add(book)

        return BookResponse.of(created)
    }

    /**
     * 書籍を更新する
     *
     * @param paramBook 書籍の更新データ
     * @return 更新後の書籍
     */
    @PutMapping
    fun putBook(@RequestBody paramBook: BookUpdateRequest): BookResponse {
        val book = Book.create(
            id = paramBook.id,
            title = paramBook.title,
            price = paramBook.price,
            authorIds = paramBook.authorIds,
            publishingStatus = paramBook.publishingStatus.toModel(),
        )

        val updated = bookService.update(book)

        return BookResponse.of(updated)
    }
}
