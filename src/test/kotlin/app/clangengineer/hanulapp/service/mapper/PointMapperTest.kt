package app.clangengineer.hanulapp.service.mapper

import org.junit.jupiter.api.BeforeEach

class PointMapperTest {

    private lateinit var pointMapper: PointMapper

    @BeforeEach
    fun setUp() {
        pointMapper = PointMapperImpl()
    }
}
