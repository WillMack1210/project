package bham.team.domain;

import static bham.team.domain.AvailiabilityBlockTestSamples.*;
import static bham.team.domain.EventTestSamples.*;
import static bham.team.domain.FindTimeTestSamples.*;
import static bham.team.domain.UserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import bham.team.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UserProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserProfile.class);
        UserProfile userProfile1 = getUserProfileSample1();
        UserProfile userProfile2 = new UserProfile();
        assertThat(userProfile1).isNotEqualTo(userProfile2);

        userProfile2.setId(userProfile1.getId());
        assertThat(userProfile1).isEqualTo(userProfile2);

        userProfile2 = getUserProfileSample2();
        assertThat(userProfile1).isNotEqualTo(userProfile2);
    }

    @Test
    void eventsTest() {
        UserProfile userProfile = getUserProfileRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        userProfile.addEvents(eventBack);
        assertThat(userProfile.getEvents()).containsOnly(eventBack);
        assertThat(eventBack.getOwner()).isEqualTo(userProfile);

        userProfile.removeEvents(eventBack);
        assertThat(userProfile.getEvents()).doesNotContain(eventBack);
        assertThat(eventBack.getOwner()).isNull();

        userProfile.events(new HashSet<>(Set.of(eventBack)));
        assertThat(userProfile.getEvents()).containsOnly(eventBack);
        assertThat(eventBack.getOwner()).isEqualTo(userProfile);

        userProfile.setEvents(new HashSet<>());
        assertThat(userProfile.getEvents()).doesNotContain(eventBack);
        assertThat(eventBack.getOwner()).isNull();
    }

    @Test
    void availiabilityBlockTest() {
        UserProfile userProfile = getUserProfileRandomSampleGenerator();
        AvailiabilityBlock availiabilityBlockBack = getAvailiabilityBlockRandomSampleGenerator();

        userProfile.addAvailiabilityBlock(availiabilityBlockBack);
        assertThat(userProfile.getAvailiabilityBlocks()).containsOnly(availiabilityBlockBack);
        assertThat(availiabilityBlockBack.getUser()).isEqualTo(userProfile);

        userProfile.removeAvailiabilityBlock(availiabilityBlockBack);
        assertThat(userProfile.getAvailiabilityBlocks()).doesNotContain(availiabilityBlockBack);
        assertThat(availiabilityBlockBack.getUser()).isNull();

        userProfile.availiabilityBlocks(new HashSet<>(Set.of(availiabilityBlockBack)));
        assertThat(userProfile.getAvailiabilityBlocks()).containsOnly(availiabilityBlockBack);
        assertThat(availiabilityBlockBack.getUser()).isEqualTo(userProfile);

        userProfile.setAvailiabilityBlocks(new HashSet<>());
        assertThat(userProfile.getAvailiabilityBlocks()).doesNotContain(availiabilityBlockBack);
        assertThat(availiabilityBlockBack.getUser()).isNull();
    }

    @Test
    void sharedEventTest() {
        UserProfile userProfile = getUserProfileRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        userProfile.addSharedEvent(eventBack);
        assertThat(userProfile.getSharedEvents()).containsOnly(eventBack);
        assertThat(eventBack.getParticipants()).containsOnly(userProfile);

        userProfile.removeSharedEvent(eventBack);
        assertThat(userProfile.getSharedEvents()).doesNotContain(eventBack);
        assertThat(eventBack.getParticipants()).doesNotContain(userProfile);

        userProfile.sharedEvents(new HashSet<>(Set.of(eventBack)));
        assertThat(userProfile.getSharedEvents()).containsOnly(eventBack);
        assertThat(eventBack.getParticipants()).containsOnly(userProfile);

        userProfile.setSharedEvents(new HashSet<>());
        assertThat(userProfile.getSharedEvents()).doesNotContain(eventBack);
        assertThat(eventBack.getParticipants()).doesNotContain(userProfile);
    }

    @Test
    void findTimeTest() {
        UserProfile userProfile = getUserProfileRandomSampleGenerator();
        FindTime findTimeBack = getFindTimeRandomSampleGenerator();

        userProfile.addFindTime(findTimeBack);
        assertThat(userProfile.getFindTimes()).containsOnly(findTimeBack);
        assertThat(findTimeBack.getParticipants()).containsOnly(userProfile);

        userProfile.removeFindTime(findTimeBack);
        assertThat(userProfile.getFindTimes()).doesNotContain(findTimeBack);
        assertThat(findTimeBack.getParticipants()).doesNotContain(userProfile);

        userProfile.findTimes(new HashSet<>(Set.of(findTimeBack)));
        assertThat(userProfile.getFindTimes()).containsOnly(findTimeBack);
        assertThat(findTimeBack.getParticipants()).containsOnly(userProfile);

        userProfile.setFindTimes(new HashSet<>());
        assertThat(userProfile.getFindTimes()).doesNotContain(findTimeBack);
        assertThat(findTimeBack.getParticipants()).doesNotContain(userProfile);
    }
}
