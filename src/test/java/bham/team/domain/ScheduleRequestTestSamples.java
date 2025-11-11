package bham.team.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ScheduleRequestTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ScheduleRequest getScheduleRequestSample1() {
        return new ScheduleRequest().id(1L);
    }

    public static ScheduleRequest getScheduleRequestSample2() {
        return new ScheduleRequest().id(2L);
    }

    public static ScheduleRequest getScheduleRequestRandomSampleGenerator() {
        return new ScheduleRequest().id(longCount.incrementAndGet());
    }
}
