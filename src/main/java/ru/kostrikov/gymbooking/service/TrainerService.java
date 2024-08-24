package ru.kostrikov.gymbooking.service;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.EntityGraph;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.graph.GraphSemantic;
import ru.kostrikov.gymbooking.dto.TrainerDto;
import ru.kostrikov.gymbooking.entity.*;
import ru.kostrikov.gymbooking.mapper.TrainerMapper;
import ru.kostrikov.gymbooking.repository.TrainerRepository;
import ru.kostrikov.gymbooking.repository.UserRepository;
import ru.kostrikov.gymbooking.utils.ValidationUtil;

import java.util.*;

//@NoArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class TrainerService {

//    private static final TrainerService INSTANCE = new TrainerService();
//    private final SessionFactory sessionFactory = HibernateSessionFactoryProxy.getSessionFactory();
//    private final TrainerDao trainerDao = TrainerDao.getInstance();
//    private final UserService userService = UserService.getInstance();

    private final TrainerRepository trainerRepository;
    //    private final UserService userService;
    private final UserRepository userRepository;
    private final UserService userService;

//    public static TrainerService getInstance() {
//        return INSTANCE;
//    }

    @Transactional
    public List<TrainerDto> findAll() {
//        @Cleanup Session session = sessionFactory.openSession();
        return trainerRepository.findAll().stream().map(TrainerMapper.INSTANCE::toDto).toList();
    }

    @Transactional
    public TrainerDto saveOrUpdate(TrainerDto dto, boolean isNew) {
        Set<ConstraintViolation<TrainerDto>> trainerViolations = ValidationUtil.validate(dto);

        if (!trainerViolations.isEmpty()) {
            throw new ConstraintViolationException(trainerViolations);
        }

//        @Cleanup Session session = sessionFactory.openSession();
        if (isNew) {
            return TrainerMapper.INSTANCE.toDto(trainerRepository.save(TrainerMapper.INSTANCE.toEntity(dto)));
        } else {
            return update(dto);
        }
    }

    @Transactional
    public TrainerDto update(TrainerDto dto) {
        Trainer entity = TrainerMapper.INSTANCE.toEntity(dto);

        EntityGraph<User> graph = userRepository.getEntityManager().createEntityGraph(User.class);
        graph.addAttributeNodes("bookings");
        Map<String, Object> properties = new HashMap<>();
        properties.put(GraphSemantic.LOAD.getJakartaHintName(), graph);

        Optional<User> oldUser = userRepository.findById(entity.getId(), properties);

        if (oldUser.isPresent()) {

            List<Booking> bookings = oldUser.map(User::getBookings).map(ArrayList::new)
                    .stream()
                    .flatMap(Collection::stream)
                    .toList();

            User user = oldUser.get();
            if ("TRAINER".equals(user.getClass().getAnnotation(DiscriminatorValue.class).value())) {
                Trainer trainer = (Trainer) user;
                List<TrainingSession> trainings = trainerRepository.findSessionTrainingsByTrainerId(trainer.getId());
                bookings.forEach(booking -> booking.setUser(null));
                trainings.forEach(trainingSession -> trainingSession.setTrainer(null));
                trainerRepository.delete(trainer);
                bookings.forEach(entity::addBooking);
                trainings.forEach(entity::addTraining);
            } else {
                bookings.forEach(booking -> booking.setUser(null));
                userRepository.delete(user);
                bookings.forEach(entity::addBooking);
            }

            return TrainerMapper.INSTANCE.toDto(trainerRepository.update(entity));
        }
        return dto;
    }


    @Transactional
    public Optional<TrainerDto> findById(Long id) {
//        @Cleanup Session session = sessionFactory.openSession();
        return trainerRepository.findById(id).map(TrainerMapper.INSTANCE::toDto);
    }

    public void setRequestParamsForTrainerEdit(TrainerDto trainerDto, HttpServletRequest req) {
        if (req.getParameter("role") == null) req.setAttribute("role", Role.find(trainerDto.getUser().getRole()));
        else req.setAttribute("role", Role.find(req.getParameter("role")));
        req.setAttribute("roles", Arrays.stream(Role.values()).filter(role -> userService.isAdmin(trainerDto.getUser()) || role != Role.ADMIN).toArray());
        req.setAttribute("user", trainerDto.getUser());
        req.setAttribute("trainer", trainerDto);
    }
}
