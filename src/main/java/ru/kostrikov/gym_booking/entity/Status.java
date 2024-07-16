package ru.kostrikov.gym_booking.entity;

import java.util.Arrays;
import java.util.Optional;

public enum Status {
    PENDING, CONFIRMED, CANCELLED;

    public static Optional<Status> find(String status) {
        return Arrays.stream(values())
                .filter(it -> it.name().equals(status))
                .findFirst();
    }
}
