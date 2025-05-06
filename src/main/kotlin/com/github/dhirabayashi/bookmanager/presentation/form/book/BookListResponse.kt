package com.github.dhirabayashi.bookmanager.presentation.form.book

/**
 * 書籍一覧レスポンス
 *
 * @property books 書籍の一覧
 */
data class BookListResponse(
    val books: List<BookResponse>,
)
