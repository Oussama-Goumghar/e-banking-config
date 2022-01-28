package com.ensa.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ensa.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClientBlackListTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClientBlackList.class);
        ClientBlackList clientBlackList1 = new ClientBlackList();
        clientBlackList1.setId(1L);
        ClientBlackList clientBlackList2 = new ClientBlackList();
        clientBlackList2.setId(clientBlackList1.getId());
        assertThat(clientBlackList1).isEqualTo(clientBlackList2);
        clientBlackList2.setId(2L);
        assertThat(clientBlackList1).isNotEqualTo(clientBlackList2);
        clientBlackList1.setId(null);
        assertThat(clientBlackList1).isNotEqualTo(clientBlackList2);
    }
}
