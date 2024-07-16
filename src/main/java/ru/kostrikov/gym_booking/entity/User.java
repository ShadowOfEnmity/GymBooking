package ru.kostrikov.gym_booking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Data
@ToString(exclude = "bookings")
@EqualsAndHashCode(of = {"id"})
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorColumn(name = "type")
@DiscriminatorValue("USER")
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(generator = "user_gen", strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 2, max = 255, message = "Login must be between 2 and 255 characters")
    @NotBlank(message = "Password can't be empty")
    @Column(unique = true, nullable = false)
    private String login;
    @NotBlank(message = "Password can't be empty")
    @Column(nullable = false)
    private String password;
    @Embedded
    private PersonalInfo personalInfo;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
    private List<Booking> bookings = new ArrayList<>();
}
