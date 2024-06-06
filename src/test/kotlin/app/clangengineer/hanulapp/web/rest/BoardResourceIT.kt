package app.clangengineer.hanulapp.web.rest

import app.clangengineer.hanulapp.IntegrationTest
import app.clangengineer.hanulapp.domain.Board
import app.clangengineer.hanulapp.repository.BoardRepository
import app.clangengineer.hanulapp.service.mapper.BoardMapper
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*
import kotlin.test.assertNotNull

@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class BoardResourceIT {
    @Autowired
    private lateinit var boardRepository: BoardRepository

    @Autowired
    private lateinit var boardMapper: BoardMapper

    @Autowired
    private lateinit var webTestClient: WebTestClient

    private lateinit var board: Board

    @BeforeEach
    fun initTest() {
        boardRepository.deleteAll().block()
        board = createEntity()
    }

    @Test
    @Throws(Exception::class)
    fun createBoard() {
        val databaseSizeBeforeCreate = boardRepository.findAll().collectList().block().size
        // Create the Board
        val boardDTO = boardMapper.toDto(board)
        webTestClient.post().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(boardDTO))
            .exchange()
            .expectStatus().isCreated

        // Validate the Board in the database
        val boardList = boardRepository.findAll().collectList().block()
        assertThat(boardList).hasSize(databaseSizeBeforeCreate + 1)
        val testBoard = boardList[boardList.size - 1]

        assertThat(testBoard.title).isEqualTo(DEFAULT_TITLE)
        assertThat(testBoard.description).isEqualTo(DEFAULT_DESCRIPTION)
        assertThat(testBoard.activated).isEqualTo(DEFAULT_ACTIVATED)
    }

    @Test
    @Throws(Exception::class)
    fun createBoardWithExistingId() {
        board.id = "existing_id"
        val boardDTO = boardMapper.toDto(board)

        val databaseSizeBeforeCreate = boardRepository.findAll().collectList().block().size

        webTestClient.post().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(boardDTO))
            .exchange()
            .expectStatus().isBadRequest

        val boardList = boardRepository.findAll().collectList().block()
        assertThat(boardList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Throws(Exception::class)
    fun checkTitleIsRequired() {
        val databaseSizeBeforeTest = boardRepository.findAll().collectList().block().size

        board.title = null

        val boardDTO = boardMapper.toDto(board)

        webTestClient.post().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(boardDTO))
            .exchange()
            .expectStatus().isBadRequest

        val boardList = boardRepository.findAll().collectList().block()
        assertThat(boardList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Throws(Exception::class)
    fun checkActivatedIsRequired() {
        val databaseSizeBeforeTest = boardRepository.findAll().collectList().block().size

        board.activated = null

        val boardDTO = boardMapper.toDto(board)

        webTestClient.post().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(boardDTO))
            .exchange()
            .expectStatus().isBadRequest

        val boardList = boardRepository.findAll().collectList().block()
        assertThat(boardList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    fun getAllBoards() {
        boardRepository.save(board).block()

        webTestClient.get().uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id").value(hasItem(board.id))
            .jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].activated").value(hasItem(DEFAULT_ACTIVATED))
    }

    @Test
    fun getBoard() {
        boardRepository.save(board).block()

        val id = board.id
        assertNotNull(id)

        webTestClient.get().uri(ENTITY_API_URL_ID, board.id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id").value(`is`(board.id))
            .jsonPath("$.title").value(`is`(DEFAULT_TITLE))
            .jsonPath("$.description").value(`is`(DEFAULT_DESCRIPTION))
            .jsonPath("$.activated").value(`is`(DEFAULT_ACTIVATED))
    }

    @Test
    fun getNonExistingBoard() {
        webTestClient.get().uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun putExistingBoard() {
        boardRepository.save(board).block()

        val databaseSizeBeforeUpdate = boardRepository.findAll().collectList().block().size

        val updatedBoard = boardRepository.findById(board.id).block()
        updatedBoard.title = UPDATED_TITLE
        updatedBoard.description = UPDATED_DESCRIPTION
        updatedBoard.activated = UPDATED_ACTIVATED
        val boardDTO = boardMapper.toDto(updatedBoard)

        webTestClient.put().uri(ENTITY_API_URL_ID, boardDTO.id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(boardDTO))
            .exchange()
            .expectStatus().isOk

        val boardList = boardRepository.findAll().collectList().block()
        assertThat(boardList).hasSize(databaseSizeBeforeUpdate)
        val testBoard = boardList[boardList.size - 1]
        assertThat(testBoard.title).isEqualTo(UPDATED_TITLE)
        assertThat(testBoard.description).isEqualTo(UPDATED_DESCRIPTION)
        assertThat(testBoard.activated).isEqualTo(UPDATED_ACTIVATED)
    }

    @Test
    fun putNonExistingBoard() {
        val databaseSizeBeforeUpdate = boardRepository.findAll().collectList().block().size
        board.id = UUID.randomUUID().toString()

        val boardDTO = boardMapper.toDto(board)

        webTestClient.put().uri(ENTITY_API_URL_ID, boardDTO.id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(boardDTO))
            .exchange()
            .expectStatus().isBadRequest

        val boardList = boardRepository.findAll().collectList().block()
        assertThat(boardList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun putWithIdMismatchBoard() {
        val databaseSizeBeforeUpdate = boardRepository.findAll().collectList().block().size
        board.id = UUID.randomUUID().toString()

        val boardDTO = boardMapper.toDto(board)

        webTestClient.put().uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(boardDTO))
            .exchange()
            .expectStatus().isBadRequest

        val boardList = boardRepository.findAll().collectList().block()
        assertThat(boardList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun putWithMissingIdPathParamBoard() {
        val databaseSizeBeforeUpdate = boardRepository.findAll().collectList().block().size
        board.id = UUID.randomUUID().toString()

        val boardDTO = boardMapper.toDto(board)

        webTestClient.put().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(boardDTO))
            .exchange()
            .expectStatus().isEqualTo(405)

        val boardList = boardRepository.findAll().collectList().block()
        assertThat(boardList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun partialUpdateBoardWithPatch() {
        boardRepository.save(board).block()

        val databaseSizeBeforeUpdate = boardRepository.findAll().collectList().block().size

        val partialUpdatedBoard = Board().apply {
            id = board.id
            title = UPDATED_TITLE
            description = UPDATED_DESCRIPTION
            activated = UPDATED_ACTIVATED
        }

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBoard.id)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(partialUpdatedBoard))
            .exchange()
            .expectStatus()
            .isOk

        val boardList = boardRepository.findAll().collectList().block()
        assertThat(boardList).hasSize(databaseSizeBeforeUpdate)
        val testBoard = boardList.last()
        assertThat(testBoard.title).isEqualTo(UPDATED_TITLE)
        assertThat(testBoard.description).isEqualTo(UPDATED_DESCRIPTION)
        assertThat(testBoard.activated).isEqualTo(UPDATED_ACTIVATED)
    }

    @Test
    @Throws(Exception::class)
    fun fullUpdateBoardWithPatch() {
        boardRepository.save(board).block()

        val databaseSizeBeforeUpdate = boardRepository.findAll().collectList().block().size

        val partialUpdatedBoard = Board().apply {
            id = board.id

            title = UPDATED_TITLE
            description = UPDATED_DESCRIPTION
            activated = UPDATED_ACTIVATED
        }

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBoard.id)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(partialUpdatedBoard))
            .exchange()
            .expectStatus()
            .isOk

        val boardList = boardRepository.findAll().collectList().block()
        assertThat(boardList).hasSize(databaseSizeBeforeUpdate)
        val testBoard = boardList.last()
        assertThat(testBoard.title).isEqualTo(UPDATED_TITLE)
        assertThat(testBoard.description).isEqualTo(UPDATED_DESCRIPTION)
        assertThat(testBoard.activated).isEqualTo(UPDATED_ACTIVATED)
    }

    @Throws(Exception::class)
    fun patchNonExistingBoard() {
        val databaseSizeBeforeUpdate = boardRepository.findAll().collectList().block().size
        board.id = UUID.randomUUID().toString()

        val boardDTO = boardMapper.toDto(board)

        webTestClient.patch().uri(ENTITY_API_URL_ID, boardDTO.id)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(boardDTO))
            .exchange()
            .expectStatus().isBadRequest

        val boardList = boardRepository.findAll().collectList().block()
        assertThat(boardList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun patchWithIdMismatchBoard() {
        val databaseSizeBeforeUpdate = boardRepository.findAll().collectList().block().size
        board.id = UUID.randomUUID().toString()

        val boardDTO = boardMapper.toDto(board)

        webTestClient.patch().uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(boardDTO))
            .exchange()
            .expectStatus().isBadRequest

        val boardList = boardRepository.findAll().collectList().block()
        assertThat(boardList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamBoard() {
        val databaseSizeBeforeUpdate = boardRepository.findAll().collectList().block().size
        board.id = UUID.randomUUID().toString()

        val boardDTO = boardMapper.toDto(board)

        webTestClient.patch().uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(boardDTO))
            .exchange()
            .expectStatus().isEqualTo(405)

        val boardList = boardRepository.findAll().collectList().block()
        assertThat(boardList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test

    fun deleteBoard() {
        boardRepository.save(board).block()
        val databaseSizeBeforeDelete = boardRepository.findAll().collectList().block().size

        webTestClient.delete().uri(ENTITY_API_URL_ID, board.id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent

        val boardList = boardRepository.findAll().collectList().block()
        assertThat(boardList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {
        private const val DEFAULT_TITLE = "AAAAAAAAAA"
        private const val UPDATED_TITLE = "BBBBBBBBBB"

        private const val DEFAULT_DESCRIPTION = "AAAAAAAAAA"
        private const val UPDATED_DESCRIPTION = "BBBBBBBBBB"

        private const val DEFAULT_ACTIVATED: Boolean = false
        private const val UPDATED_ACTIVATED: Boolean = true

        private val ENTITY_API_URL: String = "/api/boards"
        private val ENTITY_API_URL_ID: String = ENTITY_API_URL + "/{id}"

        @JvmStatic
        fun createEntity(): Board {
            return Board(
                title = DEFAULT_TITLE,
                description = DEFAULT_DESCRIPTION,
                activated = DEFAULT_ACTIVATED
            )
        }

        @JvmStatic
        fun createUpdatedEntity(): Board {
            return Board(
                title = UPDATED_TITLE,
                description = UPDATED_DESCRIPTION,
                activated = UPDATED_ACTIVATED
            )
        }
    }
}
