package com.github.dhirabayashi.bookmanager.presentation.form

import com.github.dhirabayashi.bookmanager.domain.model.Book

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
         * ドメインモデルをレスポンス用オブジェクトに詰め替える
         *
         * @param model ドメインモデル
         * @return レスポンス用オブジェクト
         */
        fun of(model: Book) = BookResponse(
            id = model.id,
            title = model.title,
            price = model.price,
            authors = model.authors.map { AuthorResponse.of(it) },
            publishingStatus = model.publishingStatus.name,
        )
    }
}
