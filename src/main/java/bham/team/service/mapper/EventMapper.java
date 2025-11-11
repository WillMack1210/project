package bham.team.service.mapper;

import bham.team.domain.Event;
import bham.team.domain.UserProfile;
import bham.team.service.dto.EventDTO;
import bham.team.service.dto.UserProfileDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Event} and its DTO {@link EventDTO}.
 */
@Mapper(componentModel = "spring")
public interface EventMapper extends EntityMapper<EventDTO, Event> {
    @Mapping(target = "participants", source = "participants", qualifiedByName = "userProfileIdSet")
    @Mapping(target = "owner", source = "owner", qualifiedByName = "userProfileId")
    EventDTO toDto(Event s);

    @Mapping(target = "removeParticipant", ignore = true)
    Event toEntity(EventDTO eventDTO);

    @Named("userProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserProfileDTO toDtoUserProfileId(UserProfile userProfile);

    @Named("userProfileIdSet")
    default Set<UserProfileDTO> toDtoUserProfileIdSet(Set<UserProfile> userProfile) {
        return userProfile.stream().map(this::toDtoUserProfileId).collect(Collectors.toSet());
    }
}
