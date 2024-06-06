package app.clangengineer.hanulapp.service.dto

import app.clangengineer.hanulapp.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointDTOTest {

    @Test
    fun dtoEqualsVerifier() {
        equalsVerifier(PointDTO::class)
        val pointDTO1 = PointDTO()
        pointDTO1.id = "id1"
        val pointDTO2 = PointDTO()
        assertThat(pointDTO1).isNotEqualTo(pointDTO2)
        pointDTO2.id = pointDTO1.id
        assertThat(pointDTO1).isEqualTo(pointDTO2)
        pointDTO2.id = "id2"
        assertThat(pointDTO1).isNotEqualTo(pointDTO2)
        pointDTO1.id = null
        assertThat(pointDTO1).isNotEqualTo(pointDTO2)
    }
}
