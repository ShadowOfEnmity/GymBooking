package ru.kostrikov.gym_booking.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import ru.kostrikov.gym_booking.dto.GymDto;
import ru.kostrikov.gym_booking.exception.ValidationException;
import ru.kostrikov.gym_booking.service.GymService;
import ru.kostrikov.gym_booking.utils.JspHelper;
import ru.kostrikov.gym_booking.utils.ParametersProcessor;
import ru.kostrikov.gym_booking.utils.UrlPath;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

import static ru.kostrikov.gym_booking.utils.UrlPath.GYM;
import static ru.kostrikov.gym_booking.utils.UrlPath.GYMS;

@Log4j2
@WebServlet(urlPatterns = {GYMS, GYM})
public class GymServlet extends HttpServlet {

    private final GymService gymService = GymService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/html");
        try {
            Long id = Optional.ofNullable(req.getParameter("id")).filter(s -> !s.isEmpty()).map(Long::parseLong).orElseGet(() -> 0L);
            Optional<GymDto> gym = Optional.ofNullable((GymDto) req.getAttribute("gym")).or(() -> gymService.findById(id));
            String servletPath = req.getServletPath();
            if (GYMS.equals(servletPath)) {
                int pageNumber = JspHelper.getPageByRequestParameter(req.getParameter("page"), 1);
                req.setAttribute("items", gymService.findAll(pageNumber, JspHelper.getPageSize()));
                req.setAttribute("itemsPerPage", JspHelper.getPageSize());
                req.setAttribute("totalItems", gymService.getTotalGyms());
                req.setAttribute("currentPage", pageNumber);
                req.getRequestDispatcher(JspHelper.getPath("Gyms")).forward(req, resp);
            } else {
                gym.ifPresent(dto -> req.setAttribute("gym", dto));
                req.getRequestDispatcher(JspHelper.getPath("Gym")).forward(req, resp);
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
            Optional<GymDto> foundGym = Optional.ofNullable(req.getParameter("id")).filter(s -> !s.isEmpty()).map(Long::parseLong).flatMap(gymService::findById);
            ParametersProcessor.process(req, GymDto.class, GymDto.GymDtoBuilder.class, Function.identity()).ifPresent(dto -> req.setAttribute("gym", dto));
            try {
                ParametersProcessor.process(req, GymDto.class, GymDto.GymDtoBuilder.class, dto -> gymService.saveOrUpdate(dto, foundGym.isEmpty()));
                resp.sendRedirect(UrlPath.buildUrlPathWithContext(req.getContextPath(), GYMS));
            } catch (ValidationException e) {
                req.setAttribute("errors", e.getErrors());
                req.getRequestDispatcher(JspHelper.getPath("Gym")).forward(req, resp);
            }
        } catch (ServletException | IOException e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
