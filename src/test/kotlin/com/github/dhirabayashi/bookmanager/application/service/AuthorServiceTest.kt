package com.github.dhirabayashi.bookmanager.application.service

import com.github.dhirabayashi.bookmanager.application.exception.EntityNotFoundException
import com.github.dhirabayashi.bookmanager.domain.model.Author
import com.github.dhirabayashi.bookmanager.domain.model.DraftAuthor
import com.github.dhirabayashi.bookmanager.domain.reposiroty.AuthorRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class AuthorServiceTest {

    @Mock
    private lateinit var authorRepository: AuthorRepository

    @InjectMocks
    private lateinit var sut: AuthorService

    @Test
    @DisplayName("著者一覧が取得できること")
    fun listAll() {
        val authors = listOf(
            Author.create(id = "id", name = "Author One", birthDate = LocalDate.of(1990, 1, 1)),
            Author.create(id = "id2", name = "Author Two", birthDate = LocalDate.of(1990, 1, 2)),
        )
        whenever(authorRepository.findAll()).thenReturn(authors)

        val result = sut.listAll()

        assertThat(result).hasSize(2)
        assertThat(result[0].id).isEqualTo("id")
        assertThat(result[0].name).isEqualTo("Author One")
        assertThat(result[0].birthDate).isEqualTo(LocalDate.of(1990, 1, 1))
        assertThat(result[1].id).isEqualTo("id2")
        assertThat(result[1].name).isEqualTo("Author Two")
        assertThat(result[1].birthDate).isEqualTo(LocalDate.of(1990, 1, 2))

        verify(authorRepository, times(1)).findAll()
    }

    @Test
    @DisplayName("追加処理が呼び出され、追加された著者が返ってくること")
    fun add() {
        val author = Author.create(id = "id", name = "Author Two", birthDate = LocalDate.of(1980, 1, 1))
        val draftAuthor = DraftAuthor.create(name = "Author Two", birthDate = LocalDate.of(1980, 1, 1))
        whenever(authorRepository.add(draftAuthor)).thenReturn(author)

        val result = sut.add(draftAuthor)

        assertThat(result.id).isEqualTo("id")
        assertThat(result.name).isEqualTo("Author Two")
        assertThat(result.birthDate).isEqualTo(LocalDate.of(1980, 1, 1))

        verify(authorRepository, times(1)).add(draftAuthor)
    }

    @Test
    @DisplayName("更新処理が呼び出され、更新した著者が返ってくること")
    fun update() {
        val author = Author.create(id = "author1", name = "Updated Author", birthDate = LocalDate.of(1985, 1, 1))
        whenever(authorRepository.update(author)).thenReturn(author)

        val result = sut.update(author)

        assertThat(result.id).isEqualTo("author1")
        assertThat(result.name).isEqualTo("Updated Author")
        assertThat(result.birthDate).isEqualTo(LocalDate.of(1985, 1, 1))

        verify(authorRepository, times(1)).update(author)
    }

    @Test
    @DisplayName("存在しない著者が指定された場合は例外が投げられること")
    fun update_notFound() {
        val author =
            Author.create(id = "nonexistent", name = "Nonexistent Author", birthDate = LocalDate.of(1985, 1, 1))
        whenever(authorRepository.update(author)).thenReturn(null)

        assertThrows<EntityNotFoundException> {
            sut.update(author)
        }

        verify(authorRepository, times(1)).update(author)
    }
}
