package com.polykhel.billnext.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import java.math.BigDecimal
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A Wallet.
 */
@Entity
@Table(name = "wallet")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Wallet(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,
    @get: NotNull
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @get: NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    var amount: BigDecimal? = null,

    @Column(name = "currency")
    var currency: String? = null,

    @Column(name = "remarks")
    var remarks: String? = null,

    @ManyToOne(optional = false) @NotNull

    @JsonIgnoreProperties(
        value = [
            "user", "wallets"
        ],
        allowSetters = true
    )
    var walletGroup: WalletGroup? = null,

    @OneToMany(mappedBy = "wallet")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)

    @JsonIgnoreProperties(
        value = [
            "user", "wallet", "category"
        ],
        allowSetters = true
    )
    var activities: MutableSet<Activity>? = mutableSetOf(),

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addActivity(activity: Activity): Wallet {
        if (this.activities == null) {
            this.activities = mutableSetOf()
        }
        this.activities?.add(activity)
        activity.wallet = this
        return this
    }

    fun removeActivity(activity: Activity): Wallet {
        this.activities?.remove(activity)
        activity.wallet = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Wallet) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Wallet{" +
        "id=$id" +
        ", name='$name'" +
        ", amount=$amount" +
        ", currency='$currency'" +
        ", remarks='$remarks'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
