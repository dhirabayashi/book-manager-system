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
    fun findByAuthorId(authorId: String): List<Book>

    /**
     * 書籍を登録する
     *
     * @param book 登録する書籍
     * @return 登録された書籍
     */
    fun add(book: Book): Book

    /**
     * 書籍を更新する
     *
     * @param bookId 書籍ID
     * @param book 書籍の更新データ
     * @return 更新後の書籍
     */
    fun update(bookId: String, book: Book): Book?
}
