package app.clangengineer.hanulapp.service.mapper

import app.clangengineer.hanulapp.domain.Board
import app.clangengineer.hanulapp.service.dto.BoardDTO
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface BoardMapper :
    EntityMapper<BoardDTO, Board>
