package ru.kostrikov.gymbooking.repository;

import lombok.Cleanup;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.kostrikov.gymbooking.entity.Gym;
import ru.kostrikov.gymbooking.entity.GymPhoto;
import ru.kostrikov.gymbooking.util.HibernateTestUtil;
import ru.kostrikov.gymbooking.util.TestDataImporter;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.kostrikov.gymbooking.util.TestDataImporter.createGymPhoto;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class GymPhotoRepositoryTest {
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
    void verifyFindAllPhotosByGymId() {
        @Cleanup Session session = sessionFactory.openSession();
        List<GymPhoto> actualList = Collections.emptyList();
        session.beginTransaction();
        GymPhotoRepository repository = new GymPhotoRepository(session);
        actualList = repository.findAllByGymId(1, 1, 2L);
        session.getTransaction().commit();

        assertThat(actualList).hasSize(1)
                .extracting(GymPhoto::getImageUrl)
                .containsExactlyInAnyOrder("https://www.example.com/images/gym2.jpg");
    }

    @Test
    void verifyCountPhotosByGym() {
        @Cleanup Session session = sessionFactory.openSession();
        long amount = 0;
        session.beginTransaction();
        GymPhotoRepository repository = new GymPhotoRepository(session);
        amount = repository.countPhotosByGym(2L);
        session.getTransaction().commit();

        assertThat(amount).isEqualTo(1L);
    }

    @Test
    void verifyCountPhotos() {
        @Cleanup Session session = sessionFactory.openSession();
        long amount = 0;
        session.beginTransaction();
        GymPhotoRepository repository = new GymPhotoRepository(session);
        amount = repository.countPhotos();
        session.getTransaction().commit();
        assertThat(amount).isEqualTo(4L);
    }

    @Test
    void verifyNewPhotoIsSaved() {
        @Cleanup var session = sessionFactory.openSession();
        GymPhoto persistedPhoto = null;
        session.beginTransaction();
        GymPhotoRepository repository = new GymPhotoRepository(session);

        Gym gymFitnessSport = session.get(Gym.class, 1L);

        GymPhoto gymPhoto = createGymPhoto(gymFitnessSport, "https://www.example.com/images/test.jpg", "Фото test фитнес-центра \"Спорт\"");
        persistedPhoto = repository.save(gymPhoto);

        session.getTransaction().commit();
        assertThat(persistedPhoto).isNotNull();
        assertThat(persistedPhoto).extracting(GymPhoto::getImageUrl).isEqualTo("https://www.example.com/images/test.jpg");
    }

    @Test
    void verifyPhotoIsDeleted() {
        @Cleanup var session = sessionFactory.openSession();
        GymPhoto removedPhoto = null;
        session.beginTransaction();

        GymPhotoRepository repository = new GymPhotoRepository(session);
        GymPhoto gymPhoto = session.get(GymPhoto.class, 1L);
        repository.delete(gymPhoto);
        session.flush();

        removedPhoto = session.get(GymPhoto.class, 1L);
        session.getTransaction().commit();

        assertThat(removedPhoto).isNull();
    }
}