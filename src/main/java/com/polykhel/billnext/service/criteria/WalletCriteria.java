package com.polykhel.billnext.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BigDecimalFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.polykhel.billnext.domain.Wallet} entity. This class is used
 * in {@link com.polykhel.billnext.web.rest.WalletResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /wallets?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class WalletCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private BigDecimalFilter amount;

    private StringFilter currency;

    private StringFilter remarks;

    private LongFilter walletGroupId;

    private LongFilter activityId;

    public WalletCriteria() {}

    public WalletCriteria(WalletCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.amount = other.amount == null ? null : other.amount.copy();
        this.currency = other.currency == null ? null : other.currency.copy();
        this.remarks = other.remarks == null ? null : other.remarks.copy();
        this.walletGroupId = other.walletGroupId == null ? null : other.walletGroupId.copy();
        this.activityId = other.activityId == null ? null : other.activityId.copy();
    }

    @Override
    public WalletCriteria copy() {
        return new WalletCriteria(this);
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

    public StringFilter getCurrency() {
        return currency;
    }

    public StringFilter currency() {
        if (currency == null) {
            currency = new StringFilter();
        }
        return currency;
    }

    public void setCurrency(StringFilter currency) {
        this.currency = currency;
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

    public LongFilter getWalletGroupId() {
        return walletGroupId;
    }

    public LongFilter walletGroupId() {
        if (walletGroupId == null) {
            walletGroupId = new LongFilter();
        }
        return walletGroupId;
    }

    public void setWalletGroupId(LongFilter walletGroupId) {
        this.walletGroupId = walletGroupId;
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
        final WalletCriteria that = (WalletCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(currency, that.currency) &&
            Objects.equals(remarks, that.remarks) &&
            Objects.equals(walletGroupId, that.walletGroupId) &&
            Objects.equals(activityId, that.activityId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, amount, currency, remarks, walletGroupId, activityId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WalletCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (amount != null ? "amount=" + amount + ", " : "") +
            (currency != null ? "currency=" + currency + ", " : "") +
            (remarks != null ? "remarks=" + remarks + ", " : "") +
            (walletGroupId != null ? "walletGroupId=" + walletGroupId + ", " : "") +
            (activityId != null ? "activityId=" + activityId + ", " : "") +
            "}";
    }
}
