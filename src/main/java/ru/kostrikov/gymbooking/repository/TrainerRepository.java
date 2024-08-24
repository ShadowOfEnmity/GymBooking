package ru.kostrikov.gymbooking.repository;

import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import ru.kostrikov.gymbooking.entity.Trainer;
import ru.kostrikov.gymbooking.entity.TrainingSession;

import java.util.List;

@Log4j2
public class TrainerRepository extends RepositoryBase<Long, Trainer> {

    public TrainerRepository(EntityManager entityManager) {
        super(Trainer.class, entityManager);
    }

    @Override
    public List<Trainer> findAll() {
        return getEntityManager().createQuery("FROM Trainer t ORDER BY t.personalInfo.firstName, t.personalInfo.lastName", getClazz())
                .getResultList();
    }

    @Override
    public void delete(Trainer entity) {
        super.delete(entity);
        log.info("Trainer {} is deleted", entity);
    }

    @Override
    public Trainer save(Trainer entity) {
        var persistedUser = super.save(entity);
        log.info("New {} is created", entity);
        return persistedUser;
    }

    public List<TrainingSession> findSessionTrainingsByTrainerId(Long id) {
        return getEntityManager().createQuery("FROM TrainingSession WHERE trainer.id = :trainerID", TrainingSession.class)
                .setParameter("trainerID", id)
                .getResultList();
    }
}
