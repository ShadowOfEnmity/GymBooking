package ru.kostrikov.gymbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.ArrayList;
import java.util.List;

@NamedEntityGraph(name = "gymWithPhotos", attributeNodes = {
        @NamedAttributeNode("photos"),
})
@Data
//@NoArgsConstructor
//@AllArgsConstructor
@EqualsAndHashCode(of = {"latitude", "longitude"})
@ToString(of = {"name", "address", "description", "latitude", "longitude"})
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"latitude", "longitude"}))
public class Gym implements BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String address;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "latitude", length = 10, nullable = false, unique = true)
    private String latitude;

    @Column(name = "longitude", length = 11, nullable = false, unique = true)
    private String longitude;

    @Column(length = 20)
    private String phone;

    private String website;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Builder.Default
    @OneToMany(mappedBy = "gym", cascade = {CascadeType.ALL}, orphanRemoval = true)
    List<GymPhoto> photos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "gym", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE}, orphanRemoval = true)
    List<TrainingSession> training = new ArrayList<>();

    public Gym() {
    }

    @Builder
    public Gym(Long id, String name, String address, String description, String latitude, String longitude, String phone, String website) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.website = website;
    }

    public void addTraining(TrainingSession tSession) {
        if (tSession != null) {
            training.add(tSession);
            tSession.setGym(this);
        }
    }

    public void removeTraining(TrainingSession tSession) {
        if (tSession != null) {
            training.remove(tSession);
            tSession.setGym(null);
        }
    }

    public void addPhoto(GymPhoto photo) {
        if (photo != null) {
            photo.setGym(this);
        }
    }

    public void removePhoto(GymPhoto photo) {
        if (photo != null) {
            photo.setGym(null);
        }
    }

}
