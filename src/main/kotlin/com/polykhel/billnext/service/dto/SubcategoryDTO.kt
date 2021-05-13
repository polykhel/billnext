package com.polykhel.billnext.service.dto

import java.io.Serializable
import java.util.*
import javax.validation.constraints.NotNull

/**
 * A DTO for the [com.polykhel.billnext.domain.Subcategory] entity.
 */
data class SubcategoryDTO(

    var id: Long? = null,

    @get: NotNull
    var name: String? = null,

    var category: CategoryDTO? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SubcategoryDTO) return false
        val subcategoryDTO = other
        if (this.id == null) {
            return false
        }
        return Objects.equals(this.id, subcategoryDTO.id)
    }

    override fun hashCode() = Objects.hash(this.id)
}
