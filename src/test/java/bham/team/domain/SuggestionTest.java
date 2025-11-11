package bham.team.domain;

import static bham.team.domain.FindTimeTestSamples.*;
import static bham.team.domain.SuggestionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import bham.team.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SuggestionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Suggestion.class);
        Suggestion suggestion1 = getSuggestionSample1();
        Suggestion suggestion2 = new Suggestion();
        assertThat(suggestion1).isNotEqualTo(suggestion2);

        suggestion2.setId(suggestion1.getId());
        assertThat(suggestion1).isEqualTo(suggestion2);

        suggestion2 = getSuggestionSample2();
        assertThat(suggestion1).isNotEqualTo(suggestion2);
    }

    @Test
    void findTimeTest() {
        Suggestion suggestion = getSuggestionRandomSampleGenerator();
        FindTime findTimeBack = getFindTimeRandomSampleGenerator();

        suggestion.addFindTime(findTimeBack);
        assertThat(suggestion.getFindTimes()).containsOnly(findTimeBack);

        suggestion.removeFindTime(findTimeBack);
        assertThat(suggestion.getFindTimes()).doesNotContain(findTimeBack);

        suggestion.findTimes(new HashSet<>(Set.of(findTimeBack)));
        assertThat(suggestion.getFindTimes()).containsOnly(findTimeBack);

        suggestion.setFindTimes(new HashSet<>());
        assertThat(suggestion.getFindTimes()).doesNotContain(findTimeBack);
    }
}
