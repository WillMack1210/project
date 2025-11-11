package bham.team.domain;

import static bham.team.domain.AvailiabilityBlockTestSamples.*;
import static bham.team.domain.UserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import bham.team.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AvailiabilityBlockTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AvailiabilityBlock.class);
        AvailiabilityBlock availiabilityBlock1 = getAvailiabilityBlockSample1();
        AvailiabilityBlock availiabilityBlock2 = new AvailiabilityBlock();
        assertThat(availiabilityBlock1).isNotEqualTo(availiabilityBlock2);

        availiabilityBlock2.setId(availiabilityBlock1.getId());
        assertThat(availiabilityBlock1).isEqualTo(availiabilityBlock2);

        availiabilityBlock2 = getAvailiabilityBlockSample2();
        assertThat(availiabilityBlock1).isNotEqualTo(availiabilityBlock2);
    }

    @Test
    void userTest() {
        AvailiabilityBlock availiabilityBlock = getAvailiabilityBlockRandomSampleGenerator();
        UserProfile userProfileBack = getUserProfileRandomSampleGenerator();

        availiabilityBlock.setUser(userProfileBack);
        assertThat(availiabilityBlock.getUser()).isEqualTo(userProfileBack);

        availiabilityBlock.user(null);
        assertThat(availiabilityBlock.getUser()).isNull();
    }
}
