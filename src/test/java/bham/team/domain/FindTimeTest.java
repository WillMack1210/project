package bham.team.domain;

import static bham.team.domain.FindTimeTestSamples.*;
import static bham.team.domain.SuggestionTestSamples.*;
import static bham.team.domain.UserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import bham.team.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FindTimeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FindTime.class);
        FindTime findTime1 = getFindTimeSample1();
        FindTime findTime2 = new FindTime();
        assertThat(findTime1).isNotEqualTo(findTime2);

        findTime2.setId(findTime1.getId());
        assertThat(findTime1).isEqualTo(findTime2);

        findTime2 = getFindTimeSample2();
        assertThat(findTime1).isNotEqualTo(findTime2);
    }

    @Test
    void requesterTest() {
        FindTime findTime = getFindTimeRandomSampleGenerator();
        UserProfile userProfileBack = getUserProfileRandomSampleGenerator();

        findTime.setRequester(userProfileBack);
        assertThat(findTime.getRequester()).isEqualTo(userProfileBack);

        findTime.requester(null);
        assertThat(findTime.getRequester()).isNull();
    }

    @Test
    void participantTest() {
        FindTime findTime = getFindTimeRandomSampleGenerator();
        UserProfile userProfileBack = getUserProfileRandomSampleGenerator();

        findTime.addParticipant(userProfileBack);
        assertThat(findTime.getParticipants()).containsOnly(userProfileBack);

        findTime.removeParticipant(userProfileBack);
        assertThat(findTime.getParticipants()).doesNotContain(userProfileBack);

        findTime.participants(new HashSet<>(Set.of(userProfileBack)));
        assertThat(findTime.getParticipants()).containsOnly(userProfileBack);

        findTime.setParticipants(new HashSet<>());
        assertThat(findTime.getParticipants()).doesNotContain(userProfileBack);
    }

    @Test
    void suggestionTest() {
        FindTime findTime = getFindTimeRandomSampleGenerator();
        Suggestion suggestionBack = getSuggestionRandomSampleGenerator();

        findTime.addSuggestion(suggestionBack);
        assertThat(findTime.getSuggestions()).containsOnly(suggestionBack);
        assertThat(suggestionBack.getFindTimes()).containsOnly(findTime);

        findTime.removeSuggestion(suggestionBack);
        assertThat(findTime.getSuggestions()).doesNotContain(suggestionBack);
        assertThat(suggestionBack.getFindTimes()).doesNotContain(findTime);

        findTime.suggestions(new HashSet<>(Set.of(suggestionBack)));
        assertThat(findTime.getSuggestions()).containsOnly(suggestionBack);
        assertThat(suggestionBack.getFindTimes()).containsOnly(findTime);

        findTime.setSuggestions(new HashSet<>());
        assertThat(findTime.getSuggestions()).doesNotContain(suggestionBack);
        assertThat(suggestionBack.getFindTimes()).doesNotContain(findTime);
    }
}
