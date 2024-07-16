package ru.kostrikov.gym_booking.dao;

import jakarta.persistence.DiscriminatorValue;
import jakarta.validation.ConstraintViolation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.kostrikov.gym_booking.entity.PersonalInfo;
import ru.kostrikov.gym_booking.entity.Role;
import ru.kostrikov.gym_booking.entity.User;
import ru.kostrikov.gym_booking.exception.ValidationException;
import ru.kostrikov.gym_booking.utils.ValidationUtil;

import java.util.*;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao implements Dao<Long, User> {

    private static final UserDao INSTANCE = new UserDao();

    public static UserDao getInstance() {
        return INSTANCE;
    }

    public long countUsers(Session session) {
        return session.createQuery("SELECT COUNT(u) FROM User u", Long.class)
                .getSingleResult();
    }

    @Override
    public Optional<User> findById(Long id, Session session) {
        User user = null;
        session.beginTransaction();
        user = session.get(User.class, id);
        session.getTransaction().commit();
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll(int pageNumber, int pageSize, Session session) {
        return session.createQuery("from User u ORDER BY u.personalInfo.firstName, u.personalInfo.lastName ", User.class)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public void update(User entity, Session session) throws ValidationException {
        Transaction tx = null;
        Set<ConstraintViolation<PersonalInfo>> personalInfoViolations = ValidationUtil.validate(entity.getPersonalInfo());
        Set<ConstraintViolation<User>> userViolations = ValidationUtil.validate(entity);

        Set<ConstraintViolation<?>> mergedViolations = new HashSet<>();
        mergedViolations.addAll(userViolations);
        mergedViolations.addAll(personalInfoViolations);

        if (!mergedViolations.isEmpty()) {
            throw new ValidationException(mergedViolations);
        }

        try {
            tx = session.beginTransaction();
            Optional<User> oldUser = Optional.ofNullable(session.get(User.class, entity.getId()));
            oldUser.ifPresent(user -> entity.getBookings().addAll(user.getBookings()));
            if (oldUser.map(user -> user.getClass().getAnnotation(DiscriminatorValue.class).value()).filter("TRAINER"::equals).isPresent()) {
                session.remove(oldUser.get());
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
    public User save(User entity, Session session) throws ValidationException {
        Transaction tx = null;
        Set<ConstraintViolation<PersonalInfo>> personalInfoViolations = ValidationUtil.validate(entity.getPersonalInfo());
        Set<ConstraintViolation<User>> userViolations = ValidationUtil.validate(entity);

        Set<ConstraintViolation<?>> mergedViolations = new HashSet<>();
        mergedViolations.addAll(userViolations);
        mergedViolations.addAll(personalInfoViolations);
        if (entity.getPersonalInfo().getRole() != Role.ADMIN && !mergedViolations.isEmpty()) {
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
    public void delete(User entity, Session session) {
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

    public User findByLoginAndPassword(String login, String password, Session session) {
        return session.createQuery("FROM User u WHERE u.login = :login  AND u.password = :password", User.class)
                .setParameter("login", login)
                .setParameter("password", password)
                .getSingleResultOrNull();
    }

    public void createIfAdministratorNotExists(User user, Session session) throws ValidationException {
        long count = Optional.ofNullable(session.createQuery("SELECT COUNT(u) FROM User u WHERE u.personalInfo.role = :role", long.class)
                .setParameter("role", user.getPersonalInfo().getRole())
                .uniqueResult()).orElse(0L);
        if (count == 0) {
            save(User.builder().login("Admin").password("Admin").personalInfo(PersonalInfo.builder().firstName("Admin").lastName("Admin").role(Role.ADMIN).build()).build(), session);
        }
    }
}
