package com.github.dhirabayashi.bookmanager.domain

/**
 * IDの生成器
 */
interface IdGenerator {
    /**
     * IDを生成する。生成されるIDは実行ごとに一意となる。
     *
     * @return 生成されたID
     */
    fun generate(): String
}
