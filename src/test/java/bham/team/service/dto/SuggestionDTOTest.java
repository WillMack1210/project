package bham.team.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import bham.team.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SuggestionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SuggestionDTO.class);
        SuggestionDTO suggestionDTO1 = new SuggestionDTO();
        suggestionDTO1.setId(1L);
        SuggestionDTO suggestionDTO2 = new SuggestionDTO();
        assertThat(suggestionDTO1).isNotEqualTo(suggestionDTO2);
        suggestionDTO2.setId(suggestionDTO1.getId());
        assertThat(suggestionDTO1).isEqualTo(suggestionDTO2);
        suggestionDTO2.setId(2L);
        assertThat(suggestionDTO1).isNotEqualTo(suggestionDTO2);
        suggestionDTO1.setId(null);
        assertThat(suggestionDTO1).isNotEqualTo(suggestionDTO2);
    }
}
