package ru.kostrikov.gymbooking.repository;

import lombok.Cleanup;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.kostrikov.gymbooking.entity.*;
import ru.kostrikov.gymbooking.util.HibernateTestUtil;
import ru.kostrikov.gymbooking.util.TestDataImporter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static ru.kostrikov.gymbooking.util.TestDataImporter.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class TrainerRepositoryTest {

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
    void verifyFindAllTrainersIsCorrect() {
        @Cleanup Session session = sessionFactory.openSession();
        TrainerRepository repository = new TrainerRepository(session);
        PersonalInfo trainer1Info = PersonalInfo.builder().role(Role.TRAINER).firstName("Петр").lastName("Петров").phone("+7(925)987-65-43").email("trainer1@example.com").build();
        PersonalInfo trainer2Info = PersonalInfo.builder().role(Role.TRAINER).firstName("Анна").lastName("Кузнецова").phone("++7(926)777-88-99").email("trainer2@example.com").build();
        var trainer1 = createTrainer("trainer1", "trainer1", "Бодибилдинг", "Опыт работы 5 лет", BigDecimal.valueOf(4.8), true, trainer1Info);
        var trainer2 = createTrainer("trainer2", "trainer2", "Йога", "Опыт работы 7 лет", BigDecimal.valueOf(4.2), true, trainer2Info);
        session.beginTransaction();
        List<Trainer> trainers = repository.findAll();
        session.getTransaction().commit();
        assertThat(trainers).hasSize(2).containsExactlyInAnyOrder(trainer1, trainer2);
    }

    @Test
    void verifyTrainerIsDeleted() {
        sessionFactory.getCache().evictAllRegions();
        @Cleanup var session = sessionFactory.openSession();
        session.beginTransaction();

        TrainerRepository repository = new TrainerRepository(session);
        Trainer trainer1 = session.get(Trainer.class, 4L);
        repository.delete(trainer1);

        Trainer removedTrainer = session.get(Trainer.class, 4L);

        session.getTransaction().commit();
        assertThat(removedTrainer).isNull();

    }

    @Test
    void verifyNewTrainerIsSaved() {
        @Cleanup var session = sessionFactory.openSession();
        session.beginTransaction();
        TrainerRepository repository = new TrainerRepository(session);

        PersonalInfo testTrainerInfo = PersonalInfo.builder().role(Role.TRAINER).firstName("Иван").lastName("Иванов").phone("+7(777)777-77-77").email("test_trainer@example.com").build();

        var trainer1 = createTrainer("test", "test", "Бодибилдинг", "Опыт работы 5 лет", BigDecimal.valueOf(4.8), true, testTrainerInfo);
        Trainer persistedTrainer = repository.save(trainer1);
        session.refresh(persistedTrainer);

        session.getTransaction().commit();
        assertThat(persistedTrainer.getId()).isNotNull();
        assertThat(persistedTrainer).isEqualTo(trainer1);
    }

    @Test
    void verifyFindSessionTrainingsByTrainerId() {
        @Cleanup var session = sessionFactory.openSession();
        PersonalInfo trainer1Info = PersonalInfo.builder().role(Role.TRAINER).firstName("Петр").lastName("Петров").phone("+7(925)987-65-43").email("trainer1@example.com").build();
        var trainer1 = createTrainer("trainer1", "trainer1", "Бодибилдинг", "Опыт работы 5 лет", BigDecimal.valueOf(4.8), true, trainer1Info);

        Gym gymZone = createGym("GymZone", "пр. Мира, 5", "Тренажерный зал с персональными тренерами", "55.7522", "37.6218", "+7(499)987-65-43", "www.gymzone.ru");
        Gym gymFitLife = createGym("FitLife", "ул. Пушкина, 15", "Фитнес-клуб с групповыми занятиями", "55.7587", "37.6195", "+7(495)555-44-33", "www.fitlife.ru");
        Gym gymCrossFitPower = createGym("CrossFit Power", "пр. Победы, 20", "Кроссфит-зал с опытными тренерами", "55.0000", "37.2143", "+7(499)888-77-66", "www.crossfitpower.ru");

        TrainingSession strengthTrainingForBeginners = createTrainingSession(gymZone, trainer1, 60, BigDecimal.valueOf(1000.00), 4, LocalDate.of(2024, Month.MARCH, 5), LocalTime.of(10, 0, 0), "Силовая тренировка для начинающих", "Силовая");
        TrainingSession bodybuildingWorkout = createTrainingSession(gymCrossFitPower, trainer1, 15, BigDecimal.valueOf(1500.00), 90, LocalDate.of(2024, Month.JULY, 12), LocalTime.of(18, 30, 0), "Тренировка по бодибилдингу", "Бодибилдинг");
        TrainingSession boxingWorkout = createTrainingSession(gymFitLife, trainer1, 9, BigDecimal.valueOf(3700.00), 30, LocalDate.of(2024, Month.AUGUST, 20), LocalTime.of(19, 0, 0), "Тренировка по боксу", "Бокс");

        session.beginTransaction();
        TrainerRepository repository = new TrainerRepository(session);
        List<TrainingSession> actualResult = repository.findSessionTrainingsByTrainerId(4L);

        session.getTransaction().commit();
        assertThat(actualResult).hasSize(3).containsExactlyInAnyOrder(strengthTrainingForBeginners, bodybuildingWorkout, boxingWorkout);
    }
}