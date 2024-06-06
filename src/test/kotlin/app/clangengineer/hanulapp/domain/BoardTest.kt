package app.clangengineer.hanulapp.domain

import app.clangengineer.hanulapp.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BoardTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Board::class)
        val board1 = Board()
        board1.id = "id1"
        val board2 = Board()
        board2.id = board1.id
        assertThat(board1).isEqualTo(board2)

        board2.id = "id2"
        assertThat(board1).isNotEqualTo(board2)
        board1.id = null
        assertThat(board1).isNotEqualTo(board2)
    }
}
