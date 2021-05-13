package com.polykhel.billnext.domain

import com.polykhel.billnext.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WalletGroupTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(WalletGroup::class)
        val walletGroup1 = WalletGroup()
        walletGroup1.id = 1L
        val walletGroup2 = WalletGroup()
        walletGroup2.id = walletGroup1.id
        assertThat(walletGroup1).isEqualTo(walletGroup2)
        walletGroup2.id = 2L
        assertThat(walletGroup1).isNotEqualTo(walletGroup2)
        walletGroup1.id = null
        assertThat(walletGroup1).isNotEqualTo(walletGroup2)
    }
}
