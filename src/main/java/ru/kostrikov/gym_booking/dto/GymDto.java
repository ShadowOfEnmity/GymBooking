package ru.kostrikov.gym_booking.dto;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@ToString(of = "presentation")
@Value
@Builder
public class GymDto {
    String id;
    String name;
    String address;
    String description;
    String latitude;
    String longitude;
    String phone;
    String website;
    String presentation;
}
