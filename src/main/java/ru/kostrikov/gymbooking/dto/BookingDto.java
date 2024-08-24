package ru.kostrikov.gymbooking.dto;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Value
@Builder
public class BookingDto {
    String id;
    //    UserDto user;
    String bookingDate;
    String status;
    String paymentStatus;
//    List<TrainingSessionDto> training;
}
