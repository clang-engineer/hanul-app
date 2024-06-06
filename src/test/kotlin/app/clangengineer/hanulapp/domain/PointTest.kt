package app.clangengineer.hanulapp.domain

import app.clangengineer.hanulapp.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Point::class)
        val point1 = Point()
        point1.id = "id1"
        val point2 = Point()
        point2.id = point1.id
        assertThat(point1).isEqualTo(point2)
        point2.id = "id2"
        assertThat(point1).isNotEqualTo(point2)
        point1.id = null
        assertThat(point1).isNotEqualTo(point2)
    }
}
