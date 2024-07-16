import lombok.Cleanup;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.kostrikov.gym_booking.dao.UserDao;
import ru.kostrikov.gym_booking.entity.PersonalInfo;
import ru.kostrikov.gym_booking.entity.Role;
import ru.kostrikov.gym_booking.entity.Trainer;
import ru.kostrikov.gym_booking.entity.User;
import ru.kostrikov.gym_booking.utils.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserDaoTest {

    private final SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
    private final UserDao userDao = UserDao.getInstance();

    @Test
    void findAll() {
        @Cleanup Session session = sessionFactory.openSession();
        Assertions.assertTrue(true);
    }

    @AfterAll
    public void finish() {
        sessionFactory.close();
    }

}
