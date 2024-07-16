package ru.kostrikov.gym_booking.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.kostrikov.gym_booking.dao.UserDao;
import ru.kostrikov.gym_booking.dto.UserDto;
import ru.kostrikov.gym_booking.entity.Role;
import ru.kostrikov.gym_booking.entity.User;
import ru.kostrikov.gym_booking.exception.ValidationException;
import ru.kostrikov.gym_booking.mapper.UserMapper;
import ru.kostrikov.gym_booking.utils.HibernateSessionFactoryProxy;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserService {
    private final UserDao userDao = UserDao.getInstance();
    private static final UserService INSTANCE = new UserService();
    private static final SessionFactory sessionFactory = HibernateSessionFactoryProxy.getSessionFactory();

    public static UserService getInstance() {
        return INSTANCE;
    }

    public long getTotalUsers() {
        @Cleanup Session session = sessionFactory.openSession();
        return userDao.countUsers(session);
    }

    public List<UserDto> findAll(int pageNumber, int pageSize) {
        @Cleanup Session session = sessionFactory.openSession();
        return userDao.findAll(pageNumber, pageSize, session).stream().map(UserMapper.INSTANCE::toDto).toList();
    }

    public Optional<UserDto> findById(long id) {
        @Cleanup Session session = sessionFactory.openSession();
        return userDao.findById(id, session).map(UserMapper.INSTANCE::toDto).or(Optional::empty);
    }

    public Optional<UserDto> login(String login, String password) {
        @Cleanup Session session = sessionFactory.openSession();
        return Optional.ofNullable(UserMapper.INSTANCE.toDto(userDao.findByLoginAndPassword(login, password, session)));
    }

    public UserDto saveOrUpdate(UserDto dto, boolean isNew) throws ValidationException {
        @Cleanup Session session = sessionFactory.openSession();
        if (isNew) {
            return UserMapper.INSTANCE.toDto(userDao.save(UserMapper.INSTANCE.toEntity(dto), session));
        } else {
            userDao.update(UserMapper.INSTANCE.toEntity(dto), session);
            return dto;
        }
    }

    public void setRequestParamsForUserEdit(UserDto userDto, HttpServletRequest req) {
        if (req.getParameter("role") == null) req.setAttribute("role", Role.find(userDto.getRole())); else req.setAttribute("role", Role.find(req.getParameter("role")));
        req.setAttribute("roles", Arrays.stream(Role.values()).filter(role -> isAdmin(userDto) || role != Role.ADMIN).toArray());
        req.setAttribute("user", userDto);
    }

    public Boolean isAdmin(UserDto user) {
        return Optional.of(user).flatMap(dto -> Role.find(dto.getRole())).filter(role -> Role.ADMIN == role).isPresent();
    }

    public Boolean isTrainer(UserDto user) {
        return Optional.of(user).flatMap(dto -> Role.find(dto.getRole())).filter(role -> Role.TRAINER == role).isPresent();
    }

    public Boolean isUser(UserDto user) {
        return Optional.of(user).flatMap(dto -> Role.find(dto.getRole())).filter(role -> Role.USER == role).isPresent();
    }

    public void initialize() throws ValidationException {
        @Cleanup Session session = sessionFactory.openSession();
        userDao.createIfAdministratorNotExists(UserMapper.INSTANCE.toEntity(UserDto.builder().role(Role.ADMIN.name()).login("admin").password("admin").build()), session);
    }
}
