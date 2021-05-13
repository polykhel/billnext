package com.polykhel.billnext.service.dto

import com.polykhel.billnext.domain.enumeration.ActivityType
import java.io.Serializable
import java.util.*
import javax.validation.constraints.NotNull

/**
 * A DTO for the [com.polykhel.billnext.domain.Category] entity.
 */
data class CategoryDTO(

    var id: Long? = null,

    @get: NotNull
    var name: String? = null,

    var type: ActivityType? = null,

    var user: UserDTO? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CategoryDTO) return false
        val categoryDTO = other
        if (this.id == null) {
            return false
        }
        return Objects.equals(this.id, categoryDTO.id)
    }

    override fun hashCode() = Objects.hash(this.id)
}
