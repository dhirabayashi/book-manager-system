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
    fun add(author: Author): Author

    /**
     * 著者を更新する
     *
     * @param id 対象のID
     * @param author 著者の更新データ
     * @return 更新後の著者情報。IDに対応するデータがなく更新しなかった場合はnull
     */
    fun update(id: String, author: Author): Author?

    /**
     * 著者IDリストをもとに著者情報を取得する
     *
     * @param ids 著者IDリスト
     * @return 著者情報リスト
     */
    fun findByIds(ids: List<String>): List<Author>
}
