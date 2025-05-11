package com.github.dhirabayashi.bookmanager.domain.check

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class ValidationException(message: String) : RuntimeException(message)

/**
 * 検証を行い、条件が満たされない場合は ValidationException をスロー
 *
 * @param condition 検証条件
 * @param messageProvider エラーメッセージ生成関数
 * @throws ValidationException 検証条件を満たさない場合
 */
@OptIn(ExperimentalContracts::class)
inline fun validate(condition: Boolean, messageProvider: () -> String) {
    contract {
        returns() implies condition
    }
    if (!condition) {
        val message = messageProvider()
        throw ValidationException(message)
    }
}
