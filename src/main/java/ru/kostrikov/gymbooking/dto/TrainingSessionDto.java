package ru.kostrikov.gymbooking.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TrainingSessionDto {
    String id;
//    String gym;
    GymDto gym;
    TrainerDto trainer;
//    String trainer;
    String type;
    String description;
    String date;
    String startTime;
    @Min(1)
    String duration;
    String price;
    @Min(2)
    @Max(20)
    String capacity;
}
