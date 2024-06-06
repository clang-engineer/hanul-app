package app.clangengineer.hanulapp.web.rest

import app.clangengineer.hanulapp.repository.PointRepository
import app.clangengineer.hanulapp.service.PointService
import app.clangengineer.hanulapp.service.dto.PointDTO
import app.clangengineer.hanulapp.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import tech.jhipster.web.util.HeaderUtil
import tech.jhipster.web.util.PaginationUtil
import tech.jhipster.web.util.reactive.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import java.util.Objects
import javax.validation.Valid
import javax.validation.constraints.NotNull

private const val ENTITY_NAME = "point"
/**
 * REST controller for managing [app.clangengineer.hanulapp.domain.Point].
 */
@RestController
@RequestMapping("/api")
class PointResource(
    private val pointService: PointService,
    private val pointRepository: PointRepository,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "point"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /points` : Create a new point.
     *
     * @param pointDTO the pointDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new pointDTO, or with status `400 (Bad Request)` if the point has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/points")
    fun createPoint(@Valid @RequestBody pointDTO: PointDTO): Mono<ResponseEntity<PointDTO>> {
        log.debug("REST request to save Point : $pointDTO")
        if (pointDTO.id != null) {
            throw BadRequestAlertException(
                "A new point cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        return pointService.save(pointDTO)
            .map { result ->
                try {
                    ResponseEntity.created(URI("/api/points/${result.id}"))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id))
                        .body(result)
                } catch (e: URISyntaxException) {
                    throw RuntimeException(e)
                }
            }
    }

    /**
     * {@code PUT  /points/:id} : Updates an existing point.
     *
     * @param id the id of the pointDTO to save.
     * @param pointDTO the pointDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated pointDTO,
     * or with status `400 (Bad Request)` if the pointDTO is not valid,
     * or with status `500 (Internal Server Error)` if the pointDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/points/{id}")
    fun updatePoint(
        @PathVariable(value = "id", required = false) id: String,
        @Valid @RequestBody pointDTO: PointDTO
    ): Mono<ResponseEntity<PointDTO>> {
        log.debug("REST request to update Point : {}, {}", id, pointDTO)
        if (pointDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, pointDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        return pointRepository.existsById(id).flatMap {
            if (!it) {
                return@flatMap Mono.error(BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"))
            }

            pointService.update(pointDTO)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map { result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.id))
                        .body(result)
                }
        }
    }

    /**
     * {@code PATCH  /points/:id} : Partial updates given fields of an existing point, field will ignore if it is null
     *
     * @param id the id of the pointDTO to save.
     * @param pointDTO the pointDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pointDTO,
     * or with status {@code 400 (Bad Request)} if the pointDTO is not valid,
     * or with status {@code 404 (Not Found)} if the pointDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the pointDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/points/{id}"], consumes = ["application/json", "application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdatePoint(
        @PathVariable(value = "id", required = false) id: String,
        @NotNull @RequestBody pointDTO: PointDTO
    ): Mono<ResponseEntity<PointDTO>> {
        log.debug("REST request to partial update Point partially : {}, {}", id, pointDTO)
        if (pointDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, pointDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        return pointRepository.existsById(id).flatMap {
            if (!it) {
                return@flatMap Mono.error(BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"))
            }

            val result = pointService.partialUpdate(pointDTO)

            result
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map {
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, it.id))
                        .body(it)
                }
        }
    }

    /**
     * `GET  /points` : get all the points.
     *
     * @param pageable the pagination information.
     * @param request a [ServerHttpRequest] request.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of points in body.
     */
    @GetMapping("/points")
    fun getAllPoints(@org.springdoc.api.annotations.ParameterObject pageable: Pageable, request: ServerHttpRequest): Mono<ResponseEntity<List<PointDTO>>> {

        log.debug("REST request to get a page of Points")
        return pointService.countAll()
            .zipWith(pointService.findAll(pageable).collectList())
            .map {
                ResponseEntity.ok().headers(
                    PaginationUtil.generatePaginationHttpHeaders(
                        UriComponentsBuilder.fromHttpRequest(request),
                        PageImpl(it.t2, pageable, it.t1)
                    )
                ).body(it.t2)
            }
    }

    /**
     * `GET  /points/:id` : get the "id" point.
     *
     * @param id the id of the pointDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the pointDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/points/{id}")
    fun getPoint(@PathVariable id: String): Mono<ResponseEntity<PointDTO>> {
        log.debug("REST request to get Point : $id")
        val pointDTO = pointService.findOne(id)
        return ResponseUtil.wrapOrNotFound(pointDTO)
    }
    /**
     *  `DELETE  /points/:id` : delete the "id" point.
     *
     * @param id the id of the pointDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/points/{id}")
    fun deletePoint(@PathVariable id: String): Mono<ResponseEntity<Void>> {
        log.debug("REST request to delete Point : $id")
        return pointService.delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build<Void>()
                )
            )
    }
}
