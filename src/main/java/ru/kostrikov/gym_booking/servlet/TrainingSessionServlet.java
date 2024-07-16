package ru.kostrikov.gym_booking.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import ru.kostrikov.gym_booking.dto.GymDto;
import ru.kostrikov.gym_booking.dto.TrainerDto;
import ru.kostrikov.gym_booking.dto.TrainingSessionDto;
import ru.kostrikov.gym_booking.entity.TrainingSession;
import ru.kostrikov.gym_booking.exception.ValidationException;
import ru.kostrikov.gym_booking.service.GymService;
import ru.kostrikov.gym_booking.service.TrainerService;
import ru.kostrikov.gym_booking.service.TrainingSessionService;
import ru.kostrikov.gym_booking.utils.JspHelper;
import ru.kostrikov.gym_booking.utils.ParametersProcessor;
import ru.kostrikov.gym_booking.utils.UrlPath;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static ru.kostrikov.gym_booking.utils.UrlPath.*;

@Log4j2
@WebServlet(urlPatterns = {TRAINING_SESSION, TRAINING_SESSIONS})
public class TrainingSessionServlet extends HttpServlet {

    private final GymService gyms = GymService.getInstance();
    private final TrainerService trainerService = TrainerService.getInstance();
    private final TrainingSessionService trainingService = TrainingSessionService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/html");
        try {
            String referer = JspHelper.getRefererPath(Optional.ofNullable(req.getHeader("Referer")).orElseGet(() -> ""));

            if (!(GYMS.equals(referer) || TRAINING_SESSIONS.equals(referer))) {
                req.getSession().removeAttribute("gymId");
            }

            Long id = Optional.ofNullable(req.getParameter("id")).filter(s -> !s.isEmpty()).map(Long::parseLong).orElseGet(() -> 0L);

            Long gymId = Optional.ofNullable(req.getParameter("gymId")).map(Long::parseLong).or(() -> Optional.ofNullable((Long) req.getSession().getAttribute("gymId"))).orElseGet(() -> 0L);

            String servletPath = req.getServletPath();
            if (TRAINING_SESSION.equals(servletPath)) {

                req.setAttribute("gyms", gyms.findAll());
                req.setAttribute("trainers", trainerService.findAll());
                if (id > 0) {
                    trainingService.findById(id).ifPresent(dto -> req.setAttribute("training", dto));
                }
                req.getRequestDispatcher(JspHelper.getPath("TrainingSession")).forward(req, resp);
            } else if (TRAINING_SESSIONS.equals(servletPath)) {

                int pageNumber = JspHelper.getPageByRequestParameter(req.getParameter("page"), 1);
                req.setAttribute("itemsPerPage", JspHelper.getPageSize());
                req.setAttribute("currentPage", pageNumber);
                if (gymId > 0) {
                    req.setAttribute("items", trainingService.findAllByGym(pageNumber, JspHelper.getPageSize(), gymId));
                    req.setAttribute("totalItems", trainingService.getTotalTrainingByGym(gymId));
                    req.getSession().setAttribute("gymId", gymId);
                } else {

                    req.setAttribute("items", trainingService.findAll(pageNumber, JspHelper.getPageSize()));
                    req.setAttribute("totalItems", trainingService.getTotalTraining());
                }
                req.getRequestDispatcher(JspHelper.getPath("Training")).forward(req, resp);
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

            Optional<TrainingSessionDto> trainingDto = Optional.ofNullable(req.getParameter("id")).filter(s -> !s.isEmpty()).map(Long::parseLong).flatMap(trainingService::findById);

            if (TRAINING_SESSION.equals(servletPath)) {

                Optional<GymDto> gym = Optional.ofNullable(req.getParameter("gym")).filter(s -> !s.isEmpty()).map(Long::parseLong).flatMap(gyms::findById);
                Optional<TrainerDto> trainer = Optional.ofNullable(req.getParameter("trainer")).filter(s -> !s.isEmpty()).map(Long::parseLong).flatMap(trainerService::findById);
                Map<String, Object> map = new HashMap<>();
                gym.ifPresent(dto -> map.put("gym", dto));
                trainer.ifPresent(dto -> map.put("trainer", dto));

                req.setAttribute("gyms", gyms.findAll());
                req.setAttribute("trainers", trainerService.findAll());
                try {
                    ParametersProcessor.process(req, TrainingSessionDto.class, TrainingSessionDto.TrainingSessionDtoBuilder.class, map, dto -> trainingService.saveOrUpdate(dto, trainingDto.isEmpty()))
                            .ifPresent(dto -> req.setAttribute("training", dto));

                } catch (ValidationException e) {
                    ParametersProcessor.process(req, TrainingSessionDto.class, TrainingSessionDto.TrainingSessionDtoBuilder.class, Function.identity())
                            .ifPresent(dto -> req.setAttribute("training", dto));
                    req.setAttribute("errors", e.getErrors());
                    req.getRequestDispatcher(JspHelper.getPath("TrainingSession")).forward(req, resp);
                }
                resp.sendRedirect(UrlPath.buildUrlPathWithContext(req.getContextPath(), TRAINING_SESSIONS));
            }

        } catch (ServletException | IOException e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
