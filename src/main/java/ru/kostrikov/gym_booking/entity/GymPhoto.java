package ru.kostrikov.gym_booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class GymPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "gym_id")
    private Gym gym;

    private String alt;

    @Column(nullable = false, length = 255)
//    @URL(message = "Url is not correct")
    private String imageUrl;

    public void addGymPhoto(Gym gym) {
        gym.getPhotos().add(this);
        this.gym = gym;
    }
}
