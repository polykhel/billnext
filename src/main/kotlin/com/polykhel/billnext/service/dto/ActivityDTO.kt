package com.polykhel.billnext.service.dto

import com.polykhel.billnext.domain.enumeration.ActivityType
import java.io.Serializable
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.*
import javax.validation.constraints.NotNull

/**
 * A DTO for the [com.polykhel.billnext.domain.Activity] entity.
 */
data class ActivityDTO(

    var id: Long? = null,

    @get: NotNull
    var date: ZonedDateTime? = null,

    @get: NotNull
    var amount: BigDecimal? = null,

    var remarks: String? = null,

    @get: NotNull
    var type: ActivityType? = null,

    var user: UserDTO? = null,

    var wallet: WalletDTO? = null,

    var category: CategoryDTO? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ActivityDTO) return false
        if (this.id == null) {
            return false
        }
        return Objects.equals(this.id, other.id)
    }

    override fun hashCode() = Objects.hash(this.id)
}
