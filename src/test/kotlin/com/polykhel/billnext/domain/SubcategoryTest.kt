package com.polykhel.billnext.domain

import com.polykhel.billnext.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SubcategoryTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Subcategory::class)
        val subcategory1 = Subcategory()
        subcategory1.id = 1L
        val subcategory2 = Subcategory()
        subcategory2.id = subcategory1.id
        assertThat(subcategory1).isEqualTo(subcategory2)
        subcategory2.id = 2L
        assertThat(subcategory1).isNotEqualTo(subcategory2)
        subcategory1.id = null
        assertThat(subcategory1).isNotEqualTo(subcategory2)
    }
}
