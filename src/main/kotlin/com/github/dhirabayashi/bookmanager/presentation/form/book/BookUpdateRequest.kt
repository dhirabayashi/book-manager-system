package com.github.dhirabayashi.bookmanager.presentation.form.book

/**
 * 書籍の更新リクエスト
 *
 * @property title タイトル
 * @property authorIds 著者一覧
 * @property price 価格
 * @property publishingStatus 出版状況
 */
data class BookUpdateRequest(
    val id: String,
    val title: String,
    val price: Int,
    val authorIds: List<String>,
    val publishingStatus: PublishingStatusRequest,
)
