package ru.kostrikov.gym_booking.dto;

import jakarta.servlet.http.Part;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Value
@Builder
public class GymPhotoDto {
    Long id;
    String imageUrl;
    String alt;
    GymDto gym;
    Part image;
}
