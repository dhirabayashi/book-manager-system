package com.github.dhirabayashi.bookmanager.domain.model

import com.github.dhirabayashi.bookmanager.domain.enum.PublishingStatus

/**
 * 書籍情報
 *
 * @property id 書籍ID。未採番の場合はnull
 * @property title タイトル
 * @property price 価格
 * @property authorIds 著者一覧
 * @property publishingStatus 出版状況
 */
data class Book(
    val id: String?,
    val title: String,
    val price: Int,
    val authorIds: List<String>,
    val publishingStatus: PublishingStatus,
) {
    companion object {
        /**
         * 書籍情報を生成する
         *
         * @param id 書籍ID。未採番の場合はnull
         * @param title タイトル
         * @param price 価格
         * @param authorIds 著者一覧
         * @param publishingStatus 出版状況
         * @return 書籍情報
         */
        fun create(
            id: String? = null,
            title: String,
            price: Int,
            authorIds: List<String>,
            publishingStatus: PublishingStatus,
        ): Book {
            // チェック
            require(price >= 0) {
                "価格は0円以上でなければなりません"
            }
            require(authorIds.isNotEmpty()) {
                "著者は最低1人は必要です"
            }

            return Book(
                id = id,
                title = title,
                price = price,
                authorIds = authorIds,
                publishingStatus = publishingStatus,
            )
        }
    }
}
