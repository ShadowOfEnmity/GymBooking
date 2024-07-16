package ru.kostrikov.gym_booking.dao;

import jakarta.persistence.DiscriminatorValue;
import jakarta.validation.ConstraintViolation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.kostrikov.gym_booking.entity.PersonalInfo;
import ru.kostrikov.gym_booking.entity.Trainer;
import ru.kostrikov.gym_booking.entity.User;
import ru.kostrikov.gym_booking.exception.ValidationException;
import ru.kostrikov.gym_booking.utils.ValidationUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TrainerDao implements Dao<Long, Trainer> {

    private static final TrainerDao INSTANCE = new TrainerDao();

    public static TrainerDao getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<Trainer> findById(Long id, Session session) {
        Trainer trainer = null;
        session.beginTransaction();
        trainer = session.get(Trainer.class, id);
        session.getTransaction().commit();
        return Optional.ofNullable(trainer);
    }

    @Override
    public List<Trainer> findAll(int pageNumber, int pageSize, Session session) {
        return session.createQuery("FROM Trainer t ORDER BY t.personalInfo.firstName, t.personalInfo.lastName", Trainer.class)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public List<Trainer> findAll(Session session) {
        return session.createQuery("FROM Trainer t ORDER BY t.personalInfo.firstName, t.personalInfo.lastName", Trainer.class)
                .getResultList();
    }

    @Override
    public void update(Trainer entity, Session session) {
        Transaction tx = null;
        Set<ConstraintViolation<PersonalInfo>> personalInfoViolations = ValidationUtil.validate(entity.getPersonalInfo());
        Set<ConstraintViolation<Trainer>> trainerViolations = ValidationUtil.validate(entity);

        Set<ConstraintViolation<?>> mergedViolations = new HashSet<>();
        mergedViolations.addAll(trainerViolations);
        mergedViolations.addAll(personalInfoViolations);

        if (!mergedViolations.isEmpty()) {
            throw new ValidationException(mergedViolations);
        }

        Optional<User> oldTrainer = Optional.ofNullable(session.get(User.class, entity.getId()));
        oldTrainer.ifPresent(trainer -> {
            entity.getBookings().addAll(trainer.getBookings());
            entity.getTraining().addAll(entity.getTraining());
        });

        try {
            tx = session.beginTransaction();
            if (oldTrainer.map(trainer -> trainer.getClass().getAnnotation(DiscriminatorValue.class).value()).filter("USER"::equals).isPresent()) {
                session.remove(oldTrainer.get());
                session.flush();
            }
            session.merge(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        }
    }

    @Override
    public Trainer save(Trainer entity, Session session) {
        Transaction tx = null;
        Set<ConstraintViolation<PersonalInfo>> personalInfoViolations = ValidationUtil.validate(entity.getPersonalInfo());
        Set<ConstraintViolation<Trainer>> trainerViolations = ValidationUtil.validate(entity);

        Set<ConstraintViolation<?>> mergedViolations = new HashSet<>();
        mergedViolations.addAll(trainerViolations);
        mergedViolations.addAll(personalInfoViolations);
        if (!mergedViolations.isEmpty()) {
            throw new ValidationException(mergedViolations);
        }
        try {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            log.info("New {} {} {} is created", entity.getPersonalInfo().getRole(), entity.getPersonalInfo().getFirstName(), entity.getPersonalInfo().getLastName());
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        }
        return entity;
    }

    @Override
    public void delete(Trainer entity, Session session) {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
            log.info("{} {} {} is deleted", entity.getPersonalInfo().getRole(), entity.getPersonalInfo().getFirstName(), entity.getPersonalInfo().getLastName());
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        }
    }
}
