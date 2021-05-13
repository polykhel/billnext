package com.polykhel.billnext.service.dto

import com.polykhel.billnext.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WalletGroupDTOTest {

    @Test
    fun dtoEqualsVerifier() {
        equalsVerifier(WalletGroupDTO::class)
        val walletGroupDTO1 = WalletGroupDTO()
        walletGroupDTO1.id = 1L
        val walletGroupDTO2 = WalletGroupDTO()
        assertThat(walletGroupDTO1).isNotEqualTo(walletGroupDTO2)
        walletGroupDTO2.id = walletGroupDTO1.id
        assertThat(walletGroupDTO1).isEqualTo(walletGroupDTO2)
        walletGroupDTO2.id = 2L
        assertThat(walletGroupDTO1).isNotEqualTo(walletGroupDTO2)
        walletGroupDTO1.id = null
        assertThat(walletGroupDTO1).isNotEqualTo(walletGroupDTO2)
    }
}
