package app.clangengineer.hanulapp.repository

import app.clangengineer.hanulapp.domain.Point
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

/**
* Spring Data MongoDB reactive repository for the Point entity.
*/
@SuppressWarnings("unused")
@Repository
interface PointRepository : ReactiveMongoRepository<Point, String> {

    fun findAllBy(pageable: Pageable?): Flux<Point>
}
