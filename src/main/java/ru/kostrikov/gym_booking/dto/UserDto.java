package ru.kostrikov.gym_booking.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserDto {
    String id;
    String password;
    //    String oldPassword;
    String login;
    String email;
    String firstName;
    String lastName;
    String phone;
    String role;
    String fullName;

//    List<BookingDto> bookings;
}
