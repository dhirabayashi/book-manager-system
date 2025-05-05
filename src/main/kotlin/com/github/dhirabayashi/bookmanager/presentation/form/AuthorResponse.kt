package com.github.dhirabayashi.bookmanager.presentation.form

import com.github.dhirabayashi.bookmanager.domain.model.Author
import java.time.LocalDate

/**
 * 著者レスポンス
 *
 * @property id 著者ID
 * @property name 名前
 * @property birthDate 生年月日
 */
data class AuthorResponse(
    val id: Int,
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
        fun of(model: Author) = AuthorResponse(
            id = model.id,
            name = model.name,
            birthDate = model.birthDate,
        )
    }
}
