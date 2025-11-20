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
    @Mapping(target = "requester", source = "requester", qualifiedByName = "userProfileUsername")
    @Mapping(target = "addressee", source = "addressee", qualifiedByName = "userProfileUsername")
    FriendshipDTO toDto(Friendship s);

    @Named("userProfileUsername")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    UserProfileDTO toDtoUserProfileUsername(UserProfile userProfile);
}
