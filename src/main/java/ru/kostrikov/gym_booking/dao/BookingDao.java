package ru.kostrikov.gym_booking.dao;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.kostrikov.gym_booking.entity.Booking;
import ru.kostrikov.gym_booking.entity.TrainingSession;

import java.util.List;
import java.util.Optional;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingDao implements Dao<Long, Booking> {

    private static final BookingDao INSTANCE = new BookingDao();

    public static BookingDao getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<Booking> findById(Long id, Session session) {
        Booking booking = null;
        session.beginTransaction();
        booking = session.get(Booking.class, id);
        session.getTransaction().commit();
        return Optional.ofNullable(booking);
    }

    @Override
    public List<Booking> findAll(int pageNumber, int pageSize, Session session) {
        return session.createQuery("FROM Booking b ORDER BY b.id", Booking.class)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public List<Booking> findAllByUserId(Long userId, int pageNumber, int pageSize, Session session) {
        return session.createQuery("FROM Booking b WHERE b.user.id =: userId ORDER BY b.id", Booking.class)
                .setParameter("userId", userId)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public long countBooking(Session session) {
        return session.createQuery("SELECT COUNT(*) FROM Booking b", Long.class)
                .getSingleResult();
    }

    @Override
    public void update(Booking entity, Session session) {
        Transaction tx = null;
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
    public Booking save(Booking entity, Session session) {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            log.info("User {} has booked a session with id#{} and date {}", entity.getUser().getLogin(), entity.getId(), entity.getBookingDate());
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        }
        return entity;
    }

    @Override
    public void delete(Booking entity, Session session) {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
            log.info("Booking id#{} was deletes", entity.getId());
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        }
    }

    public long countBookingByUser(long id, Session session) {
        return session.createQuery("SELECT count(*) FROM Booking b WHERE b.user.id = :userId", Long.class)
                .setParameter("userId", id)
                .getSingleResult();

    }
}
