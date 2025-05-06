package com.github.dhirabayashi.bookmanager.domain.service

import com.github.dhirabayashi.bookmanager.domain.enum.PublishingStatus.PUBLISHED
import com.github.dhirabayashi.bookmanager.domain.enum.PublishingStatus.UNPUBLISHED
import com.github.dhirabayashi.bookmanager.domain.model.Book
import org.springframework.stereotype.Service

/**
 * 書籍のドメインサービス
 */
@Service
class BookDomainService {
    /**
     * 現在の書籍データに対して、新しい書籍データへ更新が可能かどうかチェックする
     *
     * @param currentBook 現在の書籍データ
     * @param newBook 新しい（更新後の）書籍データ
     * @return 更新可能な場合はtrue
     */
    fun canUpdateBook(currentBook: Book, newBook: Book): Boolean {
        return !(currentBook.publishingStatus == PUBLISHED && newBook.publishingStatus == UNPUBLISHED)
    }
}
