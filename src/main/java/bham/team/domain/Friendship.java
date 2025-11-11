package bham.team.domain;

import bham.team.domain.enumeration.FriendStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Friendship.
 */
@Entity
@Table(name = "friendship")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Friendship implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FriendStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "events", "availiabilityBlocks", "sharedEvents", "findTimes" }, allowSetters = true)
    private UserProfile user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "events", "availiabilityBlocks", "sharedEvents", "findTimes" }, allowSetters = true)
    private UserProfile friend;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Friendship id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FriendStatus getStatus() {
        return this.status;
    }

    public Friendship status(FriendStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(FriendStatus status) {
        this.status = status;
    }

    public UserProfile getUser() {
        return this.user;
    }

    public void setUser(UserProfile userProfile) {
        this.user = userProfile;
    }

    public Friendship user(UserProfile userProfile) {
        this.setUser(userProfile);
        return this;
    }

    public UserProfile getFriend() {
        return this.friend;
    }

    public void setFriend(UserProfile userProfile) {
        this.friend = userProfile;
    }

    public Friendship friend(UserProfile userProfile) {
        this.setFriend(userProfile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Friendship)) {
            return false;
        }
        return getId() != null && getId().equals(((Friendship) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Friendship{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
