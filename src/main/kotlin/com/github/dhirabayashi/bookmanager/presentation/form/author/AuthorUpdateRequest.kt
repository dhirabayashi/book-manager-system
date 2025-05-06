package com.github.dhirabayashi.bookmanager.presentation.form.author

import java.time.LocalDate

/**
 * 著者の更新リクエスト
 *
 * @property id 著者ID
 * @property name 名前
 * @property birthDate 生年月日
 */
data class AuthorUpdateRequest(
    val id: String,
    val name: String,
    val birthDate: LocalDate,
)
