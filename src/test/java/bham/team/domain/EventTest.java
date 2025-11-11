package bham.team.domain;

import static bham.team.domain.EventTestSamples.*;
import static bham.team.domain.UserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import bham.team.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Event.class);
        Event event1 = getEventSample1();
        Event event2 = new Event();
        assertThat(event1).isNotEqualTo(event2);

        event2.setId(event1.getId());
        assertThat(event1).isEqualTo(event2);

        event2 = getEventSample2();
        assertThat(event1).isNotEqualTo(event2);
    }

    @Test
    void participantTest() {
        Event event = getEventRandomSampleGenerator();
        UserProfile userProfileBack = getUserProfileRandomSampleGenerator();

        event.addParticipant(userProfileBack);
        assertThat(event.getParticipants()).containsOnly(userProfileBack);

        event.removeParticipant(userProfileBack);
        assertThat(event.getParticipants()).doesNotContain(userProfileBack);

        event.participants(new HashSet<>(Set.of(userProfileBack)));
        assertThat(event.getParticipants()).containsOnly(userProfileBack);

        event.setParticipants(new HashSet<>());
        assertThat(event.getParticipants()).doesNotContain(userProfileBack);
    }

    @Test
    void ownerTest() {
        Event event = getEventRandomSampleGenerator();
        UserProfile userProfileBack = getUserProfileRandomSampleGenerator();

        event.setOwner(userProfileBack);
        assertThat(event.getOwner()).isEqualTo(userProfileBack);

        event.owner(null);
        assertThat(event.getOwner()).isNull();
    }
}
