package bham.team.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import bham.team.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ScheduleRequestDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScheduleRequestDTO.class);
        ScheduleRequestDTO scheduleRequestDTO1 = new ScheduleRequestDTO();
        scheduleRequestDTO1.setId(1L);
        ScheduleRequestDTO scheduleRequestDTO2 = new ScheduleRequestDTO();
        assertThat(scheduleRequestDTO1).isNotEqualTo(scheduleRequestDTO2);
        scheduleRequestDTO2.setId(scheduleRequestDTO1.getId());
        assertThat(scheduleRequestDTO1).isEqualTo(scheduleRequestDTO2);
        scheduleRequestDTO2.setId(2L);
        assertThat(scheduleRequestDTO1).isNotEqualTo(scheduleRequestDTO2);
        scheduleRequestDTO1.setId(null);
        assertThat(scheduleRequestDTO1).isNotEqualTo(scheduleRequestDTO2);
    }
}
