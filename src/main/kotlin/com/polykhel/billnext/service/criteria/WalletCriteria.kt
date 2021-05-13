package com.polykhel.billnext.service.criteria

import tech.jhipster.service.Criteria
import tech.jhipster.service.filter.BigDecimalFilter
import tech.jhipster.service.filter.LongFilter
import tech.jhipster.service.filter.StringFilter
import java.io.Serializable

/**
 * Criteria class for the [com.polykhel.billnext.domain.Wallet] entity. This class is used in
 * [com.polykhel.billnext.web.rest.WalletResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/wallets?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class WalletCriteria(

    var id: LongFilter? = null,

    var name: StringFilter? = null,

    var amount: BigDecimalFilter? = null,

    var currency: StringFilter? = null,

    var remarks: StringFilter? = null,

    var walletGroupId: LongFilter? = null,

    var activityId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: WalletCriteria) :
        this(
            other.id?.copy(),
            other.name?.copy(),
            other.amount?.copy(),
            other.currency?.copy(),
            other.remarks?.copy(),
            other.walletGroupId?.copy(),
            other.activityId?.copy()
        )

    override fun copy() = WalletCriteria(this)

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
