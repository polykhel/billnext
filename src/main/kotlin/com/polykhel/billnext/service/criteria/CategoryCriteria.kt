package com.polykhel.billnext.service.criteria

import com.polykhel.billnext.domain.enumeration.ActivityType
import tech.jhipster.service.Criteria
import tech.jhipster.service.filter.Filter
import tech.jhipster.service.filter.LongFilter
import tech.jhipster.service.filter.StringFilter
import java.io.Serializable

/**
 * Criteria class for the [com.polykhel.billnext.domain.Category] entity. This class is used in
 * [com.polykhel.billnext.web.rest.CategoryResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/categories?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class CategoryCriteria(

    var id: LongFilter? = null,

    var name: StringFilter? = null,

    var type: ActivityTypeFilter? = null,

    var userId: LongFilter? = null,

    var activityId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: CategoryCriteria) :
        this(
            other.id?.copy(),
            other.name?.copy(),
            other.type?.copy(),
            other.userId?.copy(),
            other.activityId?.copy()
        )

    /**
     * Class for filtering ActivityType
     */
    class ActivityTypeFilter : Filter<ActivityType> {
        constructor()

        constructor(filter: ActivityTypeFilter) : super(filter)

        override fun copy() = ActivityTypeFilter(this)
    }

    override fun copy() = CategoryCriteria(this)

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
