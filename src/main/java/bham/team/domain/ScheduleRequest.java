package bham.team.domain;

import bham.team.domain.enumeration.ScheduleIntensity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ScheduleRequest.
 */
@Entity
@Table(name = "schedule_request")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScheduleRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    @Lob
    @Column(name = "schedule_description", nullable = false)
    private String scheduleDescription;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "intensity", nullable = false)
    private ScheduleIntensity intensity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "events", "availiabilityBlocks", "sharedEvents", "findTimes" }, allowSetters = true)
    private UserProfile user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ScheduleRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartDate() {
        return this.startDate;
    }

    public ScheduleRequest startDate(Instant startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return this.endDate;
    }

    public ScheduleRequest endDate(Instant endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public String getScheduleDescription() {
        return this.scheduleDescription;
    }

    public ScheduleRequest scheduleDescription(String scheduleDescription) {
        this.setScheduleDescription(scheduleDescription);
        return this;
    }

    public void setScheduleDescription(String scheduleDescription) {
        this.scheduleDescription = scheduleDescription;
    }

    public ScheduleIntensity getIntensity() {
        return this.intensity;
    }

    public ScheduleRequest intensity(ScheduleIntensity intensity) {
        this.setIntensity(intensity);
        return this;
    }

    public void setIntensity(ScheduleIntensity intensity) {
        this.intensity = intensity;
    }

    public UserProfile getUser() {
        return this.user;
    }

    public void setUser(UserProfile userProfile) {
        this.user = userProfile;
    }

    public ScheduleRequest user(UserProfile userProfile) {
        this.setUser(userProfile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScheduleRequest)) {
            return false;
        }
        return getId() != null && getId().equals(((ScheduleRequest) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScheduleRequest{" +
            "id=" + getId() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", scheduleDescription='" + getScheduleDescription() + "'" +
            ", intensity='" + getIntensity() + "'" +
            "}";
    }
}
