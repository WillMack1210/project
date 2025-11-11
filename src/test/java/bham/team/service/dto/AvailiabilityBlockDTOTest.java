package bham.team.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import bham.team.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AvailiabilityBlockDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AvailiabilityBlockDTO.class);
        AvailiabilityBlockDTO availiabilityBlockDTO1 = new AvailiabilityBlockDTO();
        availiabilityBlockDTO1.setId(1L);
        AvailiabilityBlockDTO availiabilityBlockDTO2 = new AvailiabilityBlockDTO();
        assertThat(availiabilityBlockDTO1).isNotEqualTo(availiabilityBlockDTO2);
        availiabilityBlockDTO2.setId(availiabilityBlockDTO1.getId());
        assertThat(availiabilityBlockDTO1).isEqualTo(availiabilityBlockDTO2);
        availiabilityBlockDTO2.setId(2L);
        assertThat(availiabilityBlockDTO1).isNotEqualTo(availiabilityBlockDTO2);
        availiabilityBlockDTO1.setId(null);
        assertThat(availiabilityBlockDTO1).isNotEqualTo(availiabilityBlockDTO2);
    }
}
