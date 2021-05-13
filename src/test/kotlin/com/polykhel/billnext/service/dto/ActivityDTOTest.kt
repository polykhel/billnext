package com.polykhel.billnext.service.dto

import com.polykhel.billnext.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ActivityDTOTest {

    @Test
    fun dtoEqualsVerifier() {
        equalsVerifier(ActivityDTO::class)
        val activityDTO1 = ActivityDTO()
        activityDTO1.id = 1L
        val activityDTO2 = ActivityDTO()
        assertThat(activityDTO1).isNotEqualTo(activityDTO2)
        activityDTO2.id = activityDTO1.id
        assertThat(activityDTO1).isEqualTo(activityDTO2)
        activityDTO2.id = 2L
        assertThat(activityDTO1).isNotEqualTo(activityDTO2)
        activityDTO1.id = null
        assertThat(activityDTO1).isNotEqualTo(activityDTO2)
    }
}
