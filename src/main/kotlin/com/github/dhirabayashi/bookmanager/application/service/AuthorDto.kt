package com.github.dhirabayashi.bookmanager.application.service

import com.github.dhirabayashi.bookmanager.domain.model.Author
import java.time.LocalDate

/**
 * Serviceからの出力用の著者
 *
 * @property id 著者ID
 * @property name 名前
 * @property birthDate 生年月日
 */
data class AuthorDto(
    val id: String,
    val name: String,
    val birthDate: LocalDate,
) {
    companion object {
        /**
         * ドメインモデルをレスポンス用オブジェクトに詰め替える
         *
         * @param model ドメインモデル
         * @return レスポンス用オブジェクト
         */
        fun of(model: Author) = AuthorDto(
            id = model.id,
            name = model.name,
            birthDate = model.birthDate,
        )
    }
}
