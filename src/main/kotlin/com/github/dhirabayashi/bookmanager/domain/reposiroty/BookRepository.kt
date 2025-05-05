package com.github.dhirabayashi.bookmanager.domain.reposiroty

import com.github.dhirabayashi.bookmanager.domain.model.Book

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
    fun findBooksByAuthorId(authorId: Int): List<Book>
}
