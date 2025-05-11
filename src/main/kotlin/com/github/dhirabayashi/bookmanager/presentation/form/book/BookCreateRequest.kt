package com.github.dhirabayashi.bookmanager.presentation.form.book

/**
 * 書籍の登録リクエスト
 *
 * @property title タイトル
 * @property price 価格
 * @property authorIds 著者一覧
 * @property publishingStatus 出版状況
 */
data class BookCreateRequest(
    val title: String,
    val price: Int,
    val authorIds: List<String>,
    val publishingStatus: PublishingStatusRequest,
)
