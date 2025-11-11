package bham.team.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link bham.team.domain.FindTime} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FindTimeDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant requestStart;

    @NotNull
    private Instant requestEnd;

    @NotNull
    private Integer length;

    private UserProfileDTO requester;

    private Set<UserProfileDTO> participants = new HashSet<>();

    private Set<SuggestionDTO> suggestions = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getRequestStart() {
        return requestStart;
    }

    public void setRequestStart(Instant requestStart) {
        this.requestStart = requestStart;
    }

    public Instant getRequestEnd() {
        return requestEnd;
    }

    public void setRequestEnd(Instant requestEnd) {
        this.requestEnd = requestEnd;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public UserProfileDTO getRequester() {
        return requester;
    }

    public void setRequester(UserProfileDTO requester) {
        this.requester = requester;
    }

    public Set<UserProfileDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<UserProfileDTO> participants) {
        this.participants = participants;
    }

    public Set<SuggestionDTO> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(Set<SuggestionDTO> suggestions) {
        this.suggestions = suggestions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FindTimeDTO)) {
            return false;
        }

        FindTimeDTO findTimeDTO = (FindTimeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, findTimeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FindTimeDTO{" +
            "id=" + getId() +
            ", requestStart='" + getRequestStart() + "'" +
            ", requestEnd='" + getRequestEnd() + "'" +
            ", length=" + getLength() +
            ", requester=" + getRequester() +
            ", participants=" + getParticipants() +
            ", suggestions=" + getSuggestions() +
            "}";
    }
}
