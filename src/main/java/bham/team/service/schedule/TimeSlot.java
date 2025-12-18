package bham.team.service.schedule;

import java.time.Duration;
import java.time.Instant;

public class TimeSlot {

    private Instant start;
    private Instant end;

    public TimeSlot(Instant start, Instant end) {
        this.start = start;
        this.end = end;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

    public Duration length() {
        return Duration.between(start, end);
    }

    public void consume(Duration d) {
        this.start = start.plus(d);
    }
}
