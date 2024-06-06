package app.clangengineer.hanulapp.service.dto

import java.io.Serializable
import java.util.*
import javax.validation.constraints.*

@SuppressWarnings("common-java:DuplicatedBlocks")
data class BoardDTO(

    var id: String? = null,

    @get: NotNull(message = "must not be null")
    @get: Size(min = 5, max = 50)
    var title: String? = null,

    var description: String? = null,

    @get: NotNull(message = "must not be null")
    var activated: Boolean? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BoardDTO) return false
        val boardDTO = other
        if (this.id == null) {
            return false
        }
        return Objects.equals(this.id, boardDTO.id)
    }

    override fun hashCode() = Objects.hash(this.id)
}
