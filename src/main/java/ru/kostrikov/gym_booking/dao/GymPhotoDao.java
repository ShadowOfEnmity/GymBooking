package ru.kostrikov.gym_booking.dao;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.kostrikov.gym_booking.entity.GymPhoto;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GymPhotoDao implements Dao<Long, GymPhoto> {

    private static final GymPhotoDao INSTANCE = new GymPhotoDao();

    public static GymPhotoDao getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<GymPhoto> findById(Long id, Session session) {
        GymPhoto gymPhoto = null;
        session.beginTransaction();
        gymPhoto = session.get(GymPhoto.class, id);
        session.getTransaction().commit();
        return Optional.ofNullable(gymPhoto);
    }

    @Override
    public List<GymPhoto> findAll(int pageNumber, int pageSize, Session session) {
        return session.createQuery("FROM GymPhoto p ORDER BY p.id", GymPhoto.class)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public List<GymPhoto> findAllByGymId(int pageNumber, int pageSize, long gymId, Session session) {
        return session.createQuery("FROM GymPhoto p WHERE p.gym.id = :gymId ORDER BY p.id", GymPhoto.class)
                .setParameter("gymId", gymId)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public void update(GymPhoto entity, Session session) {
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
    public GymPhoto save(GymPhoto entity, Session session) {
        Transaction tx = null;

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
    public void delete(GymPhoto entity, Session session) {

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

    public long countTrainingByGym(Long gymId, Session session) {
        return session.createQuery("SELECT COUNT(p) FROM GymPhoto p WHERE p.gym.id = :gymId", Long.class)
                .setParameter("gymId", gymId)
                .getSingleResult();
    }

    public long countPhotos(Session session) {
        return session.createQuery("SELECT COUNT(g) FROM GymPhoto g", Long.class)
                .getSingleResult();
    }
}
