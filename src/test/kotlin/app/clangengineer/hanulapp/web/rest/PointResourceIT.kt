package app.clangengineer.hanulapp.web.rest

import app.clangengineer.hanulapp.IntegrationTest
import app.clangengineer.hanulapp.domain.Point
import app.clangengineer.hanulapp.repository.PointRepository
import app.clangengineer.hanulapp.service.mapper.PointMapper
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
import java.util.UUID
import kotlin.test.assertNotNull

/**
 * Integration tests for the [PointResource] REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class PointResourceIT {
    @Autowired
    private lateinit var pointRepository: PointRepository

    @Autowired
    private lateinit var pointMapper: PointMapper

    @Autowired
    private lateinit var webTestClient: WebTestClient

    private lateinit var point: Point

    @BeforeEach
    fun initTest() {
        pointRepository.deleteAll().block()
        point = createEntity()
    }

    @Test
    @Throws(Exception::class)
    fun createPoint() {
        val databaseSizeBeforeCreate = pointRepository.findAll().collectList().block().size
        // Create the Point
        val pointDTO = pointMapper.toDto(point)
        webTestClient.post().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(pointDTO))
            .exchange()
            .expectStatus().isCreated

        // Validate the Point in the database
        val pointList = pointRepository.findAll().collectList().block()
        assertThat(pointList).hasSize(databaseSizeBeforeCreate + 1)
        val testPoint = pointList[pointList.size - 1]

        assertThat(testPoint.title).isEqualTo(DEFAULT_TITLE)
        assertThat(testPoint.description).isEqualTo(DEFAULT_DESCRIPTION)
        assertThat(testPoint.activated).isEqualTo(DEFAULT_ACTIVATED)
    }

    @Test
    @Throws(Exception::class)
    fun createPointWithExistingId() {
        // Create the Point with an existing ID
        point.id = "existing_id"
        val pointDTO = pointMapper.toDto(point)

        val databaseSizeBeforeCreate = pointRepository.findAll().collectList().block().size
        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient.post().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(pointDTO))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Point in the database
        val pointList = pointRepository.findAll().collectList().block()
        assertThat(pointList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Throws(Exception::class)
    fun checkTitleIsRequired() {
        val databaseSizeBeforeTest = pointRepository.findAll().collectList().block().size
        // set the field null
        point.title = null

        // Create the Point, which fails.
        val pointDTO = pointMapper.toDto(point)

        webTestClient.post().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(pointDTO))
            .exchange()
            .expectStatus().isBadRequest

        val pointList = pointRepository.findAll().collectList().block()
        assertThat(pointList).hasSize(databaseSizeBeforeTest)
    }
    @Test
    @Throws(Exception::class)
    fun checkActivatedIsRequired() {
        val databaseSizeBeforeTest = pointRepository.findAll().collectList().block().size
        // set the field null
        point.activated = null

        // Create the Point, which fails.
        val pointDTO = pointMapper.toDto(point)

        webTestClient.post().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(pointDTO))
            .exchange()
            .expectStatus().isBadRequest

        val pointList = pointRepository.findAll().collectList().block()
        assertThat(pointList).hasSize(databaseSizeBeforeTest)
    }

    @Test

    fun getAllPoints() {
        // Initialize the database
        pointRepository.save(point).block()

        // Get all the pointList
        webTestClient.get().uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id").value(hasItem(point.id))
            .jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].activated").value(hasItem(DEFAULT_ACTIVATED))
    }

    @Test

    fun getPoint() {
        // Initialize the database
        pointRepository.save(point).block()

        val id = point.id
        assertNotNull(id)

        // Get the point
        webTestClient.get().uri(ENTITY_API_URL_ID, point.id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id").value(`is`(point.id))
            .jsonPath("$.title").value(`is`(DEFAULT_TITLE))
            .jsonPath("$.description").value(`is`(DEFAULT_DESCRIPTION))
            .jsonPath("$.activated").value(`is`(DEFAULT_ACTIVATED))
    }
    @Test

    fun getNonExistingPoint() {
        // Get the point
        webTestClient.get().uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
    }
    @Test
    fun putExistingPoint() {
        // Initialize the database
        pointRepository.save(point).block()

        val databaseSizeBeforeUpdate = pointRepository.findAll().collectList().block().size

        // Update the point
        val updatedPoint = pointRepository.findById(point.id).block()
        updatedPoint.title = UPDATED_TITLE
        updatedPoint.description = UPDATED_DESCRIPTION
        updatedPoint.activated = UPDATED_ACTIVATED
        val pointDTO = pointMapper.toDto(updatedPoint)

        webTestClient.put().uri(ENTITY_API_URL_ID, pointDTO.id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(pointDTO))
            .exchange()
            .expectStatus().isOk

        // Validate the Point in the database
        val pointList = pointRepository.findAll().collectList().block()
        assertThat(pointList).hasSize(databaseSizeBeforeUpdate)
        val testPoint = pointList[pointList.size - 1]
        assertThat(testPoint.title).isEqualTo(UPDATED_TITLE)
        assertThat(testPoint.description).isEqualTo(UPDATED_DESCRIPTION)
        assertThat(testPoint.activated).isEqualTo(UPDATED_ACTIVATED)
    }

    @Test
    fun putNonExistingPoint() {
        val databaseSizeBeforeUpdate = pointRepository.findAll().collectList().block().size
        point.id = UUID.randomUUID().toString()

        // Create the Point
        val pointDTO = pointMapper.toDto(point)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient.put().uri(ENTITY_API_URL_ID, pointDTO.id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(pointDTO))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Point in the database
        val pointList = pointRepository.findAll().collectList().block()
        assertThat(pointList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun putWithIdMismatchPoint() {
        val databaseSizeBeforeUpdate = pointRepository.findAll().collectList().block().size
        point.id = UUID.randomUUID().toString()

        // Create the Point
        val pointDTO = pointMapper.toDto(point)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient.put().uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(pointDTO))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Point in the database
        val pointList = pointRepository.findAll().collectList().block()
        assertThat(pointList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun putWithMissingIdPathParamPoint() {
        val databaseSizeBeforeUpdate = pointRepository.findAll().collectList().block().size
        point.id = UUID.randomUUID().toString()

        // Create the Point
        val pointDTO = pointMapper.toDto(point)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient.put().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(pointDTO))
            .exchange()
            .expectStatus().isEqualTo(405)

        // Validate the Point in the database
        val pointList = pointRepository.findAll().collectList().block()
        assertThat(pointList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun partialUpdatePointWithPatch() {
        pointRepository.save(point).block()

        val databaseSizeBeforeUpdate = pointRepository.findAll().collectList().block().size

// Update the point using partial update
        val partialUpdatedPoint = Point().apply {
            id = point.id

            title = UPDATED_TITLE
            description = UPDATED_DESCRIPTION
            activated = UPDATED_ACTIVATED
        }

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPoint.id)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(partialUpdatedPoint))
            .exchange()
            .expectStatus()
            .isOk

// Validate the Point in the database
        val pointList = pointRepository.findAll().collectList().block()
        assertThat(pointList).hasSize(databaseSizeBeforeUpdate)
        val testPoint = pointList.last()
        assertThat(testPoint.title).isEqualTo(UPDATED_TITLE)
        assertThat(testPoint.description).isEqualTo(UPDATED_DESCRIPTION)
        assertThat(testPoint.activated).isEqualTo(UPDATED_ACTIVATED)
    }

    @Test
    @Throws(Exception::class)
    fun fullUpdatePointWithPatch() {
        pointRepository.save(point).block()

        val databaseSizeBeforeUpdate = pointRepository.findAll().collectList().block().size

// Update the point using partial update
        val partialUpdatedPoint = Point().apply {
            id = point.id

            title = UPDATED_TITLE
            description = UPDATED_DESCRIPTION
            activated = UPDATED_ACTIVATED
        }

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPoint.id)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(partialUpdatedPoint))
            .exchange()
            .expectStatus()
            .isOk

// Validate the Point in the database
        val pointList = pointRepository.findAll().collectList().block()
        assertThat(pointList).hasSize(databaseSizeBeforeUpdate)
        val testPoint = pointList.last()
        assertThat(testPoint.title).isEqualTo(UPDATED_TITLE)
        assertThat(testPoint.description).isEqualTo(UPDATED_DESCRIPTION)
        assertThat(testPoint.activated).isEqualTo(UPDATED_ACTIVATED)
    }

    @Throws(Exception::class)
    fun patchNonExistingPoint() {
        val databaseSizeBeforeUpdate = pointRepository.findAll().collectList().block().size
        point.id = UUID.randomUUID().toString()

        // Create the Point
        val pointDTO = pointMapper.toDto(point)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient.patch().uri(ENTITY_API_URL_ID, pointDTO.id)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(pointDTO))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Point in the database
        val pointList = pointRepository.findAll().collectList().block()
        assertThat(pointList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun patchWithIdMismatchPoint() {
        val databaseSizeBeforeUpdate = pointRepository.findAll().collectList().block().size
        point.id = UUID.randomUUID().toString()

        // Create the Point
        val pointDTO = pointMapper.toDto(point)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient.patch().uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(pointDTO))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Point in the database
        val pointList = pointRepository.findAll().collectList().block()
        assertThat(pointList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamPoint() {
        val databaseSizeBeforeUpdate = pointRepository.findAll().collectList().block().size
        point.id = UUID.randomUUID().toString()

        // Create the Point
        val pointDTO = pointMapper.toDto(point)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient.patch().uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(pointDTO))
            .exchange()
            .expectStatus().isEqualTo(405)

        // Validate the Point in the database
        val pointList = pointRepository.findAll().collectList().block()
        assertThat(pointList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test

    fun deletePoint() {
        // Initialize the database
        pointRepository.save(point).block()
        val databaseSizeBeforeDelete = pointRepository.findAll().collectList().block().size
        // Delete the point
        webTestClient.delete().uri(ENTITY_API_URL_ID, point.id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent

        // Validate the database contains one less item
        val pointList = pointRepository.findAll().collectList().block()
        assertThat(pointList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_TITLE = "AAAAAAAAAA"
        private const val UPDATED_TITLE = "BBBBBBBBBB"

        private const val DEFAULT_DESCRIPTION = "AAAAAAAAAA"
        private const val UPDATED_DESCRIPTION = "BBBBBBBBBB"

        private const val DEFAULT_ACTIVATED: Boolean = false
        private const val UPDATED_ACTIVATED: Boolean = true

        private val ENTITY_API_URL: String = "/api/points"
        private val ENTITY_API_URL_ID: String = ENTITY_API_URL + "/{id}"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(): Point {
            val point = Point(
                title = DEFAULT_TITLE,

                description = DEFAULT_DESCRIPTION,

                activated = DEFAULT_ACTIVATED

            )

            return point
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(): Point {
            val point = Point(
                title = UPDATED_TITLE,

                description = UPDATED_DESCRIPTION,

                activated = UPDATED_ACTIVATED

            )

            return point
        }
    }
}
