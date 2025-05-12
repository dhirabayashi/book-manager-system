package com.github.dhirabayashi.bookmanager.presentation.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.dhirabayashi.bookmanager.domain.enum.PublishingStatus
import com.github.dhirabayashi.bookmanager.domain.model.Author
import com.github.dhirabayashi.bookmanager.domain.model.DraftAuthor
import com.github.dhirabayashi.bookmanager.domain.model.DraftBook
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

    private lateinit var author1: Author
    private lateinit var author2: Author

    @BeforeEach
    fun setUp() {
        val draftAuthor1 = DraftAuthor.create(name = "Author One", birthDate = LocalDate.of(1990, 1, 1))
        val draftAuthor2 = DraftAuthor.create(name = "Author Two", birthDate = LocalDate.of(1995, 5, 10))
        author1 = authorRepository.add(draftAuthor1)
        author2 = authorRepository.add(draftAuthor2)
    }

    @Test
    @DisplayName("書籍を登録できること")
    fun postBook() {
        val request = mapOf(
            "title" to "New Book",
            "price" to 2000,
            "authorIds" to listOf(author1.id),
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
        val addedBook = bookRepository.findById(registeredBookId)
        assertThat(addedBook).isNotNull
        assertThat(addedBook?.title).isEqualTo("New Book")
        assertThat(addedBook?.price).isEqualTo(2000)
        assertThat(addedBook?.publishingStatus?.name).isEqualTo("UNPUBLISHED")
        assertThat(addedBook?.authorIds).containsExactly(author1.id)

        // レスポンスとDBの内容が一致しているか確認
        assertThat(addedBook?.id).isEqualTo(registeredBookId)
        assertThat(addedBook?.title).isEqualTo("New Book")
        assertThat(addedBook?.price).isEqualTo(2000)
        assertThat(addedBook?.publishingStatus?.name).isEqualTo("UNPUBLISHED")
        assertThat(addedBook?.authorIds).containsExactly(author1.id)
    }

    @Test
    @DisplayName("書籍を更新できること")
    fun putBook() {
        val book = bookRepository.add(
            DraftBook.create(
                title = "Old Book",
                price = 1000,
                authorIds = listOf(author1.id),
                publishingStatus = PublishingStatus.UNPUBLISHED
            )
        )

        val request = mapOf(
            "id" to book.id,
            "title" to "Updated Book",
            "price" to 2500,
            "authorIds" to listOf(author1.id, author2.id),
            "publishingStatus" to "PUBLISHED"
        )

        // 実行
        mockMvc.put("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect { status { isOk() } }
            .andReturn()

        // 実際にDBに更新されているか確認
        val updatedBook = bookRepository.findById(book.id)
        assertThat(updatedBook).isNotNull
        assertThat(updatedBook?.title).isEqualTo("Updated Book")
        assertThat(updatedBook?.price).isEqualTo(2500)
        assertThat(updatedBook?.publishingStatus?.name).isEqualTo("PUBLISHED")
        assertThat(updatedBook?.authorIds).containsExactly(author1.id, author2.id)

        // レスポンスとDBの内容が一致しているか確認
        assertThat(updatedBook?.id).isEqualTo(book.id)
        assertThat(updatedBook?.title).isEqualTo("Updated Book")
        assertThat(updatedBook?.price).isEqualTo(2500)
        assertThat(updatedBook?.publishingStatus?.name).isEqualTo("PUBLISHED")
        assertThat(updatedBook?.authorIds).containsExactly(author1.id, author2.id)
    }
}
