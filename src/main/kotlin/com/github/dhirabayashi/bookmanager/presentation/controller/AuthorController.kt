package com.github.dhirabayashi.bookmanager.presentation.controller

import com.github.dhirabayashi.bookmanager.application.service.AuthorService
import com.github.dhirabayashi.bookmanager.application.service.BookService
import com.github.dhirabayashi.bookmanager.domain.model.Author
import com.github.dhirabayashi.bookmanager.presentation.form.author.AuthorCreateRequest
import com.github.dhirabayashi.bookmanager.presentation.form.author.AuthorListResponse
import com.github.dhirabayashi.bookmanager.presentation.form.author.AuthorResponse
import com.github.dhirabayashi.bookmanager.presentation.form.author.AuthorUpdateRequest
import com.github.dhirabayashi.bookmanager.presentation.form.book.BookListResponse
import com.github.dhirabayashi.bookmanager.presentation.form.book.BookResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * 著者コントローラー
 *
 * @property authorService 著者のユースケース
 * @property bookService 書籍のユースケース
 */
@RestController
@RequestMapping("authors")
class AuthorController(
    private val authorService: AuthorService,
    private val bookService: BookService,
) {
    /**
     * 全ての著者を取得する。クライアントが著者IDを知るための最低限の手段という想定。
     *
     * @return 著者一覧レスポンス
     */
    @GetMapping("/list")
    fun getList(): AuthorListResponse {
        val authors = authorService.listAll()
            .map { AuthorResponse.of(it) }
        return AuthorListResponse(authors)
    }

    /**
     * 著者IDをもとに書籍の一覧を取得する
     *
     * @param authorId 著者ID
     * @return 書籍の一覧レスポンス
     */
    @GetMapping("/{author_id}/books")
    fun getBooksByAuthorId(@PathVariable("author_id") authorId: String): BookListResponse {
        val bookList = bookService.retrieveBooksByAuthorId(authorId)
            .map { BookResponse.of(it) }

        return BookListResponse(bookList)
    }

    /**
     * 著者を登録する
     *
     * @param paramAuthor 登録する著者
     * @return 登録された著者
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun postAuthor(@RequestBody paramAuthor: AuthorCreateRequest): AuthorResponse {
        val author = Author.create(
            name = paramAuthor.name,
            birthDate = paramAuthor.birthDate,
        )
        val created = authorService.add(author)

        return AuthorResponse.of(created)
    }

    /**
     * 著者を更新する
     *
     * @param paramAuthor 更新する著者
     * @return 更新後の著者
     */
    @PutMapping
    fun putAuthor(@RequestBody paramAuthor: AuthorUpdateRequest): AuthorResponse {
        val author = Author.create(
            id = paramAuthor.id,
            name = paramAuthor.name,
            birthDate = paramAuthor.birthDate,
        )
        val updated = authorService.update(author)

        return AuthorResponse.of(updated)
    }
}
