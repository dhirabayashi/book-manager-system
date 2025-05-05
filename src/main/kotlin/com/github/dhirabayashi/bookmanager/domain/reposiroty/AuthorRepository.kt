package com.github.dhirabayashi.bookmanager.domain.reposiroty

import com.github.dhirabayashi.bookmanager.domain.model.Author

/**
 * 著者リポジトリ
 */
interface AuthorRepository {
    /**
     * 全ての著者を取得する
     *
     * @return 全ての著者
     */
    fun findAll(): List<Author>

    /**
     * 著者を登録する
     *
     * @param author 登録する著者
     * @return 登録した著者情報
     */
    fun save(author: Author): Author
}
