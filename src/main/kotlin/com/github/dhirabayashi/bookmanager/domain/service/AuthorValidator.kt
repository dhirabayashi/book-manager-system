package com.github.dhirabayashi.bookmanager.domain.service

import com.github.dhirabayashi.bookmanager.domain.check.ValidationException
import com.github.dhirabayashi.bookmanager.domain.check.validate
import com.github.dhirabayashi.bookmanager.domain.model.Author
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

    private fun validateInternal(birthDate: LocalDate, clock: Clock) {
        validate(birthDate.isBefore(LocalDate.now(clock))) {
            "生年月日は過去日でなければなりません"
        }
    }
}