package bham.team.service.mapper;

import bham.team.domain.FindTime;
import bham.team.domain.Suggestion;
import bham.team.domain.UserProfile;
import bham.team.service.dto.FindTimeDTO;
import bham.team.service.dto.SuggestionDTO;
import bham.team.service.dto.UserProfileDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FindTime} and its DTO {@link FindTimeDTO}.
 */
@Mapper(componentModel = "spring")
public interface FindTimeMapper extends EntityMapper<FindTimeDTO, FindTime> {
    @Mapping(target = "requester", source = "requester", qualifiedByName = "userProfileId")
    @Mapping(target = "participants", source = "participants", qualifiedByName = "userProfileIdSet")
    @Mapping(target = "suggestions", source = "suggestions", qualifiedByName = "suggestionIdSet")
    FindTimeDTO toDto(FindTime s);

    @Mapping(target = "removeParticipant", ignore = true)
    @Mapping(target = "suggestions", ignore = true)
    @Mapping(target = "removeSuggestion", ignore = true)
    FindTime toEntity(FindTimeDTO findTimeDTO);

    @Named("userProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserProfileDTO toDtoUserProfileId(UserProfile userProfile);

    @Named("userProfileIdSet")
    default Set<UserProfileDTO> toDtoUserProfileIdSet(Set<UserProfile> userProfile) {
        return userProfile.stream().map(this::toDtoUserProfileId).collect(Collectors.toSet());
    }

    @Named("suggestionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SuggestionDTO toDtoSuggestionId(Suggestion suggestion);

    @Named("suggestionIdSet")
    default Set<SuggestionDTO> toDtoSuggestionIdSet(Set<Suggestion> suggestion) {
        return suggestion.stream().map(this::toDtoSuggestionId).collect(Collectors.toSet());
    }
}
