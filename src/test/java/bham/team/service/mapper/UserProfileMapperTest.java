package bham.team.service.mapper;

import static bham.team.domain.UserProfileAsserts.*;
import static bham.team.domain.UserProfileTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UserProfileMapperTest {

    private UserProfileMapper userProfileMapper;

    @BeforeEach
    void setUp() {
        userProfileMapper = Mappers.getMapper(UserProfileMapper.class);
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserProfileSample1();
        var actual = userProfileMapper.toEntity(userProfileMapper.toDto(expected));
        assertUserProfileAllPropertiesEquals(expected, actual);
    }
}
