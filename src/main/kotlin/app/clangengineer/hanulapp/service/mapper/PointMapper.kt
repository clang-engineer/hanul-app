package app.clangengineer.hanulapp.service.mapper

import app.clangengineer.hanulapp.domain.Point
import app.clangengineer.hanulapp.service.dto.PointDTO
import org.mapstruct.*

/**
 * Mapper for the entity [Point] and its DTO [PointDTO].
 */
@Mapper(componentModel = "spring")
interface PointMapper :
    EntityMapper<PointDTO, Point>
