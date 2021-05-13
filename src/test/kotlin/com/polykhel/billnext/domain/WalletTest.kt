package com.polykhel.billnext.domain

import com.polykhel.billnext.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WalletTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Wallet::class)
        val wallet1 = Wallet()
        wallet1.id = 1L
        val wallet2 = Wallet()
        wallet2.id = wallet1.id
        assertThat(wallet1).isEqualTo(wallet2)
        wallet2.id = 2L
        assertThat(wallet1).isNotEqualTo(wallet2)
        wallet1.id = null
        assertThat(wallet1).isNotEqualTo(wallet2)
    }
}
