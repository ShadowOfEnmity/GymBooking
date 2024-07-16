package ru.kostrikov.gym_booking.service;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.kostrikov.gym_booking.dao.GymDao;
import ru.kostrikov.gym_booking.dto.GymDto;
import ru.kostrikov.gym_booking.entity.Gym;
import ru.kostrikov.gym_booking.mapper.GymMapper;
import ru.kostrikov.gym_booking.utils.HibernateSessionFactoryProxy;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GymService {
    private static final GymService INSTANCE = new GymService();
    private final static SessionFactory sessionFactory = HibernateSessionFactoryProxy.getSessionFactory();
    private final GymDao gymDao = GymDao.getInstance();

    public static GymService getInstance() {
        return INSTANCE;
    }


    public long getTotalGyms() {
        @Cleanup Session session = sessionFactory.openSession();
        return gymDao.countGyms(session);
    }

    public List<GymDto> findAll(int pageNumber, int pageSize) {
        @Cleanup Session session = sessionFactory.openSession();
        return gymDao.findAll(pageNumber, pageSize, session).stream().map(GymMapper.INSTANCE::toDto).toList();
    }

    public List<GymDto> findAll() {
        @Cleanup Session session = sessionFactory.openSession();
        return gymDao.findAll(session).stream().map(GymMapper.INSTANCE::toDto).toList();
    }

    public Optional<GymDto> findById(long id) {
        @Cleanup Session session = sessionFactory.openSession();
        return gymDao.findById(id, session).map(GymMapper.INSTANCE::toDto).or(Optional::empty);
    }

    public GymDto saveOrUpdate(GymDto gym, boolean isNew) {
        @Cleanup Session session = sessionFactory.openSession();
        Gym entity = GymMapper.INSTANCE.toEntity(gym);
        if (isNew) {
            gym = GymMapper.INSTANCE.toDto(gymDao.save(entity, session));
        } else {
            gymDao.update(entity, session);
        }
        return gym;
    }
}
