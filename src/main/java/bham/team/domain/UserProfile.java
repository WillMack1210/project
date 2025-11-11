package bham.team.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A UserProfile.
 */
@Entity
@Table(name = "user_profile")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "username", nullable = false)
    private String username;

    @NotNull
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Lob
    @Column(name = "profile_picture")
    private byte[] profilePicture;

    @Column(name = "profile_picture_content_type")
    private String profilePictureContentType;

    @Column(name = "settings")
    private String settings;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "participants", "owner" }, allowSetters = true)
    private Set<Event> events = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Set<AvailiabilityBlock> availiabilityBlocks = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "participants")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "participants", "owner" }, allowSetters = true)
    private Set<Event> sharedEvents = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "participants")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "requester", "participants", "suggestions" }, allowSetters = true)
    private Set<FindTime> findTimes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserProfile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public UserProfile username(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return this.fullName;
    }

    public UserProfile fullName(String fullName) {
        this.setFullName(fullName);
        return this;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public byte[] getProfilePicture() {
        return this.profilePicture;
    }

    public UserProfile profilePicture(byte[] profilePicture) {
        this.setProfilePicture(profilePicture);
        return this;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getProfilePictureContentType() {
        return this.profilePictureContentType;
    }

    public UserProfile profilePictureContentType(String profilePictureContentType) {
        this.profilePictureContentType = profilePictureContentType;
        return this;
    }

    public void setProfilePictureContentType(String profilePictureContentType) {
        this.profilePictureContentType = profilePictureContentType;
    }

    public String getSettings() {
        return this.settings;
    }

    public UserProfile settings(String settings) {
        this.setSettings(settings);
        return this;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserProfile user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Event> getEvents() {
        return this.events;
    }

    public void setEvents(Set<Event> events) {
        if (this.events != null) {
            this.events.forEach(i -> i.setOwner(null));
        }
        if (events != null) {
            events.forEach(i -> i.setOwner(this));
        }
        this.events = events;
    }

    public UserProfile events(Set<Event> events) {
        this.setEvents(events);
        return this;
    }

    public UserProfile addEvents(Event event) {
        this.events.add(event);
        event.setOwner(this);
        return this;
    }

    public UserProfile removeEvents(Event event) {
        this.events.remove(event);
        event.setOwner(null);
        return this;
    }

    public Set<AvailiabilityBlock> getAvailiabilityBlocks() {
        return this.availiabilityBlocks;
    }

    public void setAvailiabilityBlocks(Set<AvailiabilityBlock> availiabilityBlocks) {
        if (this.availiabilityBlocks != null) {
            this.availiabilityBlocks.forEach(i -> i.setUser(null));
        }
        if (availiabilityBlocks != null) {
            availiabilityBlocks.forEach(i -> i.setUser(this));
        }
        this.availiabilityBlocks = availiabilityBlocks;
    }

    public UserProfile availiabilityBlocks(Set<AvailiabilityBlock> availiabilityBlocks) {
        this.setAvailiabilityBlocks(availiabilityBlocks);
        return this;
    }

    public UserProfile addAvailiabilityBlock(AvailiabilityBlock availiabilityBlock) {
        this.availiabilityBlocks.add(availiabilityBlock);
        availiabilityBlock.setUser(this);
        return this;
    }

    public UserProfile removeAvailiabilityBlock(AvailiabilityBlock availiabilityBlock) {
        this.availiabilityBlocks.remove(availiabilityBlock);
        availiabilityBlock.setUser(null);
        return this;
    }

    public Set<Event> getSharedEvents() {
        return this.sharedEvents;
    }

    public void setSharedEvents(Set<Event> events) {
        if (this.sharedEvents != null) {
            this.sharedEvents.forEach(i -> i.removeParticipant(this));
        }
        if (events != null) {
            events.forEach(i -> i.addParticipant(this));
        }
        this.sharedEvents = events;
    }

    public UserProfile sharedEvents(Set<Event> events) {
        this.setSharedEvents(events);
        return this;
    }

    public UserProfile addSharedEvent(Event event) {
        this.sharedEvents.add(event);
        event.getParticipants().add(this);
        return this;
    }

    public UserProfile removeSharedEvent(Event event) {
        this.sharedEvents.remove(event);
        event.getParticipants().remove(this);
        return this;
    }

    public Set<FindTime> getFindTimes() {
        return this.findTimes;
    }

    public void setFindTimes(Set<FindTime> findTimes) {
        if (this.findTimes != null) {
            this.findTimes.forEach(i -> i.removeParticipant(this));
        }
        if (findTimes != null) {
            findTimes.forEach(i -> i.addParticipant(this));
        }
        this.findTimes = findTimes;
    }

    public UserProfile findTimes(Set<FindTime> findTimes) {
        this.setFindTimes(findTimes);
        return this;
    }

    public UserProfile addFindTime(FindTime findTime) {
        this.findTimes.add(findTime);
        findTime.getParticipants().add(this);
        return this;
    }

    public UserProfile removeFindTime(FindTime findTime) {
        this.findTimes.remove(findTime);
        findTime.getParticipants().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserProfile)) {
            return false;
        }
        return getId() != null && getId().equals(((UserProfile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserProfile{" +
            "id=" + getId() +
            ", username='" + getUsername() + "'" +
            ", fullName='" + getFullName() + "'" +
            ", profilePicture='" + getProfilePicture() + "'" +
            ", profilePictureContentType='" + getProfilePictureContentType() + "'" +
            ", settings='" + getSettings() + "'" +
            "}";
    }
}
