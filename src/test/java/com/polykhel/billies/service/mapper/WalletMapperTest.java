package com.polykhel.billies.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WalletMapperTest {

    private WalletMapper walletMapper;

    @BeforeEach
    public void setUp() {
        walletMapper = new WalletMapperImpl();
    }
}
