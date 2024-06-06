package app.clangengineer.hanulapp.web.rest

import app.clangengineer.hanulapp.repository.BoardRepository
import app.clangengineer.hanulapp.service.BoardService
import app.clangengineer.hanulapp.service.dto.BoardDTO
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
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotNull

@RestController
@RequestMapping("/api")
class BoardResource(
    private val boardService: BoardService,
    private val boardRepository: BoardRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "board"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    @PostMapping("/boards")
    fun createBoard(@Valid @RequestBody boardDTO: BoardDTO): Mono<ResponseEntity<BoardDTO>> {
        log.debug("REST request to save Board : $boardDTO")
        if (boardDTO.id != null) {
            throw BadRequestAlertException(
                "A new point cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        return boardService.save(boardDTO)
            .map { result ->
                try {
                    ResponseEntity.created(URI("/api/boards/${result.id}"))
                        .headers(
                            HeaderUtil.createEntityCreationAlert(
                                applicationName,
                                true,
                                ENTITY_NAME,
                                result.id
                            )
                        )
                        .body(result)
                } catch (e: URISyntaxException) {
                    throw RuntimeException(e)
                }
            }
    }

    @PutMapping("/boards/{id}")
    fun updateBoard(
        @PathVariable(value = "id", required = false) id: String,
        @Valid @RequestBody boardDTO: BoardDTO
    ): Mono<ResponseEntity<BoardDTO>> {
        log.debug("REST request to update Board : {}, {}", id, boardDTO)
        if (boardDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, boardDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        return boardRepository.existsById(id).flatMap {
            if (!it) {
                return@flatMap Mono.error(
                    BadRequestAlertException(
                        "Entity not found",
                        ENTITY_NAME,
                        "idnotfound"
                    )
                )
            }

            boardService.update(boardDTO)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map { result ->
                    ResponseEntity.ok()
                        .headers(
                            HeaderUtil.createEntityUpdateAlert(
                                applicationName,
                                true,
                                ENTITY_NAME,
                                result.id
                            )
                        )
                        .body(result)
                }
        }
    }

    @PatchMapping(value = ["/boards/{id}"], consumes = ["application/json", "application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateBoard(
        @PathVariable(value = "id", required = false) id: String,
        @NotNull @RequestBody boardDTO: BoardDTO
    ): Mono<ResponseEntity<BoardDTO>> {
        log.debug("REST request to partial update Board partially : {}, {}", id, boardDTO)
        if (boardDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, boardDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        return boardRepository.existsById(id).flatMap {
            if (!it) {
                return@flatMap Mono.error(
                    BadRequestAlertException(
                        "Entity not found",
                        ENTITY_NAME,
                        "idnotfound"
                    )
                )
            }

            val result = boardService.partialUpdate(boardDTO)

            result
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map {
                    ResponseEntity.ok()
                        .headers(
                            HeaderUtil.createEntityUpdateAlert(
                                applicationName,
                                true,
                                ENTITY_NAME,
                                it.id
                            )
                        )
                        .body(it)
                }
        }
    }

    @GetMapping("/boards")
    fun getAllBoards(@org.springdoc.api.annotations.ParameterObject pageable: Pageable, request: ServerHttpRequest): Mono<ResponseEntity<List<BoardDTO>>> {

        log.debug("REST request to get a page of Points")
        return boardService.countAll()
            .zipWith(boardService.findAll(pageable).collectList())
            .map {
                ResponseEntity.ok().headers(
                    PaginationUtil.generatePaginationHttpHeaders(
                        UriComponentsBuilder.fromHttpRequest(request),
                        PageImpl(it.t2, pageable, it.t1)
                    )
                ).body(it.t2)
            }
    }

    @GetMapping("/boards/{id}")
    fun getBoard(@PathVariable id: String): Mono<ResponseEntity<BoardDTO>> {
        log.debug("REST request to get Board : $id")
        val pointDTO = boardService.findOne(id)
        return ResponseUtil.wrapOrNotFound(pointDTO)
    }

    @DeleteMapping("/boards/{id}")
    fun deletePoint(@PathVariable id: String): Mono<ResponseEntity<Void>> {
        log.debug("REST request to delete Point : $id")
        return boardService.delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(
                            HeaderUtil.createEntityDeletionAlert(
                                applicationName,
                                true,
                                ENTITY_NAME,
                                id
                            )
                        ).build<Void>()
                )
            )
    }
}
