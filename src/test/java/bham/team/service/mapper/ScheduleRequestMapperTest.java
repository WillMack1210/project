package bham.team.service.mapper;

import static bham.team.domain.ScheduleRequestAsserts.*;
import static bham.team.domain.ScheduleRequestTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScheduleRequestMapperTest {

    private ScheduleRequestMapper scheduleRequestMapper;

    @BeforeEach
    void setUp() {
        scheduleRequestMapper = new ScheduleRequestMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getScheduleRequestSample1();
        var actual = scheduleRequestMapper.toEntity(scheduleRequestMapper.toDto(expected));
        assertScheduleRequestAllPropertiesEquals(expected, actual);
    }
}
