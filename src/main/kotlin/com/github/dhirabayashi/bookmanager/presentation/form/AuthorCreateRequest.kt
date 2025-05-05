package com.github.dhirabayashi.bookmanager.presentation.form

import java.time.LocalDate

/**
 * 著者の登録リクエスト
 *
 * @property name 名前
 * @property birthDate 生年月日
 */
data class AuthorCreateRequest(
    val name: String,
    val birthDate: LocalDate,
)
