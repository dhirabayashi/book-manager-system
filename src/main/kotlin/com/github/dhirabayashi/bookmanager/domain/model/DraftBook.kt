package com.github.dhirabayashi.bookmanager.domain.model

import com.github.dhirabayashi.bookmanager.domain.check.ValidationException
import com.github.dhirabayashi.bookmanager.domain.enum.PublishingStatus
import com.github.dhirabayashi.bookmanager.domain.service.BookValidator

/**
 * 登録前の書籍情報
 *
 * @property title タイトル
 * @property price 価格
 * @property authorIds 著者一覧
 * @property publishingStatus 出版状況
 */
data class DraftBook private constructor(
    val title: String,
    val price: Int,
    val authorIds: List<String>,
    val publishingStatus: PublishingStatus,
) {
    companion object {
        /**
         * 書籍情報を生成する
         *
         * @param title タイトル
         * @param price 価格
         * @param authorIds 著者一覧
         * @param publishingStatus 出版状況
         * @return 書籍情報
         * @throws ValidationException 価格が0円未満の場合、著者が未指定の場合
         */
        fun create(
            title: String,
            price: Int,
            authorIds: List<String>,
            publishingStatus: PublishingStatus,
        ): DraftBook {
            val draftBook = DraftBook(
                title = title,
                price = price,
                authorIds = authorIds,
                publishingStatus = publishingStatus,
            )
            // チェック
            BookValidator.executeValidate(draftBook)

            return draftBook
        }
    }
}
