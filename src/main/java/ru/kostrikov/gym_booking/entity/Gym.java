package ru.kostrikov.gym_booking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"name", "description", "latitude", "longitude"})
@Builder
@Entity
public class Gym {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Pattern(regexp = "^(-?([0-8]?[0-9](\\.\\d{1,6})?|90(\\.0{1,6})?))$",
            message = "Invalid latitude. Must be between -90.000000 and 90.000000.")
    @Column(name = "latitude", length = 10)
    private String latitude;

    @Pattern(regexp = "^(-?([0-1]?[0-7]?[0-9](\\.\\d{1,6})?|180(\\.0{1,6})?))$",
            message = "Invalid longitude. Must be between -180.000000 and 180.000000.")
    @Column(name = "longitude", length = 11)
    private String longitude;

    @NotEmpty
    @Pattern(regexp = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$", message = "Invalid phone number format")
    @Column(length = 20)
    private String phone;

    @Pattern(regexp = "^(https?://)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(\\.[a-zA-Z]{2,})?(/\\S*)?$", message = "Web site is not correct")
    private String website;

    @Builder.Default
    @OneToMany(mappedBy = "gym", cascade = {CascadeType.ALL}, orphanRemoval = true)
    List<GymPhoto> photos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "gym")
    List<TrainingSession> training = new ArrayList<>();

}
