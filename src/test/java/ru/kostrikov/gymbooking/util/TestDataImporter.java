package ru.kostrikov.gymbooking.util;

import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.kostrikov.gymbooking.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

@UtilityClass
public class TestDataImporter {

    public void importData(SessionFactory sessionFactory) {
        @Cleanup Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();

            PersonalInfo user1Info = PersonalInfo.builder().role(Role.USER).firstName("Иван").lastName("Иванов").phone("+7(910)123-45-67").email("user1@example.com").build();
            PersonalInfo trainer1Info = PersonalInfo.builder().role(Role.TRAINER).firstName("Петр").lastName("Петров").phone("+7(925)987-65-43").email("trainer1@example.com").build();
            PersonalInfo user2Info = PersonalInfo.builder().role(Role.USER).firstName("Ольга").lastName("Смирнова").phone("+7(911)444-55-66").email("user2@example.com").build();
            PersonalInfo adminInfo = PersonalInfo.builder().role(Role.ADMIN).firstName("admin").lastName("admin").build();
            PersonalInfo trainer2Info = PersonalInfo.builder().role(Role.TRAINER).firstName("Анна").lastName("Кузнецова").phone("+7(926)777-88-99").email("trainer2@example.com").build();

            saveUser(session, createUser("user1", "user1", user1Info));
            saveUser(session, createUser("user2", "user2", user2Info));
            saveUser(session, createUser("admin", "admin", adminInfo));

            var trainer1 = saveTrainer(session, createTrainer("trainer1", "trainer1", "Бодибилдинг", "Опыт работы 5 лет", BigDecimal.valueOf(4.8), true, trainer1Info));
            var trainer2 = saveTrainer(session, createTrainer("trainer2", "trainer2", "Йога", "Опыт работы 7 лет", BigDecimal.valueOf(4.2), true, trainer2Info));

            Gym gymFitnessSport = saveGym(session, createGym("Фитнес-клуб \"Спорт\"", "ул. Ленина, 10", "Современный фитнес-центр с бассейном", "55.7558", "37.6173", "+7(495)123-45-67", "www.sportclub.ru"));
            Gym gymZone = saveGym(session, createGym("GymZone", "пр. Мира, 5", "Тренажерный зал с персональными тренерами", "55.7522", "37.6218", "+7(499)987-65-43", "www.gymzone.ru"));
            Gym gymFitLife = saveGym(session, createGym("FitLife", "ул. Пушкина, 15", "Фитнес-клуб с групповыми занятиями", "55.7587", "37.6195", "+7(495)555-44-33", "www.fitlife.ru"));
            Gym gymCrossFitPower = saveGym(session, createGym("CrossFit Power", "пр. Победы, 20", "Кроссфит-зал с опытными тренерами", "55.0000", "37.2143", "+7(499)888-77-66", "www.crossfitpower.ru"));

            saveGymPhoto(session, createGymPhoto(gymFitnessSport, "https://www.example.com/images/gym1.jpg", "Фото фитнес-центра \"Спорт\""));
            saveGymPhoto(session, createGymPhoto(gymZone, "https://www.example.com/images/gym2.jpg", "Фото тренажерного зала GymZone"));
            saveGymPhoto(session, createGymPhoto(gymFitLife, "https://www.example.com/images/gym3.jpg", "Фото фитнес-клуба FitLife"));
            saveGymPhoto(session, createGymPhoto(gymCrossFitPower, "https://www.example.com/images/gym4.jpg", "Фото кроссфит-зала CrossFit Power"));

            TrainingSession strengthTrainingForBeginners = saveTrainingSession(session, createTrainingSession(gymZone, trainer1, 60, BigDecimal.valueOf(1000.00), 4, LocalDate.of(2024, Month.MARCH, 5), LocalTime.of(10, 0, 0), "Силовая тренировка для начинающих", "Силовая"));
            TrainingSession yogaForAllLevels = saveTrainingSession(session, createTrainingSession(gymFitnessSport, trainer2, 15, BigDecimal.valueOf(800.00), 41, LocalDate.of(2024, Month.DECEMBER, 19), LocalTime.of(17, 0, 0), "Йога для всех уровней", "Йога"));
            TrainingSession bodybuildingWorkout = saveTrainingSession(session, createTrainingSession(gymCrossFitPower, trainer1, 15, BigDecimal.valueOf(1500.00), 90, LocalDate.of(2024, Month.JULY, 12), LocalTime.of(18, 30, 0), "Тренировка по бодибилдингу", "Бодибилдинг"));
            TrainingSession boxingWorkout = saveTrainingSession(session, createTrainingSession(gymFitLife, trainer1, 9, BigDecimal.valueOf(3700.00), 30, LocalDate.of(2024, Month.AUGUST, 20), LocalTime.of(19, 0, 0), "Тренировка по боксу", "Бокс"));

            session.getTransaction().commit();
        } catch (RuntimeException e) {
            if (session.isOpen()) {
                session.getTransaction().rollback();
            }
        }
    }

    public static Gym createGym(String name, String address, String description, String latitude, String longitude, String phone, String website) {
        return Gym.builder().name(name).address(address).description(description).latitude(latitude).longitude(longitude).phone(phone).website(website).build();
    }

    public static Trainer createTrainer(String login, String password, String specialization, String experience, BigDecimal rating, boolean availability, PersonalInfo personalInfo) {
        return Trainer.TrainerBuilder()
                .login(login)
                .password(password)
                .experience(experience)
                .personalInfo(personalInfo)
                .rating(rating)
                .availability(availability).build();
    }

    public static GymPhoto createGymPhoto(Gym gym, String imageUrl, String alt) {
        GymPhoto gymPhoto = GymPhoto.builder().imageUrl(imageUrl).alt(alt).build();
        gymPhoto.setGym(gym);
        return gymPhoto;
    }


    public static User createUser(String login, String password, PersonalInfo personalInfo) {
        return User.builder().login(login).password(password).personalInfo(personalInfo).build();
    }

    public static TrainingSession createTrainingSession(Gym gym, Trainer trainer, int capacity, BigDecimal price, int duration, LocalDate date, LocalTime startTime, String description, String type) {
        TrainingSession trainingSession = TrainingSession.builder().capacity(capacity).price(price).duration(duration).date(date).startTime(startTime).description(description).type(type).build();
        trainingSession.setGym(gym);
        trainingSession.setTrainer(trainer);
        return trainingSession;
    }

    private User saveUser(Session session, User user) {
        session.persist(user);
        return user;
    }

    private Trainer saveTrainer(Session session, Trainer trainer) {
        session.persist(trainer);
        return trainer;
    }

    private Gym saveGym(Session session, Gym gym) {
        session.persist(gym);
        return gym;
    }

    private GymPhoto saveGymPhoto(Session session, GymPhoto gymPhoto) {
        session.persist(gymPhoto);
        return gymPhoto;
    }

    private TrainingSession saveTrainingSession(Session session, TrainingSession trainingSession) {
        session.persist(trainingSession);
        return trainingSession;
    }

}
