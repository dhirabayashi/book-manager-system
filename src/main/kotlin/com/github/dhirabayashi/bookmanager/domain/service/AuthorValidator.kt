package com.github.dhirabayashi.bookmanager.domain.service

import com.github.dhirabayashi.bookmanager.domain.check.ValidationException
import com.github.dhirabayashi.bookmanager.domain.check.validate
import com.github.dhirabayashi.bookmanager.domain.model.Author
import com.github.dhirabayashi.bookmanager.domain.model.DraftAuthor
import java.time.Clock
import java.time.LocalDate

/**
 * 著者のバリデーション機能
 */
object AuthorValidator {
    /**
     * 著者のバリデーションを実行する
     *
     * @param author 対象の著者
     * @throws ValidationException バリデーション結果が失敗の場合
     */
    fun executeValidate(author: Author, clock: Clock) {
        validateInternal(author.birthDate, clock)
    }

    /**
     * 登録前の著者のバリデーションを実行する
     *
     * @param draftAuthor 対象の著者
     * @throws ValidationException バリデーション結果が失敗の場合
     */
    fun executeValidate(draftAuthor: DraftAuthor, clock: Clock) {
        validateInternal(draftAuthor.birthDate, clock)
    }

    private fun validateInternal(birthDate: LocalDate, clock: Clock) {
        validate(birthDate.isBefore(LocalDate.now(clock))) {
            "生年月日は過去日でなければなりません"
        }
    }
}
