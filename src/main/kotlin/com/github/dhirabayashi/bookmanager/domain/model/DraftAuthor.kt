package com.github.dhirabayashi.bookmanager.domain.model

import com.github.dhirabayashi.bookmanager.domain.check.ValidationException
import com.github.dhirabayashi.bookmanager.domain.service.AuthorValidator
import java.time.Clock
import java.time.LocalDate

/**
 * 登録前の著者
 *
 * @property name 名前
 * @property birthDate 生年月日
 */
data class DraftAuthor private constructor(
    val name: String,
    val birthDate: LocalDate,
) {
    companion object {
        /**
         * 著者情報を生成する
         *
         * @param name 名前
         * @param birthDate 生年月日
         * @param clock クロック。テストから使うことを想定
         * @throws ValidationException 生年月日が過去日ではない場合
         * @return 生成された著者
         */
        fun create(
            name: String,
            birthDate: LocalDate,
            clock: Clock = Clock.systemDefaultZone(),
        ): DraftAuthor {
            val draftAuthor = DraftAuthor(name, birthDate)

            // チェック
            AuthorValidator.executeValidate(draftAuthor, clock)

            return draftAuthor
        }
    }
}
