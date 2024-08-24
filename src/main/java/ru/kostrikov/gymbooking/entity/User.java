package ru.kostrikov.gymbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//@SuperBuilder
@Data
@ToString(exclude = {"bookings"})
@EqualsAndHashCode(of = {"login"})
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@DiscriminatorColumn(name = "type")
@DiscriminatorValue("USER")
@Table(name = "users")
public class User implements BaseEntity<Long> {

    @Id
    @GeneratedValue(generator = "user_gen", strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 255)
    private String login;

    @Column(nullable = false, length = 20)
    private String password;

    @Embedded
    private PersonalInfo personalInfo;


//    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private List<Booking> bookings = new ArrayList<>();

    @Builder
    public User(Long id, String login, String password, PersonalInfo personalInfo) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.personalInfo = personalInfo;
    }

   public void addBooking(Booking booking){
        booking.setUser(this);
   }

   public void removeBooking(Booking booking){
        booking.setUser(null);
   }
}
