package ru.kostrikov.gymbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import ru.kostrikov.gymbooking.converter.DateConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//@Builder
@Data
@EqualsAndHashCode(of = {"user", "bookingDate"})
@ToString(exclude = {"training"})
@NoArgsConstructor
//@AllArgsConstructor
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"booking_date", "user_id"}))
@Entity
public class Booking implements BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotAudited
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "booking_training",
            joinColumns = @JoinColumn(name = "training_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "booking_id", referencedColumnName = "id")
    )
    List<TrainingSession> training = new ArrayList<>();

    @Convert(converter = DateConverter.class)
    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Builder
    public Booking(Long id, LocalDate bookingDate, Status status, PaymentStatus paymentStatus) {
        this.id = id;
        this.bookingDate = bookingDate;
        this.status = status;
        this.paymentStatus = paymentStatus;
    }

    public void addTraining(TrainingSession trainingSession) {
        if (trainingSession != null) {
            training.add(trainingSession);
            trainingSession.getBookings().add(this);
        }
    }

    public void removeTraining(TrainingSession trainingSession) {
        if (trainingSession != null) {
            trainingSession.getBookings().remove(this);
            training.remove(trainingSession);
        }
    }

    public void setUser(User user) {
        if (user != null) {
            this.user = user;
            user.getBookings().add(this);
        } else {
            this.user.getBookings().remove(this);
            this.user = null;
        }
    }


}
