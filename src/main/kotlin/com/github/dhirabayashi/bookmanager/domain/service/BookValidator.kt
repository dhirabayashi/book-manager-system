package com.github.dhirabayashi.bookmanager.domain.service

import com.github.dhirabayashi.bookmanager.domain.check.ValidationException
import com.github.dhirabayashi.bookmanager.domain.check.validate
import com.github.dhirabayashi.bookmanager.domain.model.Book

/**
 * 書籍のバリデーション機能
 */
object BookValidator {
    /**
     * 書籍のバリデーションを実行する
     *
     * @param book 対象の書籍
     * @throws ValidationException バリデーション結果が失敗の場合
     */
    fun executeValidate(book: Book) {
        validateInternal(book.price, book.authorIds)
    }

    private fun validateInternal(price: Int, authorIds: List<String>) {
        validate(price >= 0) {
            "価格は0円以上でなければなりません"
        }
        validate(authorIds.isNotEmpty()) {
            "著者は最低1人は必要です"
        }
    }
}
