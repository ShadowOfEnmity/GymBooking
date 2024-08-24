package ru.kostrikov.gymbooking.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Value
@Builder
public class GymDto {
    String id;
    String name;
    String address;
    String description;
    @Pattern(regexp = "^(-?([0-8]?[0-9](\\.\\d{1,6})?|90(\\.0{1,6})?))$",
            message = "Invalid latitude. Must be between -90.000000 and 90.000000.")
    String latitude;
    @Pattern(regexp = "^(-?([0-1]?[0-7]?[0-9](\\.\\d{1,6})?|180(\\.0{1,6})?))$",
            message = "Invalid longitude. Must be between -180.000000 and 180.000000.")
    String longitude;
    @NotEmpty
    @Pattern(regexp = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$", message = "Invalid phone number format")
    String phone;
    @Pattern(regexp = "^(https?://)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(\\.[a-zA-Z]{2,})?(/\\S*)?$", message = "Web site is not correct")
    String website;
    String presentation;
}
