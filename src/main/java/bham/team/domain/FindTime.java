package bham.team.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A FindTime.
 */
@Entity
@Table(name = "find_time")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FindTime implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "request_start", nullable = false)
    private Instant requestStart;

    @NotNull
    @Column(name = "request_end", nullable = false)
    private Instant requestEnd;

    @NotNull
    @Column(name = "length", nullable = false)
    private Integer length;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "events", "availiabilityBlocks", "sharedEvents", "findTimes" }, allowSetters = true)
    private UserProfile requester;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_find_time__participant",
        joinColumns = @JoinColumn(name = "find_time_id"),
        inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "events", "availiabilityBlocks", "sharedEvents", "findTimes" }, allowSetters = true)
    private Set<UserProfile> participants = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "findTimes")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "findTimes" }, allowSetters = true)
    private Set<Suggestion> suggestions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FindTime id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getRequestStart() {
        return this.requestStart;
    }

    public FindTime requestStart(Instant requestStart) {
        this.setRequestStart(requestStart);
        return this;
    }

    public void setRequestStart(Instant requestStart) {
        this.requestStart = requestStart;
    }

    public Instant getRequestEnd() {
        return this.requestEnd;
    }

    public FindTime requestEnd(Instant requestEnd) {
        this.setRequestEnd(requestEnd);
        return this;
    }

    public void setRequestEnd(Instant requestEnd) {
        this.requestEnd = requestEnd;
    }

    public Integer getLength() {
        return this.length;
    }

    public FindTime length(Integer length) {
        this.setLength(length);
        return this;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public UserProfile getRequester() {
        return this.requester;
    }

    public void setRequester(UserProfile userProfile) {
        this.requester = userProfile;
    }

    public FindTime requester(UserProfile userProfile) {
        this.setRequester(userProfile);
        return this;
    }

    public Set<UserProfile> getParticipants() {
        return this.participants;
    }

    public void setParticipants(Set<UserProfile> userProfiles) {
        this.participants = userProfiles;
    }

    public FindTime participants(Set<UserProfile> userProfiles) {
        this.setParticipants(userProfiles);
        return this;
    }

    public FindTime addParticipant(UserProfile userProfile) {
        this.participants.add(userProfile);
        return this;
    }

    public FindTime removeParticipant(UserProfile userProfile) {
        this.participants.remove(userProfile);
        return this;
    }

    public Set<Suggestion> getSuggestions() {
        return this.suggestions;
    }

    public void setSuggestions(Set<Suggestion> suggestions) {
        if (this.suggestions != null) {
            this.suggestions.forEach(i -> i.removeFindTime(this));
        }
        if (suggestions != null) {
            suggestions.forEach(i -> i.addFindTime(this));
        }
        this.suggestions = suggestions;
    }

    public FindTime suggestions(Set<Suggestion> suggestions) {
        this.setSuggestions(suggestions);
        return this;
    }

    public FindTime addSuggestion(Suggestion suggestion) {
        this.suggestions.add(suggestion);
        suggestion.getFindTimes().add(this);
        return this;
    }

    public FindTime removeSuggestion(Suggestion suggestion) {
        this.suggestions.remove(suggestion);
        suggestion.getFindTimes().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FindTime)) {
            return false;
        }
        return getId() != null && getId().equals(((FindTime) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FindTime{" +
            "id=" + getId() +
            ", requestStart='" + getRequestStart() + "'" +
            ", requestEnd='" + getRequestEnd() + "'" +
            ", length=" + getLength() +
            "}";
    }
}
