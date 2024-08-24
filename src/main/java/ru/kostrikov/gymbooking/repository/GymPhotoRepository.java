package ru.kostrikov.gymbooking.repository;

import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import ru.kostrikov.gymbooking.entity.GymPhoto;

import java.util.List;

@Log4j2
public class GymPhotoRepository extends RepositoryBase<Long, GymPhoto> {
    public GymPhotoRepository(EntityManager entityManager) {
        super(GymPhoto.class, entityManager);
    }

    public List<GymPhoto> findAllByGymId(int pageNumber, int pageSize, long gymId) {
        return getEntityManager().createQuery("FROM GymPhoto p WHERE p.gym.id = :gymId ORDER BY p.id", GymPhoto.class)
                .setParameter("gymId", gymId)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public long countPhotosByGym(Long gymId) {
        return getEntityManager().createQuery("SELECT COUNT(p) FROM GymPhoto p WHERE p.gym.id = :gymId", Long.class)
                .setParameter("gymId", gymId)
                .getResultStream().findFirst().orElse(0L);
    }

    public long countPhotos() {
        return getEntityManager().createQuery("SELECT COUNT(g) FROM GymPhoto g", Long.class)
                .getResultStream().findFirst().orElse(0L);
    }

    @Override
    public GymPhoto save(GymPhoto entity) {
        var persistedGymPhoto = super.save(entity);
        log.info("New {} is created", entity);
        return persistedGymPhoto;
    }

    @Override
    public void delete(GymPhoto entity) {
        super.delete(entity);
        log.info("GymPhoto {} is deleted", entity);
    }
}
