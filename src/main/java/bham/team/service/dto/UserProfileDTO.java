package bham.team.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link bham.team.domain.UserProfile} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserProfileDTO implements Serializable {

    private Long id;

    @NotNull
    private String username;

    @NotNull
    private String fullName;

    @Lob
    private byte[] profilePicture;

    private String profilePictureContentType;

    private String settings;

    private UserDTO user;

    private Set<EventDTO> sharedEvents = new HashSet<>();

    private Set<FindTimeDTO> findTimes = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getProfilePictureContentType() {
        return profilePictureContentType;
    }

    public void setProfilePictureContentType(String profilePictureContentType) {
        this.profilePictureContentType = profilePictureContentType;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Set<EventDTO> getSharedEvents() {
        return sharedEvents;
    }

    public void setSharedEvents(Set<EventDTO> sharedEvents) {
        this.sharedEvents = sharedEvents;
    }

    public Set<FindTimeDTO> getFindTimes() {
        return findTimes;
    }

    public void setFindTimes(Set<FindTimeDTO> findTimes) {
        this.findTimes = findTimes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserProfileDTO)) {
            return false;
        }

        UserProfileDTO userProfileDTO = (UserProfileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userProfileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserProfileDTO{" +
            "id=" + getId() +
            ", username='" + getUsername() + "'" +
            ", fullName='" + getFullName() + "'" +
            ", profilePicture='" + getProfilePicture() + "'" +
            ", settings='" + getSettings() + "'" +
            ", user=" + getUser() +
            ", sharedEvents=" + getSharedEvents() +
            ", findTimes=" + getFindTimes() +
            "}";
    }
}
