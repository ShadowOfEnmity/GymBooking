package ru.kostrikov.gymbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import ru.kostrikov.gymbooking.converter.DateConverter;
import ru.kostrikov.gymbooking.converter.TimeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NamedEntityGraphs({
        @NamedEntityGraph(name = "trainingWithGymTrainerAndBookings", attributeNodes = {
                @NamedAttributeNode("gym"),
                @NamedAttributeNode("trainer"),
                @NamedAttributeNode("bookings")
        }),
        @NamedEntityGraph(name = "trainingWithBookings", attributeNodes = {
                @NamedAttributeNode("bookings")
        }),
}
)
@EqualsAndHashCode(of = {"trainer", "date", "startTime"})
@ToString(exclude = {"bookings"})
@Entity
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Table(name = "training_session", uniqueConstraints = @UniqueConstraint(columnNames = {"trainer_id", "date", "start_time"}))
public class TrainingSession implements BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "gym_id", nullable = false)
    private Gym gym;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;
    @Builder.Default
    @ManyToMany(mappedBy = "training", cascade = {CascadeType.REMOVE})
//    uniqueConstraints = @UniqueConstraint(columnNames = {"training_id", "booking_id"})
    private List<Booking> bookings = new ArrayList<>();
    private String type;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Convert(converter = DateConverter.class)
    private LocalDate date;
    @Convert(converter = TimeConverter.class)
    @Column(name = "start_time")
    private LocalTime startTime;
    private Integer duration;
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    private Integer capacity;

    public TrainingSession() {
    }

    @Builder
    public TrainingSession(Long id, Gym gym, Trainer trainer, String type, String description, LocalDate date, LocalTime startTime, Integer duration, BigDecimal price, Integer capacity) {
        this.id = id;
        this.gym = gym;
        this.trainer = trainer;
        this.type = type;
        this.description = description;
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
        this.price = price;
        this.capacity = capacity;
    }

    public void addBooking(Booking booking) {
        if (booking != null) {
            bookings.add(booking);
            booking.getTraining().add(this);
        }
    }

    public void removeBooking(Booking booking) {
        if (booking != null) {
            bookings.remove(booking);
            booking.getTraining().remove(this);
        }
    }

    public void setTrainer(Trainer trainer) {
        if (trainer != null) {
            this.trainer = trainer;
            trainer.getTrainings().add(this);
        } else {
            this.trainer.getTrainings().remove(this);
            this.trainer = null;
        }
    }

    public void setGym(Gym gym) {
        if (gym != null) {
            gym.getTraining().add(this);
            this.gym = gym;
        } else {
            this.gym.getTraining().remove(this);
            this.gym = null;
        }
    }

}
