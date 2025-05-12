package com.github.dhirabayashi.bookmanager.domain.service

import com.github.dhirabayashi.bookmanager.domain.check.ValidationException
import com.github.dhirabayashi.bookmanager.domain.enum.PublishingStatus
import com.github.dhirabayashi.bookmanager.domain.model.Book
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class BookValidatorTest {

    @ParameterizedTest
    @MethodSource("priceValidationTestCases")
    @DisplayName("価格が0円未満の場合は例外が投げられること")
    fun create_price(price: Int, isValid: Boolean) {
        if (isValid) {
            assertThatCode {
                createAndValidateBook(price)
            }.doesNotThrowAnyException()
        } else {
            assertThatThrownBy {
                createAndValidateBook(price)
            }.isInstanceOf(ValidationException::class.java)
        }
    }

    @ParameterizedTest
    @MethodSource("authorValidationTestCases")
    @DisplayName("著者が0人の場合は例外が投げられること")
    fun create_author(authorIds: List<String>, isValid: Boolean) {
        if (isValid) {
            assertThatCode {
                createAndValidateBook(authorIds)
            }.doesNotThrowAnyException()
        } else {
            assertThatThrownBy {
                createAndValidateBook(authorIds)
            }.isInstanceOf(ValidationException::class.java)
        }
    }

    private fun createAndValidateBook(price: Int) {
        val book = Book.create(
            id = "id",
            title = "Test Book",
            price = price,
            authorIds = listOf("author1"),
            publishingStatus = PublishingStatus.UNPUBLISHED
        )
        BookValidator.executeValidate(book)
    }

    private fun createAndValidateBook(authorIds: List<String>) {
        val book = Book.create(
            id = "id",
            title = "Test Book",
            price = 1234,
            authorIds = authorIds,
            publishingStatus = PublishingStatus.UNPUBLISHED
        )
        BookValidator.executeValidate(book)
    }

    companion object {
        @JvmStatic
        fun priceValidationTestCases(): Stream<Arguments> = Stream.of(
            Arguments.of(1, true),
            Arguments.of(0, true),
            Arguments.of(-1, false)
        )

        @JvmStatic
        fun authorValidationTestCases(): Stream<Arguments> = Stream.of(
            Arguments.of(listOf("author1"), true),
            Arguments.of(listOf("author1", "author2"), true),
            Arguments.of(emptyList<String>(), false)
        )
    }
}
