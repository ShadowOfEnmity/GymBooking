package ru.kostrikov.gym_booking.dao;

import jakarta.validation.ConstraintViolation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.kostrikov.gym_booking.entity.Gym;
import ru.kostrikov.gym_booking.exception.ValidationException;
import ru.kostrikov.gym_booking.utils.ValidationUtil;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GymDao implements Dao<Long, Gym> {

    private static final GymDao INSTANCE = new GymDao();

    public static GymDao getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<Gym> findById(Long id, Session session) {
        Gym gym = null;
        session.beginTransaction();
        gym = session.get(Gym.class, id);
        session.getTransaction().commit();
        return Optional.ofNullable(gym);
    }

    public long countGyms(Session session) {
        return session.createQuery("SELECT COUNT(*) FROM Gym g", Long.class)
                .getSingleResult();
    }

    @Override
    public List<Gym> findAll(int pageNumber, int pageSize, Session session) {
        return session.createQuery("FROM Gym g ORDER BY g.id", Gym.class)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public List<Gym> findAll(Session session) {
        return session.createQuery("FROM Gym g ORDER BY g.name", Gym.class)
                .getResultList();
    }

    @Override
    public void update(Gym entity, Session session) {
        Transaction tx = null;
        Set<ConstraintViolation<Gym>> gymViolations = ValidationUtil.validate(entity);

        if (!gymViolations.isEmpty()) {
            throw new ValidationException(gymViolations);
        }

        findById(entity.getId(), session).ifPresent(
                old -> {
                    entity.getTraining().addAll(old.getTraining());
                    entity.getPhotos().addAll(old.getPhotos());
                });
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
    public Gym save(Gym entity, Session session) {
        Transaction tx = null;

        Set<ConstraintViolation<Gym>> gymViolations = ValidationUtil.validate(entity);

        if (!gymViolations.isEmpty()) {
            throw new ValidationException(gymViolations);
        }

        try {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            log.info("New gym {} is created", entity.getName());
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        }
        return entity;
    }

    @Override
    public void delete(Gym entity, Session session) {
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
            log.info("Gym {} is removed", entity.getName());
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        }

    }
}
