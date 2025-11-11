package bham.team.service.mapper;

import bham.team.domain.Event;
import bham.team.domain.FindTime;
import bham.team.domain.User;
import bham.team.domain.UserProfile;
import bham.team.service.dto.EventDTO;
import bham.team.service.dto.FindTimeDTO;
import bham.team.service.dto.UserDTO;
import bham.team.service.dto.UserProfileDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserProfile} and its DTO {@link UserProfileDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserProfileMapper extends EntityMapper<UserProfileDTO, UserProfile> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "sharedEvents", source = "sharedEvents", qualifiedByName = "eventIdSet")
    @Mapping(target = "findTimes", source = "findTimes", qualifiedByName = "findTimeIdSet")
    UserProfileDTO toDto(UserProfile s);

    @Mapping(target = "sharedEvents", ignore = true)
    @Mapping(target = "removeSharedEvent", ignore = true)
    @Mapping(target = "findTimes", ignore = true)
    @Mapping(target = "removeFindTime", ignore = true)
    UserProfile toEntity(UserProfileDTO userProfileDTO);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("eventId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EventDTO toDtoEventId(Event event);

    @Named("eventIdSet")
    default Set<EventDTO> toDtoEventIdSet(Set<Event> event) {
        return event.stream().map(this::toDtoEventId).collect(Collectors.toSet());
    }

    @Named("findTimeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FindTimeDTO toDtoFindTimeId(FindTime findTime);

    @Named("findTimeIdSet")
    default Set<FindTimeDTO> toDtoFindTimeIdSet(Set<FindTime> findTime) {
        return findTime.stream().map(this::toDtoFindTimeId).collect(Collectors.toSet());
    }
}
