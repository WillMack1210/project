package bham.team.service.mapper;

import static bham.team.domain.EventAsserts.*;
import static bham.team.domain.EventTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class EventMapperTest {

    private EventMapper eventMapper;

    @BeforeEach
    void setUp() {
        eventMapper = Mappers.getMapper(EventMapper.class);
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEventSample1();
        var actual = eventMapper.toEntity(eventMapper.toDto(expected));
        assertEventAllPropertiesEquals(expected, actual);
    }
}
