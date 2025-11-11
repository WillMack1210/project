package bham.team.service.mapper;

import static bham.team.domain.SuggestionAsserts.*;
import static bham.team.domain.SuggestionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SuggestionMapperTest {

    private SuggestionMapper suggestionMapper;

    @BeforeEach
    void setUp() {
        suggestionMapper = new SuggestionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSuggestionSample1();
        var actual = suggestionMapper.toEntity(suggestionMapper.toDto(expected));
        assertSuggestionAllPropertiesEquals(expected, actual);
    }
}
