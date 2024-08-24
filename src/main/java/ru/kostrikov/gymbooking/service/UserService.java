package ru.kostrikov.gymbooking.service;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.EntityGraph;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.hibernate.graph.GraphSemantic;
import ru.kostrikov.gymbooking.entity.Booking;
import ru.kostrikov.gymbooking.entity.User;
import ru.kostrikov.gymbooking.dto.UserDto;
import ru.kostrikov.gymbooking.entity.Role;
import ru.kostrikov.gymbooking.mapper.UserMapper;
import ru.kostrikov.gymbooking.repository.UserRepository;
import ru.kostrikov.gymbooking.utils.ValidationUtil;

import java.util.*;
import java.util.function.Function;

//@NoArgsConstructor(access = AccessLevel.PRIVATE)

@RequiredArgsConstructor
public class UserService {
//    private final UserDao userDao = UserDao.getInstance();
//    private static final UserService INSTANCE = new UserService();
//    private static final SessionFactory sessionFactory = HibernateSessionFactoryProxy.getSessionFactory();

//    public static UserService getInstance() {
//        return INSTANCE;
//    }

    private final UserRepository userRepository;

    @Transactional
    public long getTotalUsers() {
//        @Cleanup Session session = sessionFactory.openSession();
        return userRepository.countUsers();
    }

    @Transactional
    public List<UserDto> findAll(int pageNumber, int pageSize) {
//        @Cleanup Session session = sessionFactory.openSession();
        return userRepository.findAll(pageNumber, pageSize).stream().map(UserMapper.INSTANCE::toDto).toList();
    }

    @Transactional
    public Optional<UserDto> findById(long id) {
//        @Cleanup Session session = sessionFactory.openSession();
        return userRepository.findById(id).map(UserMapper.INSTANCE::toDto).or(Optional::empty);
    }

    @Transactional
    public Optional<UserDto> login(String login, String password) {
//        @Cleanup Session session = sessionFactory.openSession();
        return Optional.ofNullable(UserMapper.INSTANCE.toDto(userRepository.findByLoginAndPassword(login, password)));
    }

    @Transactional
    public UserDto saveOrUpdate(UserDto dto, boolean isNew) {
        Set<ConstraintViolation<UserDto>> violations = ValidationUtil.validate(dto);

        if (!violations.isEmpty() && Role.find(dto.getRole()).filter(role -> role != Role.ADMIN).isPresent()) {
            throw new ConstraintViolationException(violations);
        }
//        @Cleanup Session session = sessionFactory.openSession();
        if (isNew) {
            return save(dto);
        } else {
            return update(dto);
        }
    }

    @Transactional
    public UserDto save(UserDto user) {
        return UserMapper.INSTANCE.toDto(userRepository.save(UserMapper.INSTANCE.toEntity(user)));
    }

    @Transactional
    public UserDto update(UserDto user) {
        User entity = UserMapper.INSTANCE.toEntity(user);

        EntityGraph<User> graph = userRepository.getEntityManager().createEntityGraph(User.class);
        graph.addAttributeNodes("bookings");
        Map<String, Object> properties = new HashMap<>();
        properties.put(GraphSemantic.LOAD.getJakartaHintName(), graph);

        Optional<User> oldUser = userRepository.findById(entity.getId(), properties);
        List<Booking> bookings = oldUser.map(User::getBookings).map(ArrayList::new)
                .stream()
                .flatMap(Collection::stream)
                .toList();

        bookings.forEach(entity::addBooking);
        if (oldUser.map(usr -> usr.getClass().getAnnotation(DiscriminatorValue.class).value()).filter("TRAINER"::equals).isPresent()) {
            userRepository.delete(oldUser.get());
        }
        return UserMapper.INSTANCE.toDto(userRepository.update(entity));
    }

    @Transactional
    public void delete(UserDto dto) {
        userRepository.delete(UserMapper.INSTANCE.toEntity(dto));
    }

    public void setRequestParamsForUserEdit(UserDto userDto, HttpServletRequest req) {
        if (req.getParameter("role") == null) req.setAttribute("role", Role.find(userDto.getRole()));
        else req.setAttribute("role", Role.find(req.getParameter("role")));
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

    @Transactional
    public void initialize() {
//        @Cleanup Session session = sessionFactory.openSession();
        UserDto dto = UserDto.builder().role(Role.ADMIN.name()).login("Admin").password("Admin").firstName("Admin").lastName("Admin").build();

        if (!userRepository.userWithRoleExist(Role.ADMIN)) {
            saveOrUpdate(dto, true);
        }

    }

}
