package com.polykhel.billnext.service.dto

import com.polykhel.billnext.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SubcategoryDTOTest {

    @Test
    fun dtoEqualsVerifier() {
        equalsVerifier(SubcategoryDTO::class)
        val subcategoryDTO1 = SubcategoryDTO()
        subcategoryDTO1.id = 1L
        val subcategoryDTO2 = SubcategoryDTO()
        assertThat(subcategoryDTO1).isNotEqualTo(subcategoryDTO2)
        subcategoryDTO2.id = subcategoryDTO1.id
        assertThat(subcategoryDTO1).isEqualTo(subcategoryDTO2)
        subcategoryDTO2.id = 2L
        assertThat(subcategoryDTO1).isNotEqualTo(subcategoryDTO2)
        subcategoryDTO1.id = null
        assertThat(subcategoryDTO1).isNotEqualTo(subcategoryDTO2)
    }
}
