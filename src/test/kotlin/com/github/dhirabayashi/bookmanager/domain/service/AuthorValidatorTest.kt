package com.github.dhirabayashi.bookmanager.domain.service

import com.github.dhirabayashi.bookmanager.domain.check.ValidationException
import com.github.dhirabayashi.bookmanager.domain.model.Author
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId

class AuthorValidatorTest {
    private val clock = Clock.fixed(
        LocalDate.of(2023, 12, 31).atStartOfDay(ZoneId.systemDefault()).toInstant(),
        ZoneId.systemDefault()
    )

    @ParameterizedTest
    @CsvSource(
        "2023-12-30, true",
        "2023-12-31, false",
        "2024-01-01, false"
    )
    @DisplayName("生年月日が過去日でない場合は例外が投げられること")
    fun create(birthDateStr: String, isValid: Boolean) {
        val birthDate = LocalDate.parse(birthDateStr)

        if (isValid) {
            assertThatCode {
                createAndValidateAuthor(birthDate)
            }.doesNotThrowAnyException()
        } else {
            assertThatThrownBy {
                createAndValidateAuthor(birthDate)
            }.isInstanceOf(ValidationException::class.java)
        }
    }

    private fun createAndValidateAuthor(birthDate: LocalDate) {
        val author = Author.create(id = "id", name = "Test Author", birthDate = birthDate, clock = clock)
        AuthorValidator.executeValidate(author, clock)
    }
}
