package com.polykhel.billnext.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.polykhel.billnext.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WalletGroupDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WalletGroupDTO.class);
        WalletGroupDTO walletGroupDTO1 = new WalletGroupDTO();
        walletGroupDTO1.setId(1L);
        WalletGroupDTO walletGroupDTO2 = new WalletGroupDTO();
        assertThat(walletGroupDTO1).isNotEqualTo(walletGroupDTO2);
        walletGroupDTO2.setId(walletGroupDTO1.getId());
        assertThat(walletGroupDTO1).isEqualTo(walletGroupDTO2);
        walletGroupDTO2.setId(2L);
        assertThat(walletGroupDTO1).isNotEqualTo(walletGroupDTO2);
        walletGroupDTO1.setId(null);
        assertThat(walletGroupDTO1).isNotEqualTo(walletGroupDTO2);
    }
}
