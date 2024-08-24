package ru.kostrikov.gymbooking.servlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.kostrikov.gymbooking.dto.BookingDto;
import ru.kostrikov.gymbooking.dto.UserDto;
import ru.kostrikov.gymbooking.entity.PaymentStatus;
import ru.kostrikov.gymbooking.entity.Status;
import ru.kostrikov.gymbooking.interceptor.TransactionInterceptor;
import ru.kostrikov.gymbooking.repository.*;
import ru.kostrikov.gymbooking.service.BookingService;
import ru.kostrikov.gymbooking.service.PhotoService;
import ru.kostrikov.gymbooking.service.TrainingSessionService;
import ru.kostrikov.gymbooking.service.UserService;
import ru.kostrikov.gymbooking.utils.JspHelper;
import ru.kostrikov.gymbooking.utils.ParametersProcessor;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

import static ru.kostrikov.gymbooking.utils.UrlPath.BOOKINGS;
import static ru.kostrikov.gymbooking.utils.UrlPath.NEW_BOOKING;

@Log4j2
@WebServlet(urlPatterns = {BOOKINGS, NEW_BOOKING})
public class BookingServlet extends HttpServlet {
//    private final BookingService bookingService = BookingService.getInstance();
//    private final TrainingSessionService trainingService = TrainingSessionService.getInstance();
//    private final UserService userService = UserService.getInstance();

    private BookingService bookingService;
    private TrainingSessionService trainingService;
    private UserService userService;


    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            SessionFactory sessionFactory = (SessionFactory) config.getServletContext().getAttribute("sessionFactory");
            var session = (Session) Proxy.newProxyInstance(SessionFactory.class.getClassLoader(), new Class[]{Session.class},
                    (proxy, method, args) -> method.invoke(sessionFactory.getCurrentSession(), args));

            var transactionInterceptor = new TransactionInterceptor(sessionFactory);

            userService = new ByteBuddy()
                    .subclass(UserService.class)
                    .method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(transactionInterceptor))
                    .make()
                    .load(UserService.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor(UserRepository.class)
                    .newInstance(new UserRepository(session));


            trainingService = new ByteBuddy()
                    .subclass(TrainingSessionService.class)
                    .method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(transactionInterceptor))
                    .make()
                    .load(TrainingSessionService.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor(TrainingSessionRepository.class, GymRepository.class, TrainerRepository.class)
                    .newInstance(new TrainingSessionRepository(session), new GymRepository(session), new TrainerRepository(session));


            bookingService = new ByteBuddy()
                    .subclass(BookingService.class)
                    .method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(transactionInterceptor))
                    .make()
                    .load(BookingService.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor(BookingRepository.class, UserRepository.class, TrainingSessionRepository.class)
                    .newInstance(new BookingRepository(session), new UserRepository(session), new TrainingSessionRepository(session));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/html");
        try {
            String servletPath = req.getServletPath();
            String referer = JspHelper.getRefererPath(Optional.ofNullable(req.getHeader("Referer")).orElseGet(() -> ""));
            var userDto = (UserDto) req.getSession().getAttribute("user");
            Long id = Optional.ofNullable(req.getParameter("userId"))
                    .or(() -> Optional.of(userDto.getId()))
                    .map(Long::valueOf).orElse(0L);

            if (!BOOKINGS.equals(referer)) {
                req.getSession().removeAttribute("userId");
            }
            int pageNumber = JspHelper.getPageByRequestParameter(req.getParameter("page"), 1);
            if (NEW_BOOKING.equals(servletPath)) {

                req.setAttribute("userId", id);
                Map<Long, Boolean> selected = Optional.ofNullable(req.getParameter("selected")).filter(s -> !s.isEmpty()).map(s -> s.split(",")).stream().flatMap(Arrays::stream).collect(Collectors.toMap(Long::valueOf, s -> true, ((oldValue, newValue) -> newValue), HashMap::new));
                if (!selected.isEmpty()) {
                    Map<Long, Boolean> selectedResponse = Optional.ofNullable(req.getParameter("checkboxState")).stream().collect(Collectors.toMap(Long::valueOf, s -> true, ((oldValue, newValue) -> newValue), HashMap::new));
                    selectedResponse.putAll(selected);
                    ObjectMapper objectMapper = new ObjectMapper();
                    req.setAttribute("checkboxState", objectMapper.writeValueAsString(selected));
                    req.setAttribute("checkboxStateMap", selectedResponse);

                }

                req.setAttribute("itemsPerPage", JspHelper.getPageSize());
                req.setAttribute("totalItems", trainingService.getTotalTraining());
                req.setAttribute("currentPage", pageNumber);
                req.setAttribute("sessions", trainingService.findAllTrainingWithDescription(pageNumber, JspHelper.getPageSize()));
                req.getRequestDispatcher(JspHelper.getPath("NewBooking")).forward(req, resp);
            } else {
                req.setAttribute("itemsPerPage", JspHelper.getPageSize());
                req.setAttribute("currentPage", pageNumber);

                if (BOOKINGS.equals(servletPath) && id > 0 && !userService.isAdmin(userDto)) {

                    req.setAttribute("items", bookingService.findBookingsByUser(id, pageNumber, JspHelper.getPageSize()));
                    req.setAttribute("totalItems", bookingService.getTotalBookingsByUser(id));
                    req.getSession().setAttribute("userId", id);
                } else {
                    req.setAttribute("items", bookingService.findAll(pageNumber, JspHelper.getPageSize()));
                    req.setAttribute("totalItems", bookingService.getTotalBookings());
                }

                req.setAttribute("statuses", Arrays.asList(Status.values()));
                req.setAttribute("paymentsStatuses", Arrays.asList(PaymentStatus.values()));

                req.getRequestDispatcher(JspHelper.getPath("UserBookings")).forward(req, resp);
            }
        } catch (ServletException | IOException e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/html");
        try {
            String servletPath = req.getServletPath();
            if (NEW_BOOKING.equals(servletPath)) {
                Long userId = Long.valueOf(req.getParameter("userId"));
                ObjectMapper objectMapper = new ObjectMapper();
                Map<Long, Boolean> selectedTraining = objectMapper.readValue(req.getParameter("checkboxState"), new TypeReference<HashMap<Long, Boolean>>() {
                });
                List<Long> list = selectedTraining.keySet().stream().toList();
                bookingService.book(list, userId);
                resp.sendRedirect(Strings.concat(req.getContextPath(), BOOKINGS));
            } else if (BOOKINGS.equals(servletPath)) {
                ParametersProcessor.process(req, BookingDto.class, BookingDto.BookingDtoBuilder.class, bookingService::update);
                resp.sendRedirect(Strings.concat(req.getContextPath(), BOOKINGS));
            }

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
