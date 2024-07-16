package ru.kostrikov.gym_booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PersonalInfo {

    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Incorrect email")
    private String email;

    @NotEmpty
    @Size(min = 2, max = 255, message = "The name must be between 2 and 255 characters")
    @Column(nullable = false)
    private String firstName;

    @NotEmpty
    @Size(min = 2, max = 255, message = "The last name must be between 2 and 255 characters")
//    private String name;
    @Column(nullable = false)
    private String lastName;


    @NotEmpty
    @Pattern(regexp = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$", message = "Invalid phone number format")
    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(length = 25, nullable = false)
    private Role role = Role.USER;
}
