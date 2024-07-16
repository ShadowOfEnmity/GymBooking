package ru.kostrikov.gym_booking.service;

import lombok.Cleanup;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.kostrikov.gym_booking.dao.BookingDao;
import ru.kostrikov.gym_booking.dao.TrainingSessionDao;
import ru.kostrikov.gym_booking.dao.UserDao;
import ru.kostrikov.gym_booking.dto.BookingDto;
import ru.kostrikov.gym_booking.entity.*;
import ru.kostrikov.gym_booking.mapper.BookingMapper;
import ru.kostrikov.gym_booking.utils.HibernateSessionFactoryProxy;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@NoArgsConstructor
public class BookingService {
    private final BookingDao bookingDao = BookingDao.getInstance();
    private final UserDao userDao = UserDao.getInstance();
    private final TrainingSessionDao trainingDao = TrainingSessionDao.getInstance();

    private static final BookingService INSTANCE = new BookingService();

    private final static SessionFactory sessionFactory = HibernateSessionFactoryProxy.getSessionFactory();

    public static BookingService getInstance() {
        return INSTANCE;
    }

    public List<BookingDto> findBookingsByUser(Long id, int pageNumber, int pageSize) {
        @Cleanup Session session = sessionFactory.openSession();
        return bookingDao.findAllByUserId(id, pageNumber, pageSize, session).stream().map(BookingMapper.INSTANCE::toDto).toList();
    }

    public void book(List<Long> trainingIds, Long userId) {
        @Cleanup Session session = sessionFactory.openSession();
        List<TrainingSession> training = trainingDao.findByIds(trainingIds, session);
        userDao.findById(userId, session)
                .map(usr -> Booking.builder().bookingDate(LocalDate.now()).paymentStatus(PaymentStatus.UNPAID).status(Status.PENDING).user(usr).build())
                .stream().peek(booking -> training.forEach(booking::addTraining)).forEach(booking -> bookingDao.save(booking, session));
    }

    public Void update(BookingDto dto) {
        if (!(dto.getId() == null)) {
            @Cleanup Session session = sessionFactory.openSession();
            Booking entity = BookingMapper.INSTANCE.toEntity(dto);
            bookingDao.findById(entity.getId(), session).ifPresent(old -> {
                entity.setUser(old.getUser());
                entity.getTraining().addAll(old.getTraining());
            });
            bookingDao.update(entity, session);
        }
        return null;
    }


    public List<Booking> findAll(int pageNumber, int pageSize) {
        @Cleanup Session session = sessionFactory.openSession();
        return bookingDao.findAll(pageNumber, pageSize, session);
    }

    public long getTotalBookings() {
        @Cleanup Session session = sessionFactory.openSession();
        return bookingDao.countBooking(session);
    }

    public long getTotalBookingsByUser(long id) {
        @Cleanup Session session = sessionFactory.openSession();
        return bookingDao.countBookingByUser(id, session);
    }
}
