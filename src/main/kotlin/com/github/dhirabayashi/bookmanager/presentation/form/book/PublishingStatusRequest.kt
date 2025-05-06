package com.github.dhirabayashi.bookmanager.presentation.form.book

import com.github.dhirabayashi.bookmanager.domain.enum.PublishingStatus

/**
 * リクエスト用の出版状況
 */
enum class PublishingStatusRequest {
    /**
     * 未出版
     */
    UNPUBLISHED,

    /**
     * 出版済み
     */
    PUBLISHED;

    /**
     * ドメインの型に変換する
     *　@return ドメインの型
     */
    fun toModel() = when (this) {
        UNPUBLISHED -> PublishingStatus.UNPUBLISHED
        PUBLISHED -> PublishingStatus.PUBLISHED
    }
}
