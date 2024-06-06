package app.clangengineer.hanulapp.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable
import javax.validation.constraints.*

/**
 * A Point.
 */
@Document(collection = "tbl_point")
@SuppressWarnings("common-java:DuplicatedBlocks")
data class Point(

    @Id
    var id: String? = null,

    @get: NotNull(message = "must not be null")
    @get: Size(min = 5, max = 50)
    @Field("title")
    var title: String? = null,
    @Field("description")
    var description: String? = null,

    @get: NotNull(message = "must not be null")
    @Field("activated")
    var activated: Boolean? = null,

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Point) return false
        return id != null && other.id != null && id == other.id
    }

    override fun toString(): String {
        return "Point{" +
            "id=" + id +
            ", title='" + title + "'" +
            ", description='" + description + "'" +
            ", activated='" + activated + "'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
