package bham.team.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link bham.team.domain.AvailiabilityBlock} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AvailiabilityBlockDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant startDateTime;

    @NotNull
    private Instant endDateTime;

    private UserProfileDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Instant startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Instant getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Instant endDateTime) {
        this.endDateTime = endDateTime;
    }

    public UserProfileDTO getUser() {
        return user;
    }

    public void setUser(UserProfileDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AvailiabilityBlockDTO)) {
            return false;
        }

        AvailiabilityBlockDTO availiabilityBlockDTO = (AvailiabilityBlockDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, availiabilityBlockDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AvailiabilityBlockDTO{" +
            "id=" + getId() +
            ", startDateTime='" + getStartDateTime() + "'" +
            ", endDateTime='" + getEndDateTime() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
