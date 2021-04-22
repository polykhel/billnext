package com.polykhel.billnext.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Wallet.
 */
@Entity
@Table(name = "wallet")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Wallet extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "remarks")
    private String remarks;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "user", "wallets" }, allowSetters = true)
    private WalletGroup walletGroup;

    @OneToMany(mappedBy = "wallet")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "wallet", "category" }, allowSetters = true)
    private Set<Activity> activities = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Wallet id(Long id) {
        this.id = id;
        return this;
    }

    public WalletGroup getWalletGroup() {
        return this.walletGroup;
    }

    public Wallet walletGroup(WalletGroup walletGroup) {
        this.walletGroup = walletGroup;
        return this;
    }

    public void setWalletGroup(WalletGroup walletGroup) {
        this.walletGroup = walletGroup;
    }

    public String getName() {
        return this.name;
    }

    public Wallet name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Wallet amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return this.currency;
    }

    public Wallet currency(String currency) {
        this.currency = currency;
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public Wallet remarks(String remarks) {
        this.remarks = remarks;
        return this;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Set<Activity> getActivities() {
        return this.activities;
    }

    public Wallet activities(Set<Activity> activities) {
        this.setActivities(activities);
        return this;
    }

    public Wallet addActivity(Activity activity) {
        this.activities.add(activity);
        activity.setWallet(this);
        return this;
    }

    public Wallet removeActivity(Activity activity) {
        this.activities.remove(activity);
        activity.setWallet(null);
        return this;
    }

    public void setActivities(Set<Activity> activities) {
        if (this.activities != null) {
            this.activities.forEach(i -> i.setWallet(null));
        }
        if (activities != null) {
            activities.forEach(i -> i.setWallet(this));
        }
        this.activities = activities;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Wallet)) {
            return false;
        }
        return id != null && id.equals(((Wallet) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Wallet{" +
            "id=" + getId() +
            ", walletGroup='" + getWalletGroup() + "'" +
            ", name='" + getName() + "'" +
            ", amount=" + getAmount() +
            ", currency='" + getCurrency() + "'" +
            ", remarks='" + getRemarks() + "'" +
            "}";
    }
}
