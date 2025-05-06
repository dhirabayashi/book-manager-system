package com.github.dhirabayashi.bookmanager.presentation.form.book

import com.github.dhirabayashi.bookmanager.application.service.BookWithAuthorsDto
import com.github.dhirabayashi.bookmanager.presentation.form.author.AuthorResponse

/**
 * 書籍レスポンス
 *
 * @property id 書籍ID
 * @property title タイトル
 * @property price 価格
 * @property authors 著者の一覧
 * @property publishingStatus 出版状況
 */
data class BookResponse(
    val id: String,
    val title: String,
    val price: Int,
    val authors: List<AuthorResponse>,
    val publishingStatus: String,
) {
    companion object {
        /**
         * Dtoをレスポンス用オブジェクトに詰め替える
         *
         * @param dto Serviceからの出力
         * @return レスポンス用オブジェクト
         */
        fun of(dto: BookWithAuthorsDto) = BookResponse(
            id = dto.id,
            title = dto.title,
            price = dto.price,
            authors = dto.authors.map { AuthorResponse.of(it) },
            publishingStatus = dto.publishingStatus.name,
        )
    }
}
