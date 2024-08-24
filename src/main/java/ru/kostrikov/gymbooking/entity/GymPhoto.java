package ru.kostrikov.gymbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "imageUrl")
@ToString(exclude = "gym")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@Entity
public class GymPhoto implements BaseEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id", nullable = false)
    private Gym gym;

    private String alt;

    @Column(nullable = false, length = 255, unique = true)
//    @URL(message = "Url is not correct")
    private String imageUrl;

    public void setGym(Gym gym){
        if(gym != null){
            gym.photos.add(this);
            this.gym = gym;
        }else{
            this.gym.photos.remove(this);
            this.gym = null;
        }
    }

}
