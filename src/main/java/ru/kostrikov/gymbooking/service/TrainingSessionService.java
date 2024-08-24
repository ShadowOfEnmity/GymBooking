package ru.kostrikov.gymbooking.service;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import ru.kostrikov.gymbooking.dto.TrainingSessionDto;
import ru.kostrikov.gymbooking.dto.TrainingSessionWithPresentationDto;
import ru.kostrikov.gymbooking.entity.TrainingSession;
import ru.kostrikov.gymbooking.mapper.TrainingSessionMapper;
import ru.kostrikov.gymbooking.mapper.TrainingWithDescriptionMapper;
import ru.kostrikov.gymbooking.repository.GymRepository;
import ru.kostrikov.gymbooking.repository.TrainerRepository;
import ru.kostrikov.gymbooking.repository.TrainingSessionRepository;
import ru.kostrikov.gymbooking.utils.ValidationUtil;

import java.util.List;
import java.util.Optional;
import java.util.Set;

//@NoArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class TrainingSessionService {
    private final TrainingSessionRepository trainingSessionRepository;
    private final GymRepository gymRepository;
    //    private final TrainingSessionDao trainingDao = TrainingSessionDao.getInstance();
//    private static final TrainingSessionService INSTANCE = new TrainingSessionService();
//    private final SessionFactory sessionFactory = HibernateSessionFactoryProxy.getSessionFactory();
    private final TrainerRepository trainerRepository;
//    private final TrainerDao trainerDao = TrainerDao.getInstance();
//    private final GymDao gymDao = GymDao.getInstance();

//    public static TrainingSessionService getInstance() {
//        return INSTANCE;
//    }

    @Transactional
    public List<TrainingSessionWithPresentationDto> findAllTrainingWithDescription(int pageNumber, int pageSize) {
//        @Cleanup Session session = sessionFactory.openSession();
        return trainingSessionRepository.findAll(pageNumber, pageSize).stream().map(TrainingWithDescriptionMapper.INSTANCE::toDto).toList();
    }

    @Transactional
    public List<TrainingSessionDto> findAll(int pageNumber, int pageSize) {
//        @Cleanup Session session = sessionFactory.openSession();
        return trainingSessionRepository.findAll(pageNumber, pageSize).stream().map(TrainingSessionMapper.INSTANCE::toDto).toList();
    }

    @Transactional
    public Long getTotalTraining() {
//        @Cleanup Session session = sessionFactory.openSession();
        return trainingSessionRepository.countTraining();
    }

    @Transactional
    public List<TrainingSessionDto> findAllByGym(int pageNumber, int pageSize, long gymNumber) {
//        @Cleanup Session session = sessionFactory.openSession();
        return trainingSessionRepository.findAllByGym(pageNumber, pageSize, gymNumber).stream().map(TrainingSessionMapper.INSTANCE::toDto).toList();
    }

    @Transactional
    public long getTotalTrainingByGym(Long gymId) {
//        @Cleanup Session session = sessionFactory.openSession();
        return trainingSessionRepository.countTrainingByGym(gymId);
    }

    @Transactional
    public Optional<TrainingSessionDto> findById(Long id) {
//        @Cleanup Session session = sessionFactory.openSession();
        return trainingSessionRepository.findById(id).map(TrainingSessionMapper.INSTANCE::toDto);
    }

    @Transactional
    public TrainingSessionDto saveOrUpdate(TrainingSessionDto dto, boolean isNew) {
        Set<ConstraintViolation<TrainingSessionDto>> violations = ValidationUtil.validate(dto);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

//        @Cleanup Session session = sessionFactory.openSession();

        if (isNew) {
            TrainingSession entity = TrainingSessionMapper.INSTANCE.toEntity(dto);
            gymRepository.findById(Long.parseLong(dto.getGym().getId())).ifPresent(entity::setGym);
            trainerRepository.findById(Long.parseLong(dto.getTrainer().getId())).ifPresent(entity::setTrainer);
            return TrainingSessionMapper.INSTANCE.toDto(trainingSessionRepository.save(entity));
        } else {
            return update(dto);
        }
    }

    @Transactional
    public TrainingSessionDto update(TrainingSessionDto dto) {
        TrainingSession entity = TrainingSessionMapper.INSTANCE.toEntity(dto);
        gymRepository.findById(Long.parseLong(dto.getGym().getId())).ifPresent(entity::setGym);
        trainerRepository.findById(Long.parseLong(dto.getTrainer().getId())).ifPresent(entity::setTrainer);
        Optional<TrainingSession> existingTraining = trainingSessionRepository.findById(entity.getId());
        if (existingTraining.isPresent()) {
            TrainingSession trainingSession = existingTraining.get();
            trainingSession.setId(entity.getId());
            trainingSession.setDate(entity.getDate());
            trainingSession.setCapacity(entity.getCapacity());
            trainingSession.setType(entity.getType());
            trainingSession.setGym(entity.getGym());
            trainingSession.setDuration(entity.getDuration());
            trainingSession.setTrainer(entity.getTrainer());
            return TrainingSessionMapper.INSTANCE.toDto(trainingSessionRepository.update(trainingSession));
        }
        return dto;
    }
}
