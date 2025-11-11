package bham.team.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class SuggestionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Suggestion getSuggestionSample1() {
        return new Suggestion().id(1L);
    }

    public static Suggestion getSuggestionSample2() {
        return new Suggestion().id(2L);
    }

    public static Suggestion getSuggestionRandomSampleGenerator() {
        return new Suggestion().id(longCount.incrementAndGet());
    }
}
