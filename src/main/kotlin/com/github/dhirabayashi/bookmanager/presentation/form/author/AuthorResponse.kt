package com.github.dhirabayashi.bookmanager.presentation.form.author

import com.github.dhirabayashi.bookmanager.application.service.AuthorDto
import java.time.LocalDate

/**
 * 著者レスポンス
 *
 * @property id 著者ID
 * @property name 名前
 * @property birthDate 生年月日
 */
data class AuthorResponse(
    val id: String,
    val name: String,
    val birthDate: LocalDate,
) {
    companion object {
        /**
         * Dtoをレスポンス用オブジェクトに詰め替える
         *
         * @param dto Serviceからの出力
         * @return レスポンス用オブジェクト
         */
        fun of(dto: AuthorDto) = AuthorResponse(
            id = dto.id,
            name = dto.name,
            birthDate = dto.birthDate,
        )
    }
}
