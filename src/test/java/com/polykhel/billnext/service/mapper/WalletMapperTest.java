package com.polykhel.billnext.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;

class WalletMapperTest {

    private WalletMapper walletMapper;

    @BeforeEach
    public void setUp() {
        walletMapper = new WalletMapperImpl();
    }
}