package com.github.dhirabayashi.bookmanager.presentation.controller

import com.github.dhirabayashi.bookmanager.application.service.AuthorService
import com.github.dhirabayashi.bookmanager.domain.model.Author
import com.github.dhirabayashi.bookmanager.presentation.form.AuthorCreateRequest
import com.github.dhirabayashi.bookmanager.presentation.form.AuthorListResponse
import com.github.dhirabayashi.bookmanager.presentation.form.AuthorResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * 著者コントローラー
 *
 * @property authorService 著者のユースケース
 */
@RestController
@RequestMapping("authors")
class AuthorController(
    private val authorService: AuthorService,
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

}
