package bham.team.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class AvailiabilityBlockTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AvailiabilityBlock getAvailiabilityBlockSample1() {
        return new AvailiabilityBlock().id(1L);
    }

    public static AvailiabilityBlock getAvailiabilityBlockSample2() {
        return new AvailiabilityBlock().id(2L);
    }

    public static AvailiabilityBlock getAvailiabilityBlockRandomSampleGenerator() {
        return new AvailiabilityBlock().id(longCount.incrementAndGet());
    }
}
