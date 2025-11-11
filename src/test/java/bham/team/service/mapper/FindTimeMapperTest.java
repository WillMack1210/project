package bham.team.service.mapper;

import static bham.team.domain.FindTimeAsserts.*;
import static bham.team.domain.FindTimeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FindTimeMapperTest {

    private FindTimeMapper findTimeMapper;

    @BeforeEach
    void setUp() {
        findTimeMapper = new FindTimeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFindTimeSample1();
        var actual = findTimeMapper.toEntity(findTimeMapper.toDto(expected));
        assertFindTimeAllPropertiesEquals(expected, actual);
    }
}
