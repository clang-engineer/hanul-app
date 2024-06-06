package app.clangengineer.hanulapp.service.dto

import java.io.Serializable
import java.util.Objects
import javax.validation.constraints.*

/**
 * A DTO for the [app.clangengineer.hanulapp.domain.Point] entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
data class PointDTO(

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
        if (other !is PointDTO) return false
        val pointDTO = other
        if (this.id == null) {
            return false
        }
        return Objects.equals(this.id, pointDTO.id)
    }

    override fun hashCode() = Objects.hash(this.id)
}
