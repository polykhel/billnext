package com.polykhel.billnext.service.dto;

import com.polykhel.billnext.domain.enumeration.ActivityType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link com.polykhel.billnext.domain.Activity} entity.
 */
@Getter
@Setter
public class ActivityDTO extends AbstractAuditingDTO implements Serializable {

    private Long id;

    @NotNull
    private ZonedDateTime date;

    @NotNull
    private BigDecimal amount;

    private String remarks;

    @NotNull
    private ActivityType type;

    private UserDTO user;

    private WalletDTO wallet;

    private CategoryDTO category;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActivityDTO)) {
            return false;
        }

        ActivityDTO activityDTO = (ActivityDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, activityDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ActivityDTO{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", amount=" + getAmount() +
            ", remarks='" + getRemarks() + "'" +
            ", type='" + getType() + "'" +
            ", user='" + getUser() + "'" +
            ", wallet=" + getWallet() +
            ", category=" + getCategory() +
            "}";
    }
}
