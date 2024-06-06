package app.clangengineer.hanulapp.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable
import java.time.Instant

/**
 * Base abstract class for entities which will hold definitions for created, last modified by, created by,
 * last modified by attributes.
 */
@JsonIgnoreProperties(value = [ "createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate" ], allowGetters = true)
abstract class AbstractAuditingEntity<T>(
    @Field("created_by")
    open var createdBy: String? = null,

    @CreatedDate
    @Field("created_date")
    open var createdDate: Instant? = Instant.now(),

    @Field("last_modified_by")
    open var lastModifiedBy: String? = null,

    @LastModifiedDate
    @Field("last_modified_date")
    open var lastModifiedDate: Instant? = Instant.now()
) : Serializable {

    abstract val id: T?

    companion object {
        private const val serialVersionUID = 1L
    }
}
