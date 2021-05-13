package com.polykhel.billnext.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A WalletGroup.
 */
@Entity
@Table(name = "wallet_group")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class WalletGroup(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,
    @get: NotNull
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @ManyToOne(optional = false) @NotNull
    var user: User? = null,

    @OneToMany(mappedBy = "walletGroup")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)

    @JsonIgnoreProperties(
        value = [
            "walletGroup", "activities"
        ],
        allowSetters = true
    )
    var wallets: MutableSet<Wallet>? = mutableSetOf(),

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addWallets(wallet: Wallet): WalletGroup {
        if (this.wallets == null) {
            this.wallets = mutableSetOf()
        }
        this.wallets?.add(wallet)
        wallet.walletGroup = this
        return this
    }

    fun removeWallets(wallet: Wallet): WalletGroup {
        this.wallets?.remove(wallet)
        wallet.walletGroup = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WalletGroup) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "WalletGroup{" +
        "id=$id" +
        ", name='$name'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
