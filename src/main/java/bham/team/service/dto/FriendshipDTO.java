package bham.team.service.dto;

import bham.team.domain.enumeration.FriendStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link bham.team.domain.Friendship} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FriendshipDTO implements Serializable {

    private Long id;

    @NotNull
    private FriendStatus status;

    private UserProfileDTO requester;

    private UserProfileDTO addressee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FriendStatus getStatus() {
        return status;
    }

    public void setStatus(FriendStatus status) {
        this.status = status;
    }

    public UserProfileDTO getRequester() {
        return requester;
    }

    public void setRequester(UserProfileDTO requester) {
        this.requester = requester;
    }

    public UserProfileDTO getAddressee() {
        return addressee;
    }

    public void setAddressee(UserProfileDTO addressee) {
        this.addressee = addressee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FriendshipDTO)) {
            return false;
        }

        FriendshipDTO friendshipDTO = (FriendshipDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, friendshipDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FriendshipDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", requester=" + getRequester() +
            ", addressee=" + getAddressee() +
            "}";
    }
}
