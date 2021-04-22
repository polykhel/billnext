package com.polykhel.billnext.service.criteria;

import com.polykhel.billnext.domain.enumeration.ActivityType;
import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.polykhel.billnext.domain.Category} entity. This class is used
 * in {@link com.polykhel.billnext.web.rest.CategoryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /categories?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CategoryCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ActivityType
     */
    public static class ActivityTypeFilter extends Filter<ActivityType> {

        public ActivityTypeFilter() {}

        public ActivityTypeFilter(ActivityTypeFilter filter) {
            super(filter);
        }

        @Override
        public ActivityTypeFilter copy() {
            return new ActivityTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private ActivityTypeFilter type;

    private StringFilter userId;

    private LongFilter activityId;

    public CategoryCriteria() {}

    public CategoryCriteria(CategoryCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.activityId = other.activityId == null ? null : other.activityId.copy();
    }

    @Override
    public CategoryCriteria copy() {
        return new CategoryCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public ActivityTypeFilter getType() {
        return type;
    }

    public ActivityTypeFilter type() {
        if (type == null) {
            type = new ActivityTypeFilter();
        }
        return type;
    }

    public void setType(ActivityTypeFilter type) {
        this.type = type;
    }

    public StringFilter getUserId() {
        return userId;
    }

    public StringFilter userId() {
        if (userId == null) {
            userId = new StringFilter();
        }
        return userId;
    }

    public void setUserId(StringFilter userId) {
        this.userId = userId;
    }

    public LongFilter getActivityId() {
        return activityId;
    }

    public LongFilter activityId() {
        if (activityId == null) {
            activityId = new LongFilter();
        }
        return activityId;
    }

    public void setActivityId(LongFilter activityId) {
        this.activityId = activityId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CategoryCriteria that = (CategoryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(type, that.type) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(activityId, that.activityId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, userId, activityId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CategoryCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (activityId != null ? "activityId=" + activityId + ", " : "") +
            "}";
    }
}
