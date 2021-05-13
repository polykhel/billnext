package com.polykhel.billnext.service.dto

import java.io.Serializable
import java.util.*
import javax.validation.constraints.NotNull

/**
 * A DTO for the [com.polykhel.billnext.domain.WalletGroup] entity.
 */
data class WalletGroupDTO(

    var id: Long? = null,

    @get: NotNull
    var name: String? = null,

    var user: UserDTO? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WalletGroupDTO) return false
        val walletGroupDTO = other
        if (this.id == null) {
            return false
        }
        return Objects.equals(this.id, walletGroupDTO.id)
    }

    override fun hashCode() = Objects.hash(this.id)
}
