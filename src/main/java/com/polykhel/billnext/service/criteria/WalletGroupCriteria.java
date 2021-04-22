package com.polykhel.billnext.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.polykhel.billnext.domain.WalletGroup} entity. This class is used
 * in {@link com.polykhel.billnext.web.rest.WalletGroupResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /wallet-groups?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class WalletGroupCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter userId;

    private LongFilter walletsId;

    public WalletGroupCriteria() {}

    public WalletGroupCriteria(WalletGroupCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.walletsId = other.walletsId == null ? null : other.walletsId.copy();
    }

    @Override
    public WalletGroupCriteria copy() {
        return new WalletGroupCriteria(this);
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

    public LongFilter getWalletsId() {
        return walletsId;
    }

    public LongFilter walletsId() {
        if (walletsId == null) {
            walletsId = new LongFilter();
        }
        return walletsId;
    }

    public void setWalletsId(LongFilter walletsId) {
        this.walletsId = walletsId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final WalletGroupCriteria that = (WalletGroupCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(walletsId, that.walletsId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, userId, walletsId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WalletGroupCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (walletsId != null ? "walletsId=" + walletsId + ", " : "") +
            "}";
    }
}
