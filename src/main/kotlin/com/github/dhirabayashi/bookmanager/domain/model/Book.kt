package com.github.dhirabayashi.bookmanager.domain.model

import com.github.dhirabayashi.bookmanager.domain.enum.BookStatus

/**
 * 書籍情報
 *
 * @property id 書籍ID
 * @property title タイトル
 * @property authors 著者一覧
 * @property price 価格
 */
data class Book(
    val id: Int,
    val title: String,
    val price: Int,
    val authors: List<Author>,
    val status: BookStatus,
)
