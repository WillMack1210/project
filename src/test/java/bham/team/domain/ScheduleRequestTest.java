package bham.team.domain;

import static bham.team.domain.ScheduleRequestTestSamples.*;
import static bham.team.domain.UserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import bham.team.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ScheduleRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScheduleRequest.class);
        ScheduleRequest scheduleRequest1 = getScheduleRequestSample1();
        ScheduleRequest scheduleRequest2 = new ScheduleRequest();
        assertThat(scheduleRequest1).isNotEqualTo(scheduleRequest2);

        scheduleRequest2.setId(scheduleRequest1.getId());
        assertThat(scheduleRequest1).isEqualTo(scheduleRequest2);

        scheduleRequest2 = getScheduleRequestSample2();
        assertThat(scheduleRequest1).isNotEqualTo(scheduleRequest2);
    }

    @Test
    void userTest() {
        ScheduleRequest scheduleRequest = getScheduleRequestRandomSampleGenerator();
        UserProfile userProfileBack = getUserProfileRandomSampleGenerator();

        scheduleRequest.setUser(userProfileBack);
        assertThat(scheduleRequest.getUser()).isEqualTo(userProfileBack);

        scheduleRequest.user(null);
        assertThat(scheduleRequest.getUser()).isNull();
    }
}
