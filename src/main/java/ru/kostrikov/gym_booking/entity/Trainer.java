package ru.kostrikov.gym_booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"training"}, callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("TRAINER")
public class Trainer extends User {

    @Builder.Default
    @OneToMany(mappedBy = "trainer")
    private List<TrainingSession> training = new ArrayList<>();
    private String specialization;
    @Column(columnDefinition = "TEXT")
    private String experience;
    @Column(precision = 2, scale = 1)
    private BigDecimal rating;
    private Boolean availability;
}
