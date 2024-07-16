package ru.kostrikov.gym_booking.dto;

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
    String duration;
    String price;
    String capacity;
}
