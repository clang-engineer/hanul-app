package app.clangengineer.hanulapp.service

import app.clangengineer.hanulapp.repository.BoardRepository
import app.clangengineer.hanulapp.service.dto.BoardDTO
import app.clangengineer.hanulapp.service.mapper.BoardMapper
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class BoardService(
    private val boardRepository: BoardRepository,
    private val boardMapper: BoardMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun save(boardDTO: BoardDTO): Mono<BoardDTO> {
        log.debug("Request to save Board : $boardDTO")
        return boardRepository.save(boardMapper.toEntity(boardDTO))
            .map(boardMapper::toDto)
    }

    fun update(boardDTO: BoardDTO): Mono<BoardDTO> {
        log.debug("Request to update Board : {}", boardDTO)
        return boardRepository.save(boardMapper.toEntity(boardDTO))
            .map(boardMapper::toDto)
    }

    fun partialUpdate(boardDTO: BoardDTO): Mono<BoardDTO> {
        log.debug("Request to partially update Board : {}", boardDTO)

        return boardRepository.findById(boardDTO.id)
            .map {
                boardMapper.partialUpdate(it, boardDTO)
                it
            }
            .flatMap { boardRepository.save(it) }
            .map { boardMapper.toDto(it) }
    }

    fun findAll(pageable: Pageable): Flux<BoardDTO> {
        log.debug("Request to get all Boards")
        return boardRepository.findAllBy(pageable)
            .map(boardMapper::toDto)
    }

    fun countAll() = boardRepository.count()

    fun findOne(id: String): Mono<BoardDTO> {
        log.debug("Request to get Board : $id")
        return boardRepository.findById(id)
            .map(boardMapper::toDto)
    }

    fun delete(id: String): Mono<Void> {
        log.debug("Request to delete Board : $id")
        return boardRepository.deleteById(id)
    }
}
