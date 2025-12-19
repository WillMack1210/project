package bham.team.service.mapper;

import static bham.team.domain.SuggestionAsserts.*;
import static bham.team.domain.SuggestionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class SuggestionMapperTest {

    private SuggestionMapper suggestionMapper;

    @BeforeEach
    void setUp() {
        suggestionMapper = Mappers.getMapper(SuggestionMapper.class);
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSuggestionSample1();
        var actual = suggestionMapper.toEntity(suggestionMapper.toDto(expected));
        assertSuggestionAllPropertiesEquals(expected, actual);
    }
}
