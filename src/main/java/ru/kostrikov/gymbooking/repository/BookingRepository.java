package ru.kostrikov.gymbooking.repository;

import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import ru.kostrikov.gymbooking.entity.Booking;

import java.util.List;

@Log4j2
public class BookingRepository extends RepositoryBase<Long, Booking> {
    public BookingRepository(EntityManager entityManager) {
        super(Booking.class, entityManager);
    }

    public long countBooking() {
        return getEntityManager().createQuery("SELECT COUNT(*) FROM Booking b", Long.class).getSingleResult();
    }

    public List<Booking> findAllByUserId(Long userId, int pageNumber, int pageSize) {
        return getEntityManager().createQuery("FROM Booking b WHERE b.user.id =: userId ORDER BY b.id", Booking.class)
                .setParameter("userId", userId)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public long countBookingByUser(long id) {
        return getEntityManager().createQuery("SELECT count(*) FROM Booking b WHERE b.user.id = :userId", Long.class)
                .setParameter("userId", id)
                .getSingleResult();
    }

    @Override
    public Booking save(Booking entity) {
        var persistedEntity = super.save(entity);
        log.info("User {} has booked a session with id#{} and date {}", persistedEntity.getUser().getLogin(), persistedEntity.getId(), persistedEntity.getBookingDate());
        return persistedEntity;
    }

    @Override
    public void delete(Booking entity) {
        super.delete(entity);
        log.info("Booking {} was deleted", entity);
    }

}
