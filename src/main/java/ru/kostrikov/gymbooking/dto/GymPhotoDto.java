package ru.kostrikov.gymbooking.dto;

import jakarta.servlet.http.Part;
import lombok.Builder;
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
