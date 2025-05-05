package com.github.dhirabayashi.bookmanager.domain.model

import java.time.LocalDate

/**
 * 著者
 *
 * @property id 著者ID
 * @property name 名前
 * @property birthDate 生年月日
 */
data class Author(
    val id: String,
    val name: String,
    val birthDate: LocalDate,
)
