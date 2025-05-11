package com.github.dhirabayashi.bookmanager.domain.model

import com.github.dhirabayashi.bookmanager.domain.check.ValidationException
import com.github.dhirabayashi.bookmanager.domain.check.validate
import java.time.Clock
import java.time.LocalDate

/**
 * 著者
 *
 * @property id 著者ID。未採番の場合はnull
 * @property name 名前
 * @property birthDate 生年月日
 */
data class Author private constructor(
    val id: String?,
    val name: String,
    val birthDate: LocalDate,
) {
    companion object {
        /**
         * 著者情報を生成する
         *
         * @param id 著者ID。未採番の場合はnull
         * @param name 名前
         * @param birthDate 生年月日
         * @param clock クロック。テストから使うことを想定
         * @throws ValidationException 生年月日が過去日ではない場合
         * @return 生成された著者
         */
        fun create(
            id: String? = null,
            name: String,
            birthDate: LocalDate,
            clock: Clock = Clock.systemDefaultZone(),
        ): Author {
            // チェック
            validate(birthDate.isBefore(LocalDate.now(clock))) {
                "生年月日は過去日でなければなりません"
            }

            return Author(id, name, birthDate)
        }
    }
}
