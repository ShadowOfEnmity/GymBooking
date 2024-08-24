package ru.kostrikov.gymbooking.dto;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Value
@Builder
public class TrainerDto {
    String id;
    String specialization;
    String experience;
    String rating;
    boolean availability;
    UserDto user;
    String presentation;
//    String presentation;
}
