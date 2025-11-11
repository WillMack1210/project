package bham.team.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserProfileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UserProfile getUserProfileSample1() {
        return new UserProfile().id(1L).username("username1").fullName("fullName1").settings("settings1");
    }

    public static UserProfile getUserProfileSample2() {
        return new UserProfile().id(2L).username("username2").fullName("fullName2").settings("settings2");
    }

    public static UserProfile getUserProfileRandomSampleGenerator() {
        return new UserProfile()
            .id(longCount.incrementAndGet())
            .username(UUID.randomUUID().toString())
            .fullName(UUID.randomUUID().toString())
            .settings(UUID.randomUUID().toString());
    }
}
