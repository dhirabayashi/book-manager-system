package com.github.dhirabayashi.bookmanager.domain.reposiroty

import com.github.dhirabayashi.bookmanager.domain.model.Book
import com.github.dhirabayashi.bookmanager.domain.model.DraftBook

/**
 * 書籍リポジトリ
 */
interface BookRepository {
    /**
     * 著者IDをもとに書籍の一覧を取得する
     *
     * @param authorId 著者ID
     * @return 書籍の一覧
     */
    fun findByAuthorId(authorId: String): List<Book>

    /**
     * IDをもとに書籍を取得する
     *
     * @param id 取得対象の書籍ID
     * @return 書籍。IDに対応する書籍が存在しない場合はnull
     */
    fun findById(id: String): Book?

    /**
     * 書籍を登録する
     *
     * @param book 登録する書籍
     * @return 登録された書籍
     */
    fun add(book: DraftBook): Book

    /**
     * 書籍を更新する
     *
     * @param book 書籍の更新データ
     * @return 更新後の書籍
     */
    fun update(book: Book): Book?
}
