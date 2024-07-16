package ru.kostrikov.gym_booking.dto;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import ru.kostrikov.gym_booking.dao.UserDao;
import ru.kostrikov.gym_booking.entity.Gym;

import java.math.BigDecimal;

@ToString(of = "id")
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
