package ru.kostrikov.gymbooking.repository;

import lombok.Cleanup;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.AuditQueryCreator;
import org.junit.jupiter.api.*;
import ru.kostrikov.gymbooking.entity.*;
import ru.kostrikov.gymbooking.util.HibernateTestUtil;
import ru.kostrikov.gymbooking.util.TestDataImporter;

import java.awt.print.Book;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class BookingRepositoryTest {

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
    void verifyCountBooking() {
        @Cleanup Session session = sessionFactory.openSession();
        BookingRepository repository = new BookingRepository(session);
        session.beginTransaction();
        User user = session.get(User.class, 1L);
        TrainingSession strengthTrainingForBeginners = session.get(TrainingSession.class, 1L);
        Booking newBooking = Booking.builder().status(Status.PENDING).bookingDate(LocalDate.now()).paymentStatus(PaymentStatus.UNPAID).build();
        newBooking.setUser(user);
        newBooking.addTraining(strengthTrainingForBeginners);
        repository.save(newBooking);
        session.getTransaction().commit();

        long amount = repository.countBooking();
        assertThat(amount).isEqualTo(1);

    }

    @Test
    void verifyFindAllByUserId() {
        @Cleanup Session session = sessionFactory.openSession();
        BookingRepository repository = new BookingRepository(session);
        session.beginTransaction();
        User user = session.get(User.class, 1L);
        TrainingSession strengthTrainingForBeginners = session.get(TrainingSession.class, 1L);
        Booking newBooking = Booking.builder().status(Status.PENDING).bookingDate(LocalDate.now()).paymentStatus(PaymentStatus.UNPAID).build();
        newBooking.setUser(user);
        newBooking.addTraining(strengthTrainingForBeginners);
        repository.save(newBooking);
        session.flush();
        session.getTransaction().commit();
        List<Booking> bookingList = repository.findAllByUserId(user.getId(), 1, 1);

        assertThat(bookingList)
                .filteredOn(booking -> booking.getUser().getPersonalInfo().getFirstName().equals("Иван") && booking.getUser().getPersonalInfo().getLastName().equals("Иванов"))
                .hasSize(1);

    }

    @Test
    void verifyCountBookingByUser() {
        @Cleanup Session session = sessionFactory.openSession();
        BookingRepository repository = new BookingRepository(session);
        session.beginTransaction();
        User user = session.get(User.class, 1L);
        TrainingSession strengthTrainingForBeginners = session.get(TrainingSession.class, 1L);
        Booking newBooking = Booking.builder().status(Status.PENDING).bookingDate(LocalDate.now()).paymentStatus(PaymentStatus.UNPAID).build();
        newBooking.setUser(user);
        newBooking.addTraining(strengthTrainingForBeginners);
        repository.save(newBooking);
        session.getTransaction().commit();

        long amountBookingByUser = repository.countBookingByUser(user.getId());
        assertThat(amountBookingByUser).isEqualTo(1);
    }

    @Test
    void verifyNewBookingIsSaved() {
        @Cleanup Session session = sessionFactory.openSession();
        BookingRepository repository = new BookingRepository(session);
        session.beginTransaction();
        User user = session.get(User.class, 1L);
        TrainingSession strengthTrainingForBeginners = session.get(TrainingSession.class, 1L);
        Booking newBooking = Booking.builder().status(Status.PENDING).bookingDate(LocalDate.now()).paymentStatus(PaymentStatus.UNPAID).build();
        newBooking.setUser(user);
        newBooking.addTraining(strengthTrainingForBeginners);
        Booking persistedBooking = repository.save(newBooking);
        session.getTransaction().commit();

        assertThat(persistedBooking.getId()).isNotNull();
        assertThat(persistedBooking.getId()).isEqualTo(1L);
    }

    @Test
    void verifyBookingIsDeleted() {
        @Cleanup var session = sessionFactory.openSession();
        Booking persistedBooking = null;
        BookingRepository repository = new BookingRepository(session);
        session.beginTransaction();
        User user = session.get(User.class, 1L);
        TrainingSession strengthTrainingForBeginners = session.get(TrainingSession.class, 1L);
        Booking newBooking = Booking.builder().status(Status.PENDING).bookingDate(LocalDate.now()).paymentStatus(PaymentStatus.UNPAID).build();
        newBooking.setUser(user);
        newBooking.addTraining(strengthTrainingForBeginners);
        persistedBooking = repository.save(newBooking);
        session.getTransaction().commit();

        assertThat(persistedBooking).isNotNull();
        assertThat(persistedBooking.getId()).isEqualTo(1L);

        session.beginTransaction();
        repository.delete(persistedBooking);
        session.clear();
        persistedBooking = session.get(Booking.class, 1L);
        session.getTransaction().commit();

        assertThat(persistedBooking).isNull();

    }

    @DisplayName("Check that booking audit works correctly")
    @Test
    void verifyAudit() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();
        User user1 = session.get(User.class, 1L);
        TrainingSession strengthTrainingForBeginners = session.get(TrainingSession.class, 1L);
        Booking newBooking = Booking.builder().status(Status.PENDING).bookingDate(LocalDate.now()).paymentStatus(PaymentStatus.UNPAID).build();
        newBooking.setUser(user1);
        newBooking.addTraining(strengthTrainingForBeginners);
        session.persist(newBooking);
        session.getTransaction().commit();

        AuditReader auditReader = AuditReaderFactory.get(session);
        List<Booking> saveList = auditReader.createQuery()
                .forRevisionsOfEntity(Booking.class, Booking.class.getName(), true, true)
                .add(AuditEntity.revisionType().eq(RevisionType.ADD))
                .getResultList();
        assertThat(saveList).hasSize(1);
        assertThat(saveList.get(0).getId()).isNotNull();

        session.beginTransaction();

        newBooking.setStatus(Status.CANCELLED);
        session.merge(newBooking);

        session.getTransaction().commit();

        List<Booking> listOfUpdate = auditReader.createQuery()
                .forRevisionsOfEntity(Booking.class, Booking.class.getName(), true, true)
                .add(AuditEntity.revisionType().eq(RevisionType.MOD))
                .getResultList();
        assertThat(listOfUpdate).hasSize(1);
        assertThat(listOfUpdate.get(0).getStatus()).isEqualTo(Status.CANCELLED);

    }

}