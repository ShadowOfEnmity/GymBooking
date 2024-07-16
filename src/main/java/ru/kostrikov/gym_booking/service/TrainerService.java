package ru.kostrikov.gym_booking.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.NoArgsConstructor;
import org.eclipse.tags.shaded.org.apache.bcel.generic.INSTANCEOF;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.kostrikov.gym_booking.dao.TrainerDao;
import ru.kostrikov.gym_booking.dto.TrainerDto;
import ru.kostrikov.gym_booking.dto.UserDto;
import ru.kostrikov.gym_booking.entity.Role;
import ru.kostrikov.gym_booking.entity.Trainer;
import ru.kostrikov.gym_booking.mapper.TrainerMapper;
import ru.kostrikov.gym_booking.mapper.UserMapper;
import ru.kostrikov.gym_booking.utils.HibernateSessionFactoryProxy;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TrainerService {

    private static final TrainerService INSTANCE = new TrainerService();
    private final SessionFactory sessionFactory = HibernateSessionFactoryProxy.getSessionFactory();
    private final TrainerDao trainerDao = TrainerDao.getInstance();
    private final UserService userService = UserService.getInstance();

    public static TrainerService getInstance() {
        return INSTANCE;
    }

    public List<TrainerDto> findAll() {
        @Cleanup Session session = sessionFactory.openSession();
        return trainerDao.findAll(session).stream().map(TrainerMapper.INSTANCE::toDto).toList();
    }

    public TrainerDto saveOrUpdate(TrainerDto dto, boolean isNew) {
        @Cleanup Session session = sessionFactory.openSession();
        if (isNew) {
            return TrainerMapper.INSTANCE.toDto(trainerDao.save(TrainerMapper.INSTANCE.toEntity(dto), session));
        } else {
            trainerDao.update(TrainerMapper.INSTANCE.toEntity(dto), session);
            return dto;
        }
    }

    public Optional<TrainerDto> findById(Long id) {
        @Cleanup Session session = sessionFactory.openSession();
        return trainerDao.findById(id, session).map(TrainerMapper.INSTANCE::toDto);
    }

    public void setRequestParamsForTrainerEdit(TrainerDto trainerDto, HttpServletRequest req) {
        if (req.getParameter("role") == null) req.setAttribute("role", Role.find(trainerDto.getUser().getRole())); else req.setAttribute("role", Role.find(req.getParameter("role")));
        req.setAttribute("roles", Arrays.stream(Role.values()).filter(role -> userService.isAdmin(trainerDto.getUser()) || role != Role.ADMIN).toArray());
        req.setAttribute("user", trainerDto.getUser());
        req.setAttribute("trainer", trainerDto);
    }
}
