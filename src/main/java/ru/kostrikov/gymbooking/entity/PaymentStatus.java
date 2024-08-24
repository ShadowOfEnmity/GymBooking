package ru.kostrikov.gymbooking.entity;

import java.util.Arrays;
import java.util.Optional;

public enum PaymentStatus {
    UNPAID, PAID;

    public static Optional<PaymentStatus> find(String status) {
        return Arrays.stream(values())
                .filter(it -> it.name().equals(status))
                .findFirst();
    }
}
