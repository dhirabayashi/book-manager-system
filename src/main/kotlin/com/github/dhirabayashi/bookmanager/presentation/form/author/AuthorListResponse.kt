package com.github.dhirabayashi.bookmanager.presentation.form.author

/**
 * 著者一覧レスポンス
 *
 * @property authors 著者の一覧
 */
data class AuthorListResponse(
    val authors: List<AuthorResponse>
)
