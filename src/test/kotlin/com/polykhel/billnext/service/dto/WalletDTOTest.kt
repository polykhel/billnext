package com.polykhel.billnext.service.dto

import com.polykhel.billnext.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WalletDTOTest {

    @Test
    fun dtoEqualsVerifier() {
        equalsVerifier(WalletDTO::class)
        val walletDTO1 = WalletDTO()
        walletDTO1.id = 1L
        val walletDTO2 = WalletDTO()
        assertThat(walletDTO1).isNotEqualTo(walletDTO2)
        walletDTO2.id = walletDTO1.id
        assertThat(walletDTO1).isEqualTo(walletDTO2)
        walletDTO2.id = 2L
        assertThat(walletDTO1).isNotEqualTo(walletDTO2)
        walletDTO1.id = null
        assertThat(walletDTO1).isNotEqualTo(walletDTO2)
    }
}
