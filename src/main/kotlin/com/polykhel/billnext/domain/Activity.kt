package com.polykhel.billnext.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.polykhel.billnext.domain.enumeration.ActivityType
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import java.math.BigDecimal
import java.time.ZonedDateTime
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A Activity.
 */
@Entity
@Table(name = "activity")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Activity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,
    @get: NotNull
    @Column(name = "date", nullable = false)
    var date: ZonedDateTime? = null,

    @get: NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    var amount: BigDecimal? = null,

    @Column(name = "remarks")
    var remarks: String? = null,

    @get: NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: ActivityType? = null,

    @ManyToOne(optional = false) @NotNull
    var user: User? = null,

    @ManyToOne(optional = false) @NotNull

    @JsonIgnoreProperties(
        value = [
            "walletGroup", "activities"
        ],
        allowSetters = true
    )
    var wallet: Wallet? = null,

    @ManyToOne(optional = false) @NotNull

    @JsonIgnoreProperties(
        value = [
            "user", "activities"
        ],
        allowSetters = true
    )
    var category: Category? = null,

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Activity) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Activity{" +
        "id=$id" +
        ", date='$date'" +
        ", amount=$amount" +
        ", remarks='$remarks'" +
        ", type='$type'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
