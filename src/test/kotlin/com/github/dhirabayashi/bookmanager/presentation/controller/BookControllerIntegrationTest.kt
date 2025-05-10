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
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@SpringBootTest(classes = [TestcontainersConfig::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
class BookControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @BeforeEach
    fun setUp() {
        val author1 = Author.create(id = "author1", name = "Author One", birthDate = LocalDate.of(1990, 1, 1))
        val author2 = Author.create(id = "author2", name = "Author Two", birthDate = LocalDate.of(1995, 5, 10))
        authorRepository.add(author1)
        authorRepository.add(author2)
    }

    @Test
    @DisplayName("書籍を登録できること")
    fun postBook() {
        val request = mapOf(
            "title" to "New Book",
            "price" to 2000,
            "authorIds" to listOf("author1"),
            "publishingStatus" to "UNPUBLISHED"
        )

        // 実行
        val result = mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect { status { isCreated() } }
            .andReturn()

        val responseContent = result.response.contentAsString
        val registeredBookId = objectMapper.readTree(responseContent).get("id").asText()

        // 実際にDBに登録されているか確認
        val savedBook = bookRepository.findById(registeredBookId)
        assertThat(savedBook).isNotNull
        assertThat(savedBook?.title).isEqualTo("New Book")
        assertThat(savedBook?.price).isEqualTo(2000)
        assertThat(savedBook?.publishingStatus?.name).isEqualTo("UNPUBLISHED")
        assertThat(savedBook?.authorIds).containsExactly("author1")

        // レスポンスとDBの内容が一致しているか確認
        assertThat(savedBook?.id).isEqualTo(registeredBookId)
        assertThat(savedBook?.title).isEqualTo("New Book")
        assertThat(savedBook?.price).isEqualTo(2000)
        assertThat(savedBook?.publishingStatus?.name).isEqualTo("UNPUBLISHED")
        assertThat(savedBook?.authorIds).containsExactly("author1")
    }

    @Test
    @DisplayName("書籍を更新できること")
    fun putBook() {
        val bookId = "book1"
        bookRepository.add(
            Book.create(
                id = bookId,
                title = "Old Book",
                price = 1000,
                authorIds = listOf("author1"),
                publishingStatus = PublishingStatus.UNPUBLISHED
            )
        )

        val request = mapOf(
            "id" to bookId,
            "title" to "Updated Book",
            "price" to 2500,
            "authorIds" to listOf("author1", "author2"),
            "publishingStatus" to "PUBLISHED"
        )

        // 実行
        val result = mockMvc.put("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect { status { isOk() } }
            .andReturn()

        val responseContent = result.response.contentAsString

        // 実際にDBに更新されているか確認
        val updatedBook = bookRepository.findById(bookId)
        assertThat(updatedBook).isNotNull
        assertThat(updatedBook?.title).isEqualTo("Updated Book")
        assertThat(updatedBook?.price).isEqualTo(2500)
        assertThat(updatedBook?.publishingStatus?.name).isEqualTo("PUBLISHED")
        assertThat(updatedBook?.authorIds).containsExactly("author1", "author2")

        // レスポンスとDBの内容が一致しているか確認
        assertThat(updatedBook?.id).isEqualTo(bookId)
        assertThat(updatedBook?.title).isEqualTo("Updated Book")
        assertThat(updatedBook?.price).isEqualTo(2500)
        assertThat(updatedBook?.publishingStatus?.name).isEqualTo("PUBLISHED")
        assertThat(updatedBook?.authorIds).containsExactly("author1", "author2")
    }
}
