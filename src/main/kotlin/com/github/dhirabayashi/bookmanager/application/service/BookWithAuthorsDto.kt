package com.github.dhirabayashi.bookmanager.application.service

import com.github.dhirabayashi.bookmanager.domain.enum.PublishingStatus

/**
 * Serviceからの出力用の書籍
 *
 * @property id 書籍ID
 * @property title タイトル
 * @property price 価格
 * @property authors 著者
 * @property publishingStatus 出版状況
 */
data class BookWithAuthorsDto(
    val id: String,
    val title: String,
    val price: Int,
    val authors: List<AuthorDto>,
    val publishingStatus: PublishingStatus,
)
