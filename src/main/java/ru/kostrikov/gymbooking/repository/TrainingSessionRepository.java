package ru.kostrikov.gymbooking.repository;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import ru.kostrikov.gymbooking.entity.TrainingSession;

import java.util.List;

public class TrainingSessionRepository extends RepositoryBase<Long, TrainingSession>{
    public TrainingSessionRepository(EntityManager entityManager) {
        super(TrainingSession.class, entityManager);
    }

    public List<TrainingSession> findAll() {
        return getEntityManager().createQuery("FROM TrainingSession", TrainingSession.class)
                .getResultList();
    }

    public List<TrainingSession> findByIds(List<Long> ids) {
        return getEntityManager().createQuery("FROM TrainingSession WHERE id IN (:ids) ORDER BY id", TrainingSession.class)
                .setParameter("ids", ids).getResultList();
    }

    public long countTraining() {
        return getEntityManager().createQuery("SELECT COUNT(t) FROM TrainingSession t", Long.class)
                .getResultStream().findFirst().orElse(0L);
    }

    public List<TrainingSession> findAllByGym(int pageNumber, int pageSize, long gymId) {
        return getEntityManager().createQuery("FROM TrainingSession t WHERE t.gym.id = :gymId ORDER BY t.id", TrainingSession.class)
                .setParameter("gymId", gymId)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public long countTrainingByGym(Long gymId) {
        return getEntityManager().createQuery("SELECT COUNT(t) FROM TrainingSession t WHERE t.gym.id = :gymId", Long.class)
                .setParameter("gymId", gymId)
                .getResultStream().findFirst().orElse(0L);
    }
}
