package bham.team.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FindTimeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static FindTime getFindTimeSample1() {
        return new FindTime().id(1L).length(1);
    }

    public static FindTime getFindTimeSample2() {
        return new FindTime().id(2L).length(2);
    }

    public static FindTime getFindTimeRandomSampleGenerator() {
        return new FindTime().id(longCount.incrementAndGet()).length(intCount.incrementAndGet());
    }
}
