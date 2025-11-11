package bham.team.service.dto;

import bham.team.domain.enumeration.ScheduleIntensity;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link bham.team.domain.ScheduleRequest} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScheduleRequestDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant startDate;

    @NotNull
    private Instant endDate;

    @Lob
    private String scheduleDescription;

    @NotNull
    private ScheduleIntensity intensity;

    private UserProfileDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public String getScheduleDescription() {
        return scheduleDescription;
    }

    public void setScheduleDescription(String scheduleDescription) {
        this.scheduleDescription = scheduleDescription;
    }

    public ScheduleIntensity getIntensity() {
        return intensity;
    }

    public void setIntensity(ScheduleIntensity intensity) {
        this.intensity = intensity;
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
        if (!(o instanceof ScheduleRequestDTO)) {
            return false;
        }

        ScheduleRequestDTO scheduleRequestDTO = (ScheduleRequestDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, scheduleRequestDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScheduleRequestDTO{" +
            "id=" + getId() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", scheduleDescription='" + getScheduleDescription() + "'" +
            ", intensity='" + getIntensity() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
