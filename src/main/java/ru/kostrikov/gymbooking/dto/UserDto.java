package ru.kostrikov.gymbooking.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Value
@Builder
public class UserDto {
    String id;
    @NotBlank(message = "Password can't be empty")
    String password;
    //    String oldPassword;
    @Size(min = 2, max = 255, message = "Login must be between 2 and 255 characters")
    @NotBlank(message = "Password can't be empty")
    String login;

    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Incorrect email")
    String email;

    @Size(min = 2, max = 255, message = "The name must be between 2 and 255 characters")
    @NotEmpty
    String firstName;

    @Size(min = 2, max = 255, message = "The last name must be between 2 and 255 characters")
    @NotEmpty
    String lastName;

    @NotEmpty
    @Pattern(regexp = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$", message = "Invalid phone number format")
    String phone;
    String role;
    String fullName;

//    List<BookingDto> bookings;
}
