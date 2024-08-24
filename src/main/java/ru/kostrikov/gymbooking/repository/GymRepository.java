package ru.kostrikov.gymbooking.repository;

import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import ru.kostrikov.gymbooking.entity.Gym;

import java.util.List;
@Log4j2
public class GymRepository extends RepositoryBase<Long, Gym> {
    public GymRepository(EntityManager entityManager) {
        super(Gym.class, entityManager);
    }

    public long countGyms() {
        return getEntityManager().createQuery("SELECT COUNT(*) FROM Gym g", Long.class)
                .getResultStream().findFirst().orElse(0L);
    }

    public List<Gym> findAll() {
        return getEntityManager().createQuery("FROM Gym g ORDER BY g.name", Gym.class)
                .getResultList();
    }

    @Override
    public Gym save(Gym entity) {
        var gym = super.save(entity);
        log.info("New gym {} is created", entity);
        return gym;
    }

    @Override
    public void delete(Gym gym) {
        super.delete(gym);
        log.info("Gym {} is deleted", gym);
    }
}
