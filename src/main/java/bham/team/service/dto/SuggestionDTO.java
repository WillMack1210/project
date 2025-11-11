package bham.team.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link bham.team.domain.Suggestion} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SuggestionDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant suggestedStart;

    @NotNull
    private Instant suggestedEnd;

    private Set<FindTimeDTO> findTimes = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getSuggestedStart() {
        return suggestedStart;
    }

    public void setSuggestedStart(Instant suggestedStart) {
        this.suggestedStart = suggestedStart;
    }

    public Instant getSuggestedEnd() {
        return suggestedEnd;
    }

    public void setSuggestedEnd(Instant suggestedEnd) {
        this.suggestedEnd = suggestedEnd;
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
        if (!(o instanceof SuggestionDTO)) {
            return false;
        }

        SuggestionDTO suggestionDTO = (SuggestionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, suggestionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SuggestionDTO{" +
            "id=" + getId() +
            ", suggestedStart='" + getSuggestedStart() + "'" +
            ", suggestedEnd='" + getSuggestedEnd() + "'" +
            ", findTimes=" + getFindTimes() +
            "}";
    }
}
