package app.clangengineer.hanulapp.repository

import app.clangengineer.hanulapp.domain.Board
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@SuppressWarnings("unused")
@Repository
interface BoardRepository : ReactiveMongoRepository<Board, String> {

    fun findAllBy(pageable: Pageable?): Flux<Board>
}
