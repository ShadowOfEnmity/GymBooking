package ru.kostrikov.gymbooking.repository;

import lombok.Cleanup;
import org.assertj.core.api.Condition;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.kostrikov.gymbooking.entity.PersonalInfo;
import ru.kostrikov.gymbooking.entity.Role;
import ru.kostrikov.gymbooking.entity.User;
import ru.kostrikov.gymbooking.util.HibernateTestUtil;
import ru.kostrikov.gymbooking.util.TestDataImporter;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.kostrikov.gymbooking.util.TestDataImporter.createTrainer;
import static ru.kostrikov.gymbooking.util.TestDataImporter.createUser;


@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserRepositoryTest {

    private SessionFactory sessionFactory;

    @BeforeEach
    void setUp() {
        sessionFactory = HibernateTestUtil.buildSessionFactory();
        TestDataImporter.importData(sessionFactory);

    }

    @AfterEach
    void tearDown() {
        if (sessionFactory != null && sessionFactory.isOpen()) {
            sessionFactory.close();
        }
    }


    @Test
    void verifyCountUsers() {
        @Cleanup Session session = sessionFactory.openSession();
        long count = 0L;
        UserRepository repository = new UserRepository(session);
        session.beginTransaction();
        count = repository.countUsers();
        session.getTransaction().commit();
        assertThat(count).isEqualTo(5L);
    }

    @Test
    void verifyNewUserIsSaved() {
        @Cleanup var session = sessionFactory.openSession();
        PersonalInfo personalInfo = PersonalInfo.builder().phone("+7(777)777-77-77").email("testuser@gmail.com").firstName("Ivan").lastName("Popov").role(Role.USER).build();
        User user = User.builder().login("testuser").password("testuser").personalInfo(personalInfo).build();
        session.beginTransaction();

        UserRepository repository = new UserRepository(session);
        var persistedUser = repository.save(user);
        session.refresh(persistedUser);

        session.getTransaction().commit();

        assertThat(persistedUser.getId()).isNotNull();
        assertThat(persistedUser).isEqualTo(user);
    }

    @Test
    void verifyUserIsDeleted() {
        @Cleanup var session = sessionFactory.openSession();
        session.beginTransaction();
        User user = session.get(User.class, 1);

        UserRepository repository = new UserRepository(session);
        repository.delete(user);

        User removedUser = session.get(User.class, 1);

        session.getTransaction().commit();
        assertThat(removedUser).isNull();
    }

    @Test
    void verifyFindAllUsersIsCorrect() {
        @Cleanup var session = sessionFactory.openSession();

        PersonalInfo user1Info = PersonalInfo.builder().role(Role.USER).firstName("Иван").lastName("Иванов").phone("+7(910)123-45-67").email("user1@example.com").build();
        PersonalInfo trainer1Info = PersonalInfo.builder().role(Role.TRAINER).firstName("Петр").lastName("Петров").phone("+7(925)987-65-43").email("trainer1@example.com").build();
        PersonalInfo user2Info = PersonalInfo.builder().role(Role.USER).firstName("Ольга").lastName("Смирнова").phone("+7(911)444-55-66").email("user2@example.com").build();
        PersonalInfo adminInfo = PersonalInfo.builder().role(Role.ADMIN).firstName("admin").lastName("admin").build();
        PersonalInfo trainer2Info = PersonalInfo.builder().role(Role.TRAINER).firstName("Анна").lastName("Кузнецова").phone("++7(926)777-88-99").email("trainer2@example.com").build();

        var user1 = createUser("user1", "user1", user1Info);
        var user2 = createUser("user2", "user2", user2Info);
        var admin = createUser("admin", "admin", adminInfo);

        var trainer1 = (User) createTrainer("trainer1", "trainer1", "Бодибилдинг", "Опыт работы 5 лет", BigDecimal.valueOf(4.8), true, trainer1Info);
        var trainer2 = (User) createTrainer("trainer2", "trainer2", "Йога", "Опыт работы 7 лет", BigDecimal.valueOf(4.2), true, trainer2Info);


        UserRepository repository = new UserRepository(session);

        List<User> actualResult = repository.findAll();

        assertThat(actualResult).hasSize(5).containsExactlyInAnyOrder(user1, user2, admin, trainer1, trainer2);

    }

    @Test
    void verifyFindUserByLoginAndPasswordIsCorrect() {
        @Cleanup Session session = sessionFactory.openSession();
        UserRepository repository = new UserRepository(session);
        User user = repository.findByLoginAndPassword("user1", "user1");

        assertThat(user).isNotNull();
        assertThat(user.getPersonalInfo()).isNotNull();

        Condition<User> userCondition = new Condition<>(u -> "Иван".equals(u.getPersonalInfo().getFirstName()) && "Иванов".equals(u.getPersonalInfo().getLastName()), "username condition");
        assertThat(user).has(userCondition);
    }

    @Test
    void verifyUserWithRoleExist() {
        @Cleanup Session session = sessionFactory.openSession();
        UserRepository repository = new UserRepository(session);
        boolean actual = repository.userWithRoleExist(Role.USER);
        assertThat(actual).isTrue();
    }
}