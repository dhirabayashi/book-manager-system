package com.github.dhirabayashi.bookmanager.domain.model

import com.github.dhirabayashi.bookmanager.domain.check.ValidationException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId

class DraftAuthorTest {
    private val clock = Clock.fixed(
        LocalDate.of(2023, 12, 31).atStartOfDay(ZoneId.systemDefault()).toInstant(),
        ZoneId.systemDefault()
    )

    @Test
    @DisplayName("チェック処理が呼ばれていること")
    fun validate() {
        assertThrows<ValidationException> {
            DraftAuthor.create(
                name = "Test Author",
                birthDate = LocalDate.of(2112, 9, 3),
                clock = clock,
            )
        }
    }
}
