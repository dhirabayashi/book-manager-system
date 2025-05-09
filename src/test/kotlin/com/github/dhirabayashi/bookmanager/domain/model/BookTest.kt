package com.github.dhirabayashi.bookmanager.domain.model

import com.github.dhirabayashi.bookmanager.domain.enum.PublishingStatus
import org.junit.Assert.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class BookTest {

    @ParameterizedTest
    @MethodSource("priceValidationTestCases")
    @DisplayName("価格が0円未満の場合は例外が投げられること")
    fun create_price(price: Int, isValid: Boolean) {
        val executable: () -> Unit = {
            Book.create(
                title = "Test Book",
                price = price,
                authorIds = listOf("author1"),
                publishingStatus = PublishingStatus.UNPUBLISHED
            )
        }

        if (isValid) {
            assertDoesNotThrow(executable)
        } else {
            assertThrows(IllegalArgumentException::class.java, executable)
        }
    }

    @ParameterizedTest
    @MethodSource("authorValidationTestCases")
    @DisplayName("著者が0人の場合は例外が投げられること")
    fun create_author(authorIds: List<String>, isValid: Boolean) {
        val executable: () -> Unit = {
            Book.create(
                title = "Test Book",
                price = 1000,
                authorIds = authorIds,
                publishingStatus = PublishingStatus.UNPUBLISHED
            )
        }

        if (isValid) {
            assertDoesNotThrow(executable)
        } else {
            assertThrows(IllegalArgumentException::class.java, executable)
        }
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
