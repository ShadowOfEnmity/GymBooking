package ru.kostrikov.gymbooking.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TrainingSessionWithPresentationDto {
    Long id;
    String presentation;
}
