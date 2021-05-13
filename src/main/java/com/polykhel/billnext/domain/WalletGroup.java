package com.polykhel.billnext.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A WalletGroup.
 */
@Entity
@Table(name = "wallet_group")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WalletGroup extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(optional = false)
    @NotNull
    private User user;

    @OneToMany(mappedBy = "walletGroup", fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "walletGroup", "activities" }, allowSetters = true)
    private Set<Wallet> wallets = new HashSet<>();

    @Column(name = "order_index")
    private Integer orderIndex;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WalletGroup id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WalletGroup name(String name) {
        this.name = name;
        return this;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public WalletGroup user(User user) {
        this.user = user;
        return this;
    }

    public Set<Wallet> getWallets() {
        return this.wallets;
    }

    public WalletGroup wallets(Set<Wallet> wallets) {
        this.setWallets(wallets);
        return this;
    }

    public WalletGroup addWallets(Wallet wallet) {
        this.wallets.add(wallet);
        wallet.setWalletGroup(this);
        return this;
    }

    public WalletGroup removeWallets(Wallet wallet) {
        this.wallets.remove(wallet);
        wallet.setWalletGroup(null);
        return this;
    }

    public void setWallets(Set<Wallet> wallets) {
        if (this.wallets != null) {
            this.wallets.forEach(i -> i.setWalletGroup(null));
        }
        if (wallets != null) {
            wallets.forEach(i -> i.setWalletGroup(this));
        }
        this.wallets = wallets;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WalletGroup)) {
            return false;
        }
        return id != null && id.equals(((WalletGroup) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WalletGroup{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            '}';
    }
}
