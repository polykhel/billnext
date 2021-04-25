package com.polykhel.billnext.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link com.polykhel.billnext.domain.WalletGroup} entity.
 */
@Getter
@Setter
public class WalletGroupDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private UserDTO user;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WalletGroupDTO)) {
            return false;
        }

        WalletGroupDTO walletGroupDTO = (WalletGroupDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, walletGroupDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WalletGroupDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", user='" + getUser() + "'" +
            "}";
    }
}
