package app.clangengineer.hanulapp.service

import app.clangengineer.hanulapp.domain.Point
import app.clangengineer.hanulapp.repository.PointRepository
import app.clangengineer.hanulapp.service.dto.PointDTO
import app.clangengineer.hanulapp.service.mapper.PointMapper
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service Implementation for managing [Point].
 */
@Service
class PointService(
    private val pointRepository: PointRepository,
    private val pointMapper: PointMapper,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a point.
     *
     * @param pointDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(pointDTO: PointDTO): Mono<PointDTO> {
        log.debug("Request to save Point : $pointDTO")
        return pointRepository.save(pointMapper.toEntity(pointDTO))
            .map(pointMapper::toDto)
    }

    /**
     * Update a point.
     *
     * @param pointDTO the entity to save.
     * @return the persisted entity.
     */
    fun update(pointDTO: PointDTO): Mono<PointDTO> {
        log.debug("Request to update Point : {}", pointDTO)
        return pointRepository.save(pointMapper.toEntity(pointDTO))
            .map(pointMapper::toDto)
    }

    /**
     * Partially updates a point.
     *
     * @param pointDTO the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(pointDTO: PointDTO): Mono<PointDTO> {
        log.debug("Request to partially update Point : {}", pointDTO)

        return pointRepository.findById(pointDTO.id)
            .map {
                pointMapper.partialUpdate(it, pointDTO)
                it
            }
            .flatMap { pointRepository.save(it) }
            .map { pointMapper.toDto(it) }
    }

    /**
     * Get all the points.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun findAll(pageable: Pageable): Flux<PointDTO> {
        log.debug("Request to get all Points")
        return pointRepository.findAllBy(pageable)
            .map(pointMapper::toDto)
    }

    /**
     * Returns the number of points available.
     * @return the number of entities in the database.
     */
    fun countAll() = pointRepository.count()

    /**
     * Get one point by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: String): Mono<PointDTO> {
        log.debug("Request to get Point : $id")
        return pointRepository.findById(id)
            .map(pointMapper::toDto)
    }

    /**
     * Delete the point by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    fun delete(id: String): Mono<Void> {
        log.debug("Request to delete Point : $id")
        return pointRepository.deleteById(id)
    }
}
