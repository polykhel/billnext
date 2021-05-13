package com.polykhel.billnext.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.polykhel.billnext.domain.enumeration.ActivityType
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A Category.
 */
@Entity
@Table(name = "category")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,
    @get: NotNull
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    var type: ActivityType? = null,

    @ManyToOne(optional = false) @NotNull
    var user: User? = null,

    @OneToMany(mappedBy = "category")
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

    fun addActivity(activity: Activity): Category {
        if (this.activities == null) {
            this.activities = mutableSetOf()
        }
        this.activities?.add(activity)
        activity.category = this
        return this
    }

    fun removeActivity(activity: Activity): Category {
        this.activities?.remove(activity)
        activity.category = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Category) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Category{" +
        "id=$id" +
        ", name='$name'" +
        ", type='$type'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
