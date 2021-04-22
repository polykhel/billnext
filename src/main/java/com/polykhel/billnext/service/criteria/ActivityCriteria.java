package com.polykhel.billnext.service.criteria;

import com.polykhel.billnext.domain.enumeration.ActivityType;
import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.polykhel.billnext.domain.Activity} entity. This class is used
 * in {@link com.polykhel.billnext.web.rest.ActivityResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /activities?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ActivityCriteria implements Serializable, Criteria {

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

    private ZonedDateTimeFilter date;

    private BigDecimalFilter amount;

    private StringFilter remarks;

    private ActivityTypeFilter type;

    private StringFilter userId;

    private LongFilter walletId;

    private LongFilter categoryId;

    public ActivityCriteria() {}

    public ActivityCriteria(ActivityCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.amount = other.amount == null ? null : other.amount.copy();
        this.remarks = other.remarks == null ? null : other.remarks.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.walletId = other.walletId == null ? null : other.walletId.copy();
        this.categoryId = other.categoryId == null ? null : other.categoryId.copy();
    }

    @Override
    public ActivityCriteria copy() {
        return new ActivityCriteria(this);
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

    public ZonedDateTimeFilter getDate() {
        return date;
    }

    public ZonedDateTimeFilter date() {
        if (date == null) {
            date = new ZonedDateTimeFilter();
        }
        return date;
    }

    public void setDate(ZonedDateTimeFilter date) {
        this.date = date;
    }

    public BigDecimalFilter getAmount() {
        return amount;
    }

    public BigDecimalFilter amount() {
        if (amount == null) {
            amount = new BigDecimalFilter();
        }
        return amount;
    }

    public void setAmount(BigDecimalFilter amount) {
        this.amount = amount;
    }

    public StringFilter getRemarks() {
        return remarks;
    }

    public StringFilter remarks() {
        if (remarks == null) {
            remarks = new StringFilter();
        }
        return remarks;
    }

    public void setRemarks(StringFilter remarks) {
        this.remarks = remarks;
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

    public LongFilter getWalletId() {
        return walletId;
    }

    public LongFilter walletId() {
        if (walletId == null) {
            walletId = new LongFilter();
        }
        return walletId;
    }

    public void setWalletId(LongFilter walletId) {
        this.walletId = walletId;
    }

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public LongFilter categoryId() {
        if (categoryId == null) {
            categoryId = new LongFilter();
        }
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ActivityCriteria that = (ActivityCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(date, that.date) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(remarks, that.remarks) &&
            Objects.equals(type, that.type) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(walletId, that.walletId) &&
            Objects.equals(categoryId, that.categoryId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, amount, remarks, type, userId, walletId, categoryId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ActivityCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (date != null ? "date=" + date + ", " : "") +
            (amount != null ? "amount=" + amount + ", " : "") +
            (remarks != null ? "remarks=" + remarks + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (walletId != null ? "walletId=" + walletId + ", " : "") +
            (categoryId != null ? "categoryId=" + categoryId + ", " : "") +
            "}";
    }
}
