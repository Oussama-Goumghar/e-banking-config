package com.ensa.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ensa.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HistorySystemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(HistorySystem.class);
        HistorySystem historySystem1 = new HistorySystem();
        historySystem1.setId(1L);
        HistorySystem historySystem2 = new HistorySystem();
        historySystem2.setId(historySystem1.getId());
        assertThat(historySystem1).isEqualTo(historySystem2);
        historySystem2.setId(2L);
        assertThat(historySystem1).isNotEqualTo(historySystem2);
        historySystem1.setId(null);
        assertThat(historySystem1).isNotEqualTo(historySystem2);
    }
}
