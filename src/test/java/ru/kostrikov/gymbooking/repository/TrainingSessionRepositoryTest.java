package ru.kostrikov.gymbooking.repository;

import lombok.Cleanup;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.kostrikov.gymbooking.entity.Gym;
import ru.kostrikov.gymbooking.entity.PersonalInfo;
import ru.kostrikov.gymbooking.entity.Role;
import ru.kostrikov.gymbooking.entity.TrainingSession;
import ru.kostrikov.gymbooking.util.HibernateTestUtil;
import ru.kostrikov.gymbooking.util.TestDataImporter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static ru.kostrikov.gymbooking.util.TestDataImporter.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class TrainingSessionRepositoryTest {

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
    void verifyFindAllWorkouts() {
        @Cleanup var session = sessionFactory.openSession();
        List<TrainingSession> actualList = Collections.emptyList();
        List<TrainingSession> expectedList = Collections.emptyList();

        PersonalInfo trainer1Info = PersonalInfo.builder().role(Role.TRAINER).firstName("Петр").lastName("Петров").phone("+7(925)987-65-43").email("trainer1@example.com").build();
        PersonalInfo trainer2Info = PersonalInfo.builder().role(Role.TRAINER).firstName("Анна").lastName("Кузнецова").phone("+7(926)777-88-99").email("trainer2@example.com").build();

        var trainer1 = createTrainer("trainer1", "trainer1", "Бодибилдинг", "Опыт работы 5 лет", BigDecimal.valueOf(4.8), true, trainer1Info);
        var trainer2 = createTrainer("trainer2", "trainer2", "Йога", "Опыт работы 7 лет", BigDecimal.valueOf(4.2), true, trainer2Info);

        Gym gymFitnessSport = createGym("Фитнес-клуб \"Спорт\"", "ул. Ленина, 10", "Современный фитнес-центр с бассейном", "55.7558", "37.6173", "+7(495)123-45-67", "www.sportclub.ru");
        Gym gymZone = createGym("GymZone", "пр. Мира, 5", "Тренажерный зал с персональными тренерами", "55.7522", "37.6218", "+7(499)987-65-43", "www.gymzone.ru");
        Gym gymFitLife = createGym("FitLife", "ул. Пушкина, 15", "Фитнес-клуб с групповыми занятиями", "55.7587", "37.6195", "+7(495)555-44-33", "www.fitlife.ru");
        Gym gymCrossFitPower = createGym("CrossFit Power", "пр. Победы, 20", "Кроссфит-зал с опытными тренерами", "55.0000", "37.2143", "+7(499)888-77-66", "www.crossfitpower.ru");

        TrainingSession strengthTrainingForBeginners = createTrainingSession(gymZone, trainer1, 60, BigDecimal.valueOf(1000.00), 4, LocalDate.of(2024, Month.MARCH, 5), LocalTime.of(10, 0, 0), "Силовая тренировка для начинающих", "Силовая");
        TrainingSession yogaForAllLevels = createTrainingSession(gymFitnessSport, trainer2, 15, BigDecimal.valueOf(800.00), 41, LocalDate.of(2024, Month.DECEMBER, 19), LocalTime.of(17, 0, 0), "Йога для всех уровней", "Йога");
        TrainingSession bodybuildingWorkout = createTrainingSession(gymCrossFitPower, trainer1, 15, BigDecimal.valueOf(1500.00), 90, LocalDate.of(2024, Month.JULY, 12), LocalTime.of(18, 30, 0), "Тренировка по бодибилдингу", "Бодибилдинг");
        TrainingSession boxingWorkout = createTrainingSession(gymFitLife, trainer1, 9, BigDecimal.valueOf(3700.00), 30, LocalDate.of(2024, Month.AUGUST, 20), LocalTime.of(19, 0, 0), "Тренировка по боксу", "Бокс");
        expectedList = List.of(strengthTrainingForBeginners, yogaForAllLevels, bodybuildingWorkout, boxingWorkout);

        TrainingSessionRepository repository = new TrainingSessionRepository(session);
        session.beginTransaction();
        actualList = repository.findAll();
        session.getTransaction().commit();
        assertThat(actualList).hasSize(4).containsAll(expectedList);
    }

    @Test
    void verifyFindWorkoutsByIds() {
        @Cleanup var session = sessionFactory.openSession();
        PersonalInfo trainer1Info = PersonalInfo.builder().role(Role.TRAINER).firstName("Петр").lastName("Петров").phone("+7(925)987-65-43").email("trainer1@example.com").build();
        PersonalInfo trainer2Info = PersonalInfo.builder().role(Role.TRAINER).firstName("Анна").lastName("Кузнецова").phone("+7(926)777-88-99").email("trainer2@example.com").build();
        var trainer1 = createTrainer("trainer1", "trainer1", "Бодибилдинг", "Опыт работы 5 лет", BigDecimal.valueOf(4.8), true, trainer1Info);
        var trainer2 = createTrainer("trainer2", "trainer2", "Йога", "Опыт работы 7 лет", BigDecimal.valueOf(4.2), true, trainer2Info);
        List<TrainingSession> workouts = Collections.emptyList();
        List<Long> ids = List.of(1L, 2L, 3L, 4L);
        session.beginTransaction();
        TrainingSessionRepository repository = new TrainingSessionRepository(session);
        workouts = repository.findByIds(ids);
        session.getTransaction().commit();

        assertThat(workouts)
                .hasSize(4)
                .extracting(TrainingSession::getId, TrainingSession::getTrainer, TrainingSession::getStartTime, TrainingSession::getDate)
                .containsExactlyInAnyOrder(
                        tuple(1L, trainer1, LocalTime.of(10, 0, 0), LocalDate.of(2024, Month.MARCH, 5)),
                        tuple(2L, trainer2, LocalTime.of(17, 0, 0), LocalDate.of(2024, Month.DECEMBER, 19)),
                        tuple(3L, trainer1, LocalTime.of(18, 30, 0), LocalDate.of(2024, Month.JULY, 12)),
                        tuple(4L, trainer1, LocalTime.of(19, 0, 0), LocalDate.of(2024, Month.AUGUST, 20))
                );

    }

    @Test
    void verifyCountTraining() {
        @Cleanup Session session = sessionFactory.openSession();
        long amount = 0;
        TrainingSessionRepository repository = new TrainingSessionRepository(session);
        session.beginTransaction();
        amount = repository.countTraining();
        session.getTransaction().commit();
        assertThat(amount).isEqualTo(4);
    }

    @Test
    void verifyFindAllWorkoutsByGym() {
        @Cleanup Session session = sessionFactory.openSession();
        PersonalInfo trainer2Info = PersonalInfo.builder().role(Role.TRAINER).firstName("Анна").lastName("Кузнецова").phone("+7(926)777-88-99").email("trainer2@example.com").build();
        var trainer2 = createTrainer("trainer2", "trainer2", "Йога", "Опыт работы 7 лет", BigDecimal.valueOf(4.2), true, trainer2Info);
        List<TrainingSession> actualWorkouts = Collections.emptyList();
        session.beginTransaction();
        TrainingSessionRepository repository = new TrainingSessionRepository(session);
        actualWorkouts = repository.findAllByGym(1, 1, 1L);
        session.getTransaction().commit();
        assertThat(actualWorkouts)
                .hasSize(1)
                .extracting(TrainingSession::getId, TrainingSession::getTrainer, TrainingSession::getStartTime, TrainingSession::getDate)
                .containsExactlyInAnyOrder(
                        tuple(2L, trainer2, LocalTime.of(17, 0, 0), LocalDate.of(2024, Month.DECEMBER, 19))
                );
    }

    @Test
    void verifyCountTrainingByGym() {
        @Cleanup Session session = sessionFactory.openSession();
        long amount = 0L;
        session.beginTransaction();
        TrainingSessionRepository repository = new TrainingSessionRepository(session);
        amount = repository.countTrainingByGym(1L);
        session.getTransaction().commit();
        assertThat(amount).isEqualTo(1);
    }
}