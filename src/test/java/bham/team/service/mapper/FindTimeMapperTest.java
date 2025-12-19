package bham.team.service.mapper;

import static bham.team.domain.FindTimeAsserts.*;
import static bham.team.domain.FindTimeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class FindTimeMapperTest {

    private FindTimeMapper findTimeMapper;

    @BeforeEach
    void setUp() {
        findTimeMapper = Mappers.getMapper(FindTimeMapper.class);
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFindTimeSample1();
        var actual = findTimeMapper.toEntity(findTimeMapper.toDto(expected));
        assertFindTimeAllPropertiesEquals(expected, actual);
    }
}
