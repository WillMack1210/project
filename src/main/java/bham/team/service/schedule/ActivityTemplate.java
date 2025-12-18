package bham.team.service.schedule;

import java.time.Duration;

public record ActivityTemplate(String title, Duration duration, int count) {}
