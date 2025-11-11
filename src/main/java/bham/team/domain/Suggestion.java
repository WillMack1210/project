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
 * A Suggestion.
 */
@Entity
@Table(name = "suggestion")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Suggestion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "suggested_start", nullable = false)
    private Instant suggestedStart;

    @NotNull
    @Column(name = "suggested_end", nullable = false)
    private Instant suggestedEnd;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_suggestion__find_time",
        joinColumns = @JoinColumn(name = "suggestion_id"),
        inverseJoinColumns = @JoinColumn(name = "find_time_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "requester", "participants", "suggestions" }, allowSetters = true)
    private Set<FindTime> findTimes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Suggestion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getSuggestedStart() {
        return this.suggestedStart;
    }

    public Suggestion suggestedStart(Instant suggestedStart) {
        this.setSuggestedStart(suggestedStart);
        return this;
    }

    public void setSuggestedStart(Instant suggestedStart) {
        this.suggestedStart = suggestedStart;
    }

    public Instant getSuggestedEnd() {
        return this.suggestedEnd;
    }

    public Suggestion suggestedEnd(Instant suggestedEnd) {
        this.setSuggestedEnd(suggestedEnd);
        return this;
    }

    public void setSuggestedEnd(Instant suggestedEnd) {
        this.suggestedEnd = suggestedEnd;
    }

    public Set<FindTime> getFindTimes() {
        return this.findTimes;
    }

    public void setFindTimes(Set<FindTime> findTimes) {
        this.findTimes = findTimes;
    }

    public Suggestion findTimes(Set<FindTime> findTimes) {
        this.setFindTimes(findTimes);
        return this;
    }

    public Suggestion addFindTime(FindTime findTime) {
        this.findTimes.add(findTime);
        return this;
    }

    public Suggestion removeFindTime(FindTime findTime) {
        this.findTimes.remove(findTime);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Suggestion)) {
            return false;
        }
        return getId() != null && getId().equals(((Suggestion) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Suggestion{" +
            "id=" + getId() +
            ", suggestedStart='" + getSuggestedStart() + "'" +
            ", suggestedEnd='" + getSuggestedEnd() + "'" +
            "}";
    }
}
