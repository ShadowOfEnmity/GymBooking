package ru.kostrikov.gym_booking.dao;

import jakarta.validation.ConstraintViolation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.kostrikov.gym_booking.entity.*;
import ru.kostrikov.gym_booking.exception.ValidationException;
import ru.kostrikov.gym_booking.utils.ValidationUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TrainingSessionDao implements Dao<Long, TrainingSession> {

    private static final TrainingSessionDao INSTANCE = new TrainingSessionDao();

    public static TrainingSessionDao getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<TrainingSession> findById(Long id, Session session) {
        TrainingSession training = null;
        session.beginTransaction();
        training = session.get(TrainingSession.class, id);
        session.getTransaction().commit();
        return Optional.ofNullable(training);
    }


    @Override
    public List<TrainingSession> findAll(int pageNumber, int pageSize, Session session) {
        return session.createQuery("FROM TrainingSession t", TrainingSession.class)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public List<TrainingSession> findAll(Session session) {
        return session.createQuery("FROM TrainingSession", TrainingSession.class)
                .getResultList();
    }

    @Override
    public void update(TrainingSession entity, Session session) {
        Transaction tx = null;

        Set<ConstraintViolation<TrainingSession>> personalInfoViolations = ValidationUtil.validate(entity);

        if (!personalInfoViolations.isEmpty()) {
            throw new ValidationException(personalInfoViolations);
        }

        try {
            tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        }
    }

    @Override
    public TrainingSession save(TrainingSession entity, Session session) {
        Transaction tx = null;

        Set<ConstraintViolation<TrainingSession>> personalInfoViolations = ValidationUtil.validate(entity);

        if (!personalInfoViolations.isEmpty()) {
            throw new ValidationException(personalInfoViolations);
        }

        try {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        }
        return entity;
    }

    @Override
    public void delete(TrainingSession entity, Session session) {
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        }
    }

    public List<TrainingSession> findByIds(List<Long> ids, Session session) {
        return session.createQuery("FROM TrainingSession WHERE id IN (:ids) ORDER BY id", TrainingSession.class)
                .setParameter("ids", ids).getResultList();
    }

    public long countTraining(Session session) {
        return session.createQuery("SELECT COUNT(t) FROM TrainingSession t", Long.class)
                .getSingleResult();
    }

    public List<TrainingSession> findAllByGym(int pageNumber, int pageSize, long gymId, Session session) {
        return session.createQuery("FROM TrainingSession t WHERE t.gym.id = :gymId ORDER BY t.id", TrainingSession.class)
                .setParameter("gymId", gymId)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public long countTrainingByGym(Long gymId, Session session) {
        return session.createQuery("SELECT COUNT(t) FROM TrainingSession t WHERE t.gym.id = :gymId", Long.class)
                .setParameter("gymId", gymId)
                .getSingleResult();
    }
}
