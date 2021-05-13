package com.polykhel.billnext.service.dto

import java.io.Serializable
import java.math.BigDecimal
import java.util.*
import javax.validation.constraints.NotNull

/**
 * A DTO for the [com.polykhel.billnext.domain.Wallet] entity.
 */
data class WalletDTO(

    var id: Long? = null,

    @get: NotNull
    var name: String? = null,

    @get: NotNull
    var amount: BigDecimal? = null,

    var currency: String? = null,

    var remarks: String? = null,

    var walletGroup: WalletGroupDTO? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WalletDTO) return false
        val walletDTO = other
        if (this.id == null) {
            return false
        }
        return Objects.equals(this.id, walletDTO.id)
    }

    override fun hashCode() = Objects.hash(this.id)
}
