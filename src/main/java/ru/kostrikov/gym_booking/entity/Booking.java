package ru.kostrikov.gym_booking.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.kostrikov.gym_booking.converter.DateConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@EqualsAndHashCode(exclude = {"id"})
@ToString(exclude = {"user", "training"})
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Builder.Default
    @ManyToMany(mappedBy = "bookings", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<TrainingSession> training = new ArrayList<>();

    @Convert(converter = DateConverter.class)
    LocalDate bookingDate;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    public void addTraining(TrainingSession tSession){
        training.add(tSession);
        tSession.getBookings().add(this);
    }

}
