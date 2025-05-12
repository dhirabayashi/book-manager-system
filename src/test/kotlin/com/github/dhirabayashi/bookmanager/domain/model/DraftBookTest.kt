package com.github.dhirabayashi.bookmanager.domain.model

import com.github.dhirabayashi.bookmanager.domain.check.ValidationException
import com.github.dhirabayashi.bookmanager.domain.enum.PublishingStatus
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class DraftBookTest {
    @Test
    @DisplayName("チェック処理が呼ばれていること")
    fun validate() {
        assertThatThrownBy {
            DraftBook.create(
                title = "Test Book",
                price = -1,
                authorIds = listOf("author1"),
                publishingStatus = PublishingStatus.UNPUBLISHED
            )
        }.isInstanceOf(ValidationException::class.java)
    }
}
