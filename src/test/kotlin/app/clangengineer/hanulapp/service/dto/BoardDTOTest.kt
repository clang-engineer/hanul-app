package app.clangengineer.hanulapp.service.dto

import app.clangengineer.hanulapp.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BoardDTOTest {

    @Test
    fun dtoEqualsVerifier() {
        equalsVerifier(BoardDTO::class)
        val boardDTO1 = BoardDTO()
        boardDTO1.id = "id1"
        val boardDTO2 = BoardDTO()

        assertThat(boardDTO1).isNotEqualTo(boardDTO2)

        boardDTO2.id = boardDTO1.id
        assertThat(boardDTO1).isEqualTo(boardDTO2)

        boardDTO2.id = "id2"
        assertThat(boardDTO1).isNotEqualTo(boardDTO2)

        boardDTO1.id = null
        assertThat(boardDTO1).isNotEqualTo(boardDTO2)
    }
}
