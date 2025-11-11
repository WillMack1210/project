package bham.team.service.mapper;

import bham.team.domain.Friendship;
import bham.team.domain.UserProfile;
import bham.team.service.dto.FriendshipDTO;
import bham.team.service.dto.UserProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Friendship} and its DTO {@link FriendshipDTO}.
 */
@Mapper(componentModel = "spring")
public interface FriendshipMapper extends EntityMapper<FriendshipDTO, Friendship> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userProfileId")
    @Mapping(target = "friend", source = "friend", qualifiedByName = "userProfileId")
    FriendshipDTO toDto(Friendship s);

    @Named("userProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserProfileDTO toDtoUserProfileId(UserProfile userProfile);
}
