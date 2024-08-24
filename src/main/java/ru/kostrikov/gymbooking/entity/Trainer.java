package ru.kostrikov.gymbooking.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//@SuperBuilder
@Data
//@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"trainings"}, callSuper = true)
@NoArgsConstructor
//@AllArgsConstructor
@Entity
@DiscriminatorValue("TRAINER")
public class Trainer extends User {

    //    @Builder.Default
    @OneToMany(mappedBy = "trainer", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<TrainingSession> trainings = new ArrayList<>();
    private String specialization;
    @Column(columnDefinition = "TEXT")
    private String experience;
    @Column(precision = 2, scale = 1)
    private BigDecimal rating;
    private Boolean availability;

    @Builder(builderMethodName = "TrainerBuilder")
    public Trainer(Long id, String login, String password, PersonalInfo personalInfo, String specialization, String experience, BigDecimal rating, Boolean availability) {
        super(id, login, password, personalInfo);
        this.specialization = specialization;
        this.experience = experience;
        this.rating = rating;
        this.availability = availability;
    }

    public void addTraining(TrainingSession training) {
        training.setTrainer(this);
    }

    public void removeTraining(TrainingSession training) {
        training.setTrainer(null);
    }

    public boolean equals(final Object o) {
        return super.equals(o);
    }


    public int hashCode() {
        return super.hashCode();
    }
}
