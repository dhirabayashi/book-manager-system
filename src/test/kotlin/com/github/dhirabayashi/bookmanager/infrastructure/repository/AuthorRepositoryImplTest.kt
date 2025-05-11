package com.github.dhirabayashi.bookmanager.infrastructure.repository

import com.github.dhirabayashi.bookmanager.domain.IdGenerator
import com.github.dhirabayashi.bookmanager.domain.model.Author
import com.github.dhirabayashi.bookmanager.domain.reposiroty.AuthorRepository
import com.github.dhirabayashi.bookmanager.infrastructure.db.Tables.AUTHORS
import com.github.dhirabayashi.bookmanager.test.TestcontainersConfig
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@SpringBootTest(classes = [TestcontainersConfig::class])
@Transactional
class AuthorRepositoryImplTest {
    @Autowired
    private lateinit var create: DSLContext

    @Autowired
    private lateinit var idGenerator: IdGenerator

    private lateinit var sut: AuthorRepository

    @BeforeEach
    fun setUp() {
        sut = AuthorRepositoryImpl(create, idGenerator)
    }

    @Test
    @DisplayName("全ての著者が取得できること")
    fun findAll() {
        prepareFindAllTestData()

        val result = sut.findAll()

        assertThat(result.size).isEqualTo(3)

        assertThat(result[0].id).isEqualTo("author1")
        assertThat(result[0].name).isEqualTo("Author1")
        assertThat(result[0].birthDate).isEqualTo(LocalDate.of(1980, 1, 1))

        assertThat(result[1].id).isEqualTo("author2")
        assertThat(result[1].name).isEqualTo("Author2")
        assertThat(result[1].birthDate).isEqualTo(LocalDate.of(1980, 1, 2))

        assertThat(result[2].id).isEqualTo("author3")
        assertThat(result[2].name).isEqualTo("Author3")
        assertThat(result[2].birthDate).isEqualTo(LocalDate.of(1980, 1, 3))
    }

    @Test
    @DisplayName("著者を追加できること")
    fun add() {
        val author = Author.create(
            id = "author1",
            name = "Author1",
            birthDate = LocalDate.of(1978, 4, 5)
        )

        val added = sut.add(author)

        assertThat(added.id).isEqualTo(author.id)
        assertThat(added.name).isEqualTo(author.name)
        assertThat(added.birthDate).isEqualTo(author.birthDate)

        val actual = create.select()
            .from(AUTHORS)
            .where(AUTHORS.ID.eq(author.id))
            .fetchInto(Author::class.java)

        assertThat(actual.size).isEqualTo(1)
        assertThat(actual[0].id).isEqualTo(author.id)
        assertThat(actual[0].name).isEqualTo(author.name)
        assertThat(actual[0].birthDate).isEqualTo(author.birthDate)
    }

    @Test
    @DisplayName("著者を更新できること")
    fun update() {
        prepareFindAllTestData()

        val updateAuthor = Author.create(
            id = "author3",
            name = "Author3_2",
            birthDate = LocalDate.of(2000, 5, 8),
        )

        val updated = sut.update(updateAuthor)

        assertThat(updated).isEqualTo(updateAuthor)

        val actual = create.select()
            .from(AUTHORS)
            .where(AUTHORS.ID.eq(updateAuthor.id))
            .fetchInto(Author::class.java)

        assertThat(actual.size).isEqualTo(1)

        assertThat(actual[0]).isEqualTo(updateAuthor)
    }

    private fun prepareFindAllTestData() {
        create.insertInto(AUTHORS)
            .columns(AUTHORS.ID, AUTHORS.NAME, AUTHORS.BIRTH_DATE)
            .values("author1", "Author1", LocalDate.of(1980, 1, 1))
            .values("author2", "Author2", LocalDate.of(1980, 1, 2))
            .values("author3", "Author3", LocalDate.of(1980, 1, 3))
            .execute()
    }
}
