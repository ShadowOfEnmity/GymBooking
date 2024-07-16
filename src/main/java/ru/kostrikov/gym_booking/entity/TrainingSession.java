package ru.kostrikov.gym_booking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.kostrikov.gym_booking.converter.DateConverter;
import ru.kostrikov.gym_booking.converter.TimeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@EqualsAndHashCode(exclude = {"bookings"})
@ToString(exclude = {"bookings"})
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "training_session")
public class TrainingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "gym_id", nullable = false)
    private Gym gym;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(name = "booking_training",
            joinColumns = @JoinColumn(name = "training_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "booking_id", referencedColumnName = "id")
    )
//    uniqueConstraints = @UniqueConstraint(columnNames = {"training_id", "booking_id"})
    List<Booking> bookings = new ArrayList<>();
    private String type;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Convert(converter = DateConverter.class)
    private LocalDate date;
    @Convert(converter = TimeConverter.class)
    private LocalTime startTime;
    @Min(1)
    private Integer duration;
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    @Min(2)
    @Max(20)
    private Integer capacity;

    public void addTrainer(Trainer trainer){
        this.trainer = trainer;
        trainer.getTraining().add(this);
    }

    public void addGym(Gym gym){
        this.gym = gym;
        gym.getTraining().add(this);
    }
}
