package com.polykhel.billnext.service.criteria

import tech.jhipster.service.Criteria
import tech.jhipster.service.filter.LongFilter
import tech.jhipster.service.filter.StringFilter
import java.io.Serializable

/**
 * Criteria class for the [com.polykhel.billnext.domain.WalletGroup] entity. This class is used in
 * [com.polykhel.billnext.web.rest.WalletGroupResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/wallet-groups?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class WalletGroupCriteria(

    var id: LongFilter? = null,

    var name: StringFilter? = null,

    var userId: LongFilter? = null,

    var walletsId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: WalletGroupCriteria) :
        this(
            other.id?.copy(),
            other.name?.copy(),
            other.userId?.copy(),
            other.walletsId?.copy()
        )

    override fun copy() = WalletGroupCriteria(this)

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
