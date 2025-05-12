package com.github.dhirabayashi.bookmanager.domain.service

import com.github.dhirabayashi.bookmanager.domain.enum.PublishingStatus
import com.github.dhirabayashi.bookmanager.domain.enum.PublishingStatus.PUBLISHED
import com.github.dhirabayashi.bookmanager.domain.enum.PublishingStatus.UNPUBLISHED
import com.github.dhirabayashi.bookmanager.domain.model.Book
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class BookDomainServiceTest {

    private val sut = BookDomainService()

    companion object {
        @JvmStatic
        fun bookStatusUpdateCases(): Stream<Arguments> = Stream.of(
            // 出版済み → 未出版（禁止）
            Arguments.of(PUBLISHED, UNPUBLISHED, false),

            // 出版済み → 出版済み（OK）
            Arguments.of(PUBLISHED, PUBLISHED, true),

            // 未出版 → 出版済み（OK）
            Arguments.of(UNPUBLISHED, PUBLISHED, true),

            // 未出版 → 未出版（OK）
            Arguments.of(UNPUBLISHED, UNPUBLISHED, true),
        )
    }

    @ParameterizedTest
    @MethodSource("bookStatusUpdateCases")
    @DisplayName("出版済みの書籍を出版済みにすることはできない")
    fun testCanUpdateBook(
        currentStatus: PublishingStatus,
        newStatus: PublishingStatus,
        expected: Boolean,
    ) {
        // テストデータ
        val currentBook = createBook(currentStatus)
        val newBook = createBook(newStatus)

        // 実行
        val actual = sut.canUpdateBook(currentBook, newBook)

        // 検証
        assertThat(actual).isEqualTo(expected)
    }

    private fun createBook(publishingStatus: PublishingStatus) = Book.create(
        id = "dummy",
        title = "t",
        price = 1000,
        authorIds = listOf("a"),
        publishingStatus = publishingStatus,
    )
}
