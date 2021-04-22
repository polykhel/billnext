package com.polykhel.billnext.service.dto;

import com.polykhel.billnext.domain.enumeration.ActivityType;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link com.polykhel.billnext.domain.Category} entity.
 */
@Getter
@Setter
public class CategoryDTO implements Serializable {

    @NotNull
    private Long id;

    @NotNull
    private String name;

    private ActivityType type;

    private UserDTO user;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CategoryDTO)) {
            return false;
        }

        CategoryDTO categoryDTO = (CategoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, categoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CategoryDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            ", user='" + getUser() + "'" +
            "}";
    }
}
