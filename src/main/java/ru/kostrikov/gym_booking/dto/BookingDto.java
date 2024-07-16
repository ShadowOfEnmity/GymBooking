package ru.kostrikov.gym_booking.dto;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import ru.kostrikov.gym_booking.entity.TrainingSession;

import java.util.List;

@ToString(of = {"id", "bookingDate", "status", "paymentStatus"})
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
