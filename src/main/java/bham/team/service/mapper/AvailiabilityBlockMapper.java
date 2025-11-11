package bham.team.service.mapper;

import bham.team.domain.AvailiabilityBlock;
import bham.team.domain.UserProfile;
import bham.team.service.dto.AvailiabilityBlockDTO;
import bham.team.service.dto.UserProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AvailiabilityBlock} and its DTO {@link AvailiabilityBlockDTO}.
 */
@Mapper(componentModel = "spring")
public interface AvailiabilityBlockMapper extends EntityMapper<AvailiabilityBlockDTO, AvailiabilityBlock> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userProfileId")
    AvailiabilityBlockDTO toDto(AvailiabilityBlock s);

    @Named("userProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserProfileDTO toDtoUserProfileId(UserProfile userProfile);
}
