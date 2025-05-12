package com.github.dhirabayashi.bookmanager.domain.model

import com.github.dhirabayashi.bookmanager.domain.check.ValidationException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId

class AuthorTest {
    private val clock = Clock.fixed(
        LocalDate.of(2023, 12, 31).atStartOfDay(ZoneId.systemDefault()).toInstant(),
        ZoneId.systemDefault()
    )

    @Test
    @DisplayName("チェック処理が呼ばれていること")
    fun validate() {
        assertThatThrownBy {
            Author.create(
                id = "id",
                name = "Test Author",
                birthDate = LocalDate.of(2112, 9, 3),
                clock = clock,
            )
        }.isInstanceOf(ValidationException::class.java)
    }
}
