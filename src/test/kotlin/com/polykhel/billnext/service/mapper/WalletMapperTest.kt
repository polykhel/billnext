package com.polykhel.billnext.service.mapper

import org.junit.jupiter.api.BeforeEach

class WalletMapperTest {

    private lateinit var walletMapper: WalletMapper

    @BeforeEach
    fun setUp() {
        walletMapper = WalletMapperImpl()
    }
}
