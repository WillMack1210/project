package bham.team.service.dto;

import bham.team.domain.enumeration.PrivacyStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link bham.team.domain.Event} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    @Lob
    private String description;

    @NotNull
    private Instant startTime;

    @NotNull
    private Instant endTime;

    private String location;

    @NotNull
    private PrivacyStatus privacy;

    private Set<UserProfileDTO> participants = new HashSet<>();

    private UserProfileDTO owner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public PrivacyStatus getPrivacy() {
        return privacy;
    }

    public void setPrivacy(PrivacyStatus privacy) {
        this.privacy = privacy;
    }

    public Set<UserProfileDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<UserProfileDTO> participants) {
        this.participants = participants;
    }

    public UserProfileDTO getOwner() {
        return owner;
    }

    public void setOwner(UserProfileDTO owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventDTO)) {
            return false;
        }

        EventDTO eventDTO = (EventDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, eventDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", location='" + getLocation() + "'" +
            ", privacy='" + getPrivacy() + "'" +
            ", participants=" + getParticipants() +
            ", owner=" + getOwner() +
            "}";
    }
}
