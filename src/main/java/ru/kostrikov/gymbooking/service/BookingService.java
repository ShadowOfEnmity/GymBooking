package ru.kostrikov.gymbooking.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.kostrikov.gymbooking.dto.BookingDto;
import ru.kostrikov.gymbooking.entity.Booking;
import ru.kostrikov.gymbooking.entity.PaymentStatus;
import ru.kostrikov.gymbooking.entity.Status;
import ru.kostrikov.gymbooking.entity.TrainingSession;
import ru.kostrikov.gymbooking.mapper.BookingMapper;
import ru.kostrikov.gymbooking.repository.BookingRepository;
import ru.kostrikov.gymbooking.repository.TrainingSessionRepository;
import ru.kostrikov.gymbooking.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

//@NoArgsConstructor
@RequiredArgsConstructor()
public class BookingService {
//    private final BookingDao bookingDao = BookingDao.getInstance();
//    private final UserDao userDao = UserDao.getInstance();
//    private final TrainingSessionDao trainingDao = TrainingSessionDao.getInstance();

//    private static final BookingService INSTANCE = new BookingService();

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TrainingSessionRepository trainingSessionRepository;

//    private final static SessionFactory sessionFactory = HibernateSessionFactoryProxy.getSessionFactory();

//    public static BookingService getInstance() {
//        return INSTANCE;
//    }

    @Transactional
    public List<BookingDto> findBookingsByUser(Long id, int pageNumber, int pageSize) {
//        @Cleanup Session session = sessionFactory.openSession();
        return bookingRepository.findAllByUserId(id, pageNumber, pageSize).stream().map(BookingMapper.INSTANCE::toDto).toList();
    }

    @Transactional
    public void book(List<Long> trainingIds, Long userId) {
//        @Cleanup Session session = sessionFactory.openSession();
        List<TrainingSession> trainingList = trainingSessionRepository.findByIds(trainingIds);

        userRepository.findById(userId).map(usr -> {
            Booking booking = Booking.builder()
                    .bookingDate(LocalDate.now())
                    .paymentStatus(PaymentStatus.UNPAID)
                    .status(Status.PENDING)
                    .build();
            booking.setUser(usr);
            trainingList.forEach(booking::addTraining);
            return booking;
        }).ifPresent(bookingRepository::save);
    }


    @Transactional
    public Void update(BookingDto dto) {
        if (!(dto.getId() == null)) {
//            @Cleanup Session session = sessionFactory.openSession();
            Booking entity = BookingMapper.INSTANCE.toEntity(dto);
            var existingEntity = bookingRepository.findById(entity.getId()).orElseThrow(() -> new EntityNotFoundException("Booking #id%d not found".formatted(entity.getId())));
            existingEntity.setId(entity.getId());
            existingEntity.setStatus(entity.getStatus());
            existingEntity.setBookingDate(entity.getBookingDate());
            bookingRepository.update(existingEntity);
        }
        return null;
    }


    @Transactional
    public List<Booking> findAll(int pageNumber, int pageSize) {
//        @Cleanup Session session = sessionFactory.openSession();
        return bookingRepository.findAll(pageNumber, pageSize);
    }

    @Transactional
    public long getTotalBookings() {
//        @Cleanup Session session = sessionFactory.openSession();
        return bookingRepository.countBooking();
    }

    @Transactional
    public long getTotalBookingsByUser(long id) {
//        @Cleanup Session session = sessionFactory.openSession();
        return bookingRepository.countBookingByUser(id);
    }
}
