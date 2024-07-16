package ru.kostrikov.gym_booking.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import ru.kostrikov.gym_booking.dto.BookingDto;
import ru.kostrikov.gym_booking.dto.UserDto;
import ru.kostrikov.gym_booking.entity.PaymentStatus;
import ru.kostrikov.gym_booking.entity.Status;
import ru.kostrikov.gym_booking.service.BookingService;
import ru.kostrikov.gym_booking.service.TrainingSessionService;
import ru.kostrikov.gym_booking.service.UserService;
import ru.kostrikov.gym_booking.utils.JspHelper;
import ru.kostrikov.gym_booking.utils.ParametersProcessor;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.kostrikov.gym_booking.utils.UrlPath.BOOKINGS;
import static ru.kostrikov.gym_booking.utils.UrlPath.NEW_BOOKING;

@Log4j2
@WebServlet(urlPatterns = {BOOKINGS, NEW_BOOKING})
public class BookingServlet extends HttpServlet {
    private final BookingService bookingService = BookingService.getInstance();
    private final TrainingSessionService trainingService = TrainingSessionService.getInstance();
    private final UserService userService = UserService.getInstance();

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
                resp.sendRedirect(req.getContextPath() + BOOKINGS);
            } else if (BOOKINGS.equals(servletPath)) {

                ParametersProcessor.process(req, BookingDto.class, BookingDto.BookingDtoBuilder.class, bookingService::update);

                resp.sendRedirect(req.getContextPath() + BOOKINGS);
            }

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
