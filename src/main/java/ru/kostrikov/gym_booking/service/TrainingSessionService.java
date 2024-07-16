package ru.kostrikov.gym_booking.service;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.kostrikov.gym_booking.dao.GymDao;
import ru.kostrikov.gym_booking.dao.TrainerDao;
import ru.kostrikov.gym_booking.dao.TrainingSessionDao;
import ru.kostrikov.gym_booking.dto.TrainingSessionDto;
import ru.kostrikov.gym_booking.dto.TrainingSessionWithPresentationDto;
import ru.kostrikov.gym_booking.entity.TrainingSession;
import ru.kostrikov.gym_booking.mapper.TrainingSessionMapper;
import ru.kostrikov.gym_booking.mapper.TrainingWithDescriptionMapper;
import ru.kostrikov.gym_booking.utils.HibernateSessionFactoryProxy;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TrainingSessionService {
    private final TrainingSessionDao trainingDao = TrainingSessionDao.getInstance();
    private static final TrainingSessionService INSTANCE = new TrainingSessionService();
    private final SessionFactory sessionFactory = HibernateSessionFactoryProxy.getSessionFactory();
    private final TrainerDao trainerDao = TrainerDao.getInstance();
    private final GymDao gymDao = GymDao.getInstance();

    public static TrainingSessionService getInstance() {
        return INSTANCE;
    }

    public List<TrainingSessionWithPresentationDto> findAllTrainingWithDescription(int pageNumber, int pageSize) {
        @Cleanup Session session = sessionFactory.openSession();
        return trainingDao.findAll(pageNumber, pageSize, session).stream().map(TrainingWithDescriptionMapper.INSTANCE::toDto).toList();
    }

    public List<TrainingSessionDto> findAll(int pageNumber, int pageSize) {
        @Cleanup Session session = sessionFactory.openSession();
        return trainingDao.findAll(pageNumber, pageSize, session).stream().map(TrainingSessionMapper.INSTANCE::toDto).toList();
    }

    public Long getTotalTraining() {
        @Cleanup Session session = sessionFactory.openSession();
        return trainingDao.countTraining(session);
    }

    public List<TrainingSessionDto> findAllByGym(int pageNumber, int pageSize, long gymNumber) {
        @Cleanup Session session = sessionFactory.openSession();
        return trainingDao.findAllByGym(pageNumber, pageSize, gymNumber, session).stream().map(TrainingSessionMapper.INSTANCE::toDto).toList();
    }

    public long getTotalTrainingByGym(Long gymId) {
        @Cleanup Session session = sessionFactory.openSession();
        return trainingDao.countTrainingByGym(gymId, session);
    }

    public Optional<TrainingSessionDto> findById(Long id) {
        @Cleanup Session session = sessionFactory.openSession();
        return trainingDao.findById(id, session).map(TrainingSessionMapper.INSTANCE::toDto);
    }

    public TrainingSessionDto saveOrUpdate(TrainingSessionDto dto, boolean isNew) {
        @Cleanup Session session = sessionFactory.openSession();
        TrainingSession entity = TrainingSessionMapper.INSTANCE.toEntity(dto);
        gymDao.findById(Long.parseLong(dto.getGym().getId()), session).ifPresent(entity::addGym);
        trainerDao.findById(Long.parseLong(dto.getTrainer().getId()), session).ifPresent(entity::addTrainer);
        if (isNew) {
            dto = TrainingSessionMapper.INSTANCE.toDto(trainingDao.save(entity, session));
        } else {
            trainingDao.findById(entity.getId(), session).ifPresent(foundTraining -> entity.setBookings(entity.getBookings()));
            trainingDao.update(entity, session);
        }
        return dto;
    }
}
