package com.polykhel.billnext.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link com.polykhel.billnext.domain.Subcategory} entity.
 */
@Getter
@Setter
public class SubcategoryDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private CategoryDTO category;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubcategoryDTO)) {
            return false;
        }

        SubcategoryDTO subcategoryDTO = (SubcategoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, subcategoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SubcategoryDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", category=" + getCategory() +
            "}";
    }
}
