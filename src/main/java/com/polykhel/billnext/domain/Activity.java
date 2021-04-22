package com.polykhel.billnext.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.polykhel.billnext.domain.enumeration.ActivityType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Activity.
 */
@Entity
@Table(name = "activity")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Activity extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private ZonedDateTime date;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "remarks")
    private String remarks;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ActivityType type;

    @ManyToOne(optional = false)
    @NotNull
    private User user;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "walletGroup", "activities" }, allowSetters = true)
    private Wallet wallet;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "user", "activities" }, allowSetters = true)
    private Category category;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Activity id(Long id) {
        this.id = id;
        return this;
    }

    public ZonedDateTime getDate() {
        return this.date;
    }

    public Activity date(ZonedDateTime date) {
        this.date = date;
        return this;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Activity amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public Activity remarks(String remarks) {
        this.remarks = remarks;
        return this;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public ActivityType getType() {
        return this.type;
    }

    public Activity type(ActivityType type) {
        this.type = type;
        return this;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public User getUser() {
        return this.user;
    }

    public Activity user(User user) {
        this.setUser(user);
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Wallet getWallet() {
        return this.wallet;
    }

    public Activity wallet(Wallet wallet) {
        this.setWallet(wallet);
        return this;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public Category getCategory() {
        return this.category;
    }

    public Activity category(Category category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Activity)) {
            return false;
        }
        return id != null && id.equals(((Activity) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Activity{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", amount=" + getAmount() +
            ", remarks='" + getRemarks() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
