package com.github.dhirabayashi.bookmanager.presentation.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.dhirabayashi.bookmanager.domain.enum.PublishingStatus
import com.github.dhirabayashi.bookmanager.domain.model.Author
import com.github.dhirabayashi.bookmanager.domain.model.Book
import com.github.dhirabayashi.bookmanager.domain.reposiroty.AuthorRepository
import com.github.dhirabayashi.bookmanager.domain.reposiroty.BookRepository
import com.github.dhirabayashi.bookmanager.test.TestcontainersConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@SpringBootTest(classes = [TestcontainersConfig::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
class AuthorControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Autowired
    private lateinit var bookRepository: BookRepository

    @BeforeEach
    fun setUp() {
        val author1 = Author.create(id = "author1", name = "Author One", birthDate = LocalDate.of(1990, 1, 1))
        val author2 = Author.create(id = "author2", name = "Author Two", birthDate = LocalDate.of(1995, 5, 10))
        authorRepository.add(author1)
        authorRepository.add(author2)

        val book1 = Book.create(
            id = "book1",
            title = "Test Book 1",
            price = 1000,
            authorIds = listOf("author1"),
            publishingStatus = PublishingStatus.UNPUBLISHED
        )

        val book2 = Book.create(
            id = "book2",
            title = "Test Book 2",
            price = 1500,
            authorIds = listOf("author1", "author2"),
            publishingStatus = PublishingStatus.PUBLISHED
        )

        bookRepository.add(book1)
        bookRepository.add(book2)
    }

    @Test
    @DisplayName("全ての著者を取得できること")
    fun getList() {
        mockMvc.get("/authors/list")
            .andExpect { status { isOk() } }
            .andExpect { jsonPath("$.authors").isArray }
            .andExpect { jsonPath("$.authors[0].id").value("author1") }
            .andExpect { jsonPath("$.authors[0].name").value("Author One") }
            .andExpect { jsonPath("$.authors[0].birthDate").value("1990-01-01") }
            .andExpect { jsonPath("$.authors[1].id").value("author2") }
            .andExpect { jsonPath("$.authors[1].name").value("Author Two") }
            .andExpect { jsonPath("$.authors[1].birthDate").value("1995-05-10") }
    }

    @Test
    @DisplayName("著者IDをもとに書籍の一覧を取得できること")
    fun getBooksByAuthorId() {
        mockMvc.get("/authors/{author_id}/books", "author1")
            .andExpect { status { isOk() } }
            .andExpect { jsonPath("$.books").isArray }
            .andExpect { jsonPath("$.books[0].id").value("book1") }
            .andExpect { jsonPath("$.books[0].title").value("Test Book 1") }
            .andExpect { jsonPath("$.books[0].price").value(1000) }
            .andExpect { jsonPath("$.books[0].publishingStatus").value("UNPUBLISHED") }
            .andExpect { jsonPath("$.books[0].authors[0].id").value("author1") }
            .andExpect { jsonPath("$.books[0].authors[0].name").value("Author One") }
            .andExpect { jsonPath("$.books[0].authors[0].birthDate").value("1990-01-01") }
    }

    @Test
    @DisplayName("著者を登録できること")
    fun postAuthor() {
        val request = mapOf("name" to "New Author", "birthDate" to "2005-07-18")

        // 実行
        val result = mockMvc.post("/authors") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect { status { isCreated() } }
            .andReturn()

        val responseContent = result.response.contentAsString
        val responseAuthor = objectMapper.readTree(responseContent).get("id").asText()

        // 実際にDBに登録されているか確認
        val savedAuthor = authorRepository.findByIds(listOf(responseAuthor)).firstOrNull()
        assertThat(savedAuthor).isNotNull
        assertThat(savedAuthor?.name).isEqualTo("New Author")
        assertThat(savedAuthor?.birthDate.toString()).isEqualTo("2005-07-18")

        // レスポンスとDBの内容が一致しているか確認
        assertThat(savedAuthor?.id).isEqualTo(responseAuthor)
        assertThat(savedAuthor?.name).isEqualTo("New Author")
        assertThat(savedAuthor?.birthDate.toString()).isEqualTo("2005-07-18")
    }

    @Test
    @DisplayName("著者を更新できること")
    fun putAuthor() {
        val authorId = "author1"
        val request = mapOf("id" to authorId, "name" to "Updated Author", "birthDate" to "2000-01-01")

        // 実行
        mockMvc.put("/authors") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect { status { isOk() } }
            .andReturn()

        // 実際にDBに更新されているか確認
        val updatedAuthor = authorRepository.findByIds(listOf(authorId)).firstOrNull()
        assertThat(updatedAuthor).isNotNull
        assertThat(updatedAuthor?.name).isEqualTo("Updated Author")
        assertThat(updatedAuthor?.birthDate.toString()).isEqualTo("2000-01-01")

        // レスポンスとDBの内容が一致しているか確認
        assertThat(updatedAuthor?.id).isEqualTo(authorId)
        assertThat(updatedAuthor?.name).isEqualTo("Updated Author")
        assertThat(updatedAuthor?.birthDate.toString()).isEqualTo("2000-01-01")
    }
}
