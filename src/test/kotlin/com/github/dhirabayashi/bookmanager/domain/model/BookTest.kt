package com.github.dhirabayashi.bookmanager.domain.model

import com.github.dhirabayashi.bookmanager.domain.check.ValidationException
import com.github.dhirabayashi.bookmanager.domain.enum.PublishingStatus
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BookTest {
    @Test
    @DisplayName("チェック処理が呼ばれていること")
    fun validate() {
        assertThrows<ValidationException> {
            Book.create(
                id = "id",
                title = "Test Book",
                price = -1,
                authorIds = listOf("author1"),
                publishingStatus = PublishingStatus.UNPUBLISHED
            )
        }
    }
}
