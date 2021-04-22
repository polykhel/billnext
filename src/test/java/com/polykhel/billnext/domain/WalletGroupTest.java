package com.polykhel.billnext.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.polykhel.billnext.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WalletGroupTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WalletGroup.class);
        WalletGroup walletGroup1 = new WalletGroup();
        walletGroup1.setId(1L);
        WalletGroup walletGroup2 = new WalletGroup();
        walletGroup2.setId(walletGroup1.getId());
        assertThat(walletGroup1).isEqualTo(walletGroup2);
        walletGroup2.setId(2L);
        assertThat(walletGroup1).isNotEqualTo(walletGroup2);
        walletGroup1.setId(null);
        assertThat(walletGroup1).isNotEqualTo(walletGroup2);
    }
}
