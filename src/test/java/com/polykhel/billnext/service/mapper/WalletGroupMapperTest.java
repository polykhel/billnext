package com.polykhel.billnext.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WalletGroupMapperTest {

    private WalletGroupMapper walletGroupMapper;

    @BeforeEach
    public void setUp() {
        walletGroupMapper = new WalletGroupMapperImpl();
    }
}
