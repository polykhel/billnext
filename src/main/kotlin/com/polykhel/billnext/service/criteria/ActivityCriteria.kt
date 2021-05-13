package com.polykhel.billnext.service.criteria

import com.polykhel.billnext.domain.enumeration.ActivityType
import tech.jhipster.service.Criteria
import tech.jhipster.service.filter.*
import java.io.Serializable

/**
 * Criteria class for the [com.polykhel.billnext.domain.Activity] entity. This class is used in
 * [com.polykhel.billnext.web.rest.ActivityResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/activities?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class ActivityCriteria(

    var id: LongFilter? = null,

    var date: ZonedDateTimeFilter? = null,

    var amount: BigDecimalFilter? = null,

    var remarks: StringFilter? = null,

    var type: ActivityTypeFilter? = null,

    var userId: LongFilter? = null,

    var walletId: LongFilter? = null,

    var categoryId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: ActivityCriteria) :
        this(
            other.id?.copy(),
            other.date?.copy(),
            other.amount?.copy(),
            other.remarks?.copy(),
            other.type?.copy(),
            other.userId?.copy(),
            other.walletId?.copy(),
            other.categoryId?.copy()
        )

    /**
     * Class for filtering ActivityType
     */
    class ActivityTypeFilter : Filter<ActivityType> {
        constructor()

        constructor(filter: ActivityTypeFilter) : super(filter)

        override fun copy() = ActivityTypeFilter(this)
    }

    override fun copy() = ActivityCriteria(this)

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
