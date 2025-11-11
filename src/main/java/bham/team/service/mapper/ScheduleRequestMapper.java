package bham.team.service.mapper;

import bham.team.domain.ScheduleRequest;
import bham.team.domain.UserProfile;
import bham.team.service.dto.ScheduleRequestDTO;
import bham.team.service.dto.UserProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ScheduleRequest} and its DTO {@link ScheduleRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface ScheduleRequestMapper extends EntityMapper<ScheduleRequestDTO, ScheduleRequest> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userProfileId")
    ScheduleRequestDTO toDto(ScheduleRequest s);

    @Named("userProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserProfileDTO toDtoUserProfileId(UserProfile userProfile);
}
