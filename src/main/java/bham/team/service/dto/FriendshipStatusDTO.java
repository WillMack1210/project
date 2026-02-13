package bham.team.service.dto;

import bham.team.domain.enumeration.FriendStatus;

public class FriendshipStatusDTO {

    private Long userProfileId;
    private Long friendshipId;
    private FriendStatus status;
    private Boolean isRequester;

    public FriendshipStatusDTO() {}

    public FriendshipStatusDTO(Long userProfileId, Long friendshipId, FriendStatus status, Boolean isRequester) {
        this.userProfileId = userProfileId;
        this.friendshipId = friendshipId;
        this.status = status;
        this.isRequester = isRequester;
    }

    public Long getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(Long userProfileId) {
        this.userProfileId = userProfileId;
    }

    public Long getFriendshipId() {
        return friendshipId;
    }

    public void setFriendshipId(Long friendshipId) {
        this.friendshipId = friendshipId;
    }

    public FriendStatus getStatus() {
        return status;
    }

    public void setStatus(FriendStatus status) {
        this.status = status;
    }

    public Boolean getIsRequester() {
        return isRequester;
    }

    public void setIsRequester(Boolean isRequester) {
        this.isRequester = isRequester;
    }
}
