package bham.team.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class FriendshipTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Friendship getFriendshipSample1() {
        return new Friendship().id(1L);
    }

    public static Friendship getFriendshipSample2() {
        return new Friendship().id(2L);
    }

    public static Friendship getFriendshipRandomSampleGenerator() {
        return new Friendship().id(longCount.incrementAndGet());
    }
}
