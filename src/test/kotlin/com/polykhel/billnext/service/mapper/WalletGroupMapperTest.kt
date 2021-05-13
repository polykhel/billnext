package com.polykhel.billnext.service.mapper

import org.junit.jupiter.api.BeforeEach

class WalletGroupMapperTest {

    private lateinit var walletGroupMapper: WalletGroupMapper

    @BeforeEach
    fun setUp() {
        walletGroupMapper = WalletGroupMapperImpl()
    }
}
