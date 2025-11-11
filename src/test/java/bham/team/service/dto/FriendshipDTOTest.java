package bham.team.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import bham.team.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FriendshipDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FriendshipDTO.class);
        FriendshipDTO friendshipDTO1 = new FriendshipDTO();
        friendshipDTO1.setId(1L);
        FriendshipDTO friendshipDTO2 = new FriendshipDTO();
        assertThat(friendshipDTO1).isNotEqualTo(friendshipDTO2);
        friendshipDTO2.setId(friendshipDTO1.getId());
        assertThat(friendshipDTO1).isEqualTo(friendshipDTO2);
        friendshipDTO2.setId(2L);
        assertThat(friendshipDTO1).isNotEqualTo(friendshipDTO2);
        friendshipDTO1.setId(null);
        assertThat(friendshipDTO1).isNotEqualTo(friendshipDTO2);
    }
}
