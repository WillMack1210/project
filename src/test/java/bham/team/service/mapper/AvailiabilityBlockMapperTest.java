package bham.team.service.mapper;

import static bham.team.domain.AvailiabilityBlockAsserts.*;
import static bham.team.domain.AvailiabilityBlockTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AvailiabilityBlockMapperTest {

    private AvailiabilityBlockMapper availiabilityBlockMapper;

    @BeforeEach
    void setUp() {
        availiabilityBlockMapper = new AvailiabilityBlockMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAvailiabilityBlockSample1();
        var actual = availiabilityBlockMapper.toEntity(availiabilityBlockMapper.toDto(expected));
        assertAvailiabilityBlockAllPropertiesEquals(expected, actual);
    }
}
