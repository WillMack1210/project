package bham.team.service;

import bham.team.domain.Friendship;
import bham.team.domain.UserProfile;
import bham.team.domain.enumeration.FriendStatus;
import bham.team.repository.FriendshipRepository;
import bham.team.repository.UserProfileRepository;
import bham.team.repository.UserRepository;
import bham.team.security.SecurityUtils;
import bham.team.service.dto.UserProfileDTO;
import bham.team.service.mapper.FriendshipMapper;
import bham.team.service.mapper.UserProfileMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    public FriendshipService(
        FriendshipRepository friendshipRepository,
        UserProfileRepository userProfileRepository,
        UserRepository userRepository,
        FriendshipMapper friendshipMapper,
        UserProfileMapper userProfileMapper
    ) {
        this.friendshipRepository = friendshipRepository;
        this.userProfileRepository = userProfileRepository;
        this.userProfileMapper = userProfileMapper;
    }

    public void sendFriendRequest(Long recipientId) {
        UserProfile currentUser = getCurrentUserProfile();
        UserProfile recipient = userProfileRepository.findById(recipientId).orElseThrow(() -> new RuntimeException("Recipient not found"));
        if (currentUser.getId().equals(recipientId)) {
            throw new RuntimeException("Cannot send friend request to yourself");
        }
        Optional<Friendship> existing = friendshipRepository.findBetweenUsers(currentUser.getId(), recipient.getId());
        if (existing.isPresent()) {
            throw new RuntimeException("Friendship already exists");
        }
        Friendship friendship = new Friendship();
        friendship.setRequester(currentUser);
        friendship.setAddressee(recipient);
        friendship.setStatus(FriendStatus.PENDING);
        friendshipRepository.save(friendship);
    }

    public void acceptRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository
            .findOneWithToOneRelationships(friendshipId)
            .orElseThrow(() -> new RuntimeException("Friendship not found"));
        UserProfile currentUser = getCurrentUserProfile();
        if (!friendship.getAddressee().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to accpet this request");
        }
        friendship.setStatus(FriendStatus.ACCEPTED);
        friendshipRepository.save(friendship);
    }

    public void declineRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository
            .findOneWithToOneRelationships(friendshipId)
            .orElseThrow(() -> new RuntimeException("Friendship not found"));
        UserProfile currentUser = getCurrentUserProfile();
        if (!friendship.getAddressee().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to decline this request");
        }
        friendship.setStatus(FriendStatus.DECLINED);
        friendshipRepository.save(friendship);
    }

    @Transactional(readOnly = true)
    public List<UserProfileDTO> getFriends() {
        UserProfile currentUser = getCurrentUserProfile();
        List<Friendship> friendships = friendshipRepository.findAcceptedForUser(currentUser.getId());
        List<UserProfileDTO> friends = new ArrayList<>();
        for (Friendship f : friendships) {
            UserProfile friend;
            if (f.getRequester().getId().equals(currentUser.getId())) {
                friend = f.getAddressee();
            } else {
                friend = f.getRequester();
            }
            friends.add(userProfileMapper.toDto(friend));
        }

        return friends;
    }

    private UserProfile getCurrentUserProfile() {
        String currentUsername = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("No current user"));
        return userProfileRepository
            .findByUserLogin(currentUsername)
            .orElseThrow(() -> new RuntimeException("User profile not found for current user"));
    }
}
