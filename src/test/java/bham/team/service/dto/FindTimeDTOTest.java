package bham.team.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import bham.team.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FindTimeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FindTimeDTO.class);
        FindTimeDTO findTimeDTO1 = new FindTimeDTO();
        findTimeDTO1.setId(1L);
        FindTimeDTO findTimeDTO2 = new FindTimeDTO();
        assertThat(findTimeDTO1).isNotEqualTo(findTimeDTO2);
        findTimeDTO2.setId(findTimeDTO1.getId());
        assertThat(findTimeDTO1).isEqualTo(findTimeDTO2);
        findTimeDTO2.setId(2L);
        assertThat(findTimeDTO1).isNotEqualTo(findTimeDTO2);
        findTimeDTO1.setId(null);
        assertThat(findTimeDTO1).isNotEqualTo(findTimeDTO2);
    }
}
