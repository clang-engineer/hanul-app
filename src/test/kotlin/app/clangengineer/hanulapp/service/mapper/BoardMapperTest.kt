package app.clangengineer.hanulapp.service.mapper

import org.junit.jupiter.api.BeforeEach

class BoardMapperTest {

    private lateinit var boardMapper: BoardMapper

    @BeforeEach
    fun setUp() {
        boardMapper = BoardMapperImpl()
    }
}
