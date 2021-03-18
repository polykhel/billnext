package com.polykhel.billnext.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.polykhel.billnext.domain.Subcategory} entity.
 */
public class SubcategoryDTO extends AbstractAuditingDTO implements Serializable {

    @NotNull
    private Long id;

    @NotNull
    private String name;

    private CategoryDTO category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

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
