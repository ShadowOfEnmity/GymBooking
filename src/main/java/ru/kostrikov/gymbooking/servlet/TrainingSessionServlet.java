package ru.kostrikov.gymbooking.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.kostrikov.gymbooking.dto.GymDto;
import ru.kostrikov.gymbooking.dto.TrainerDto;
import ru.kostrikov.gymbooking.dto.TrainingSessionDto;
import ru.kostrikov.gymbooking.entity.Trainer;
import ru.kostrikov.gymbooking.interceptor.TransactionInterceptor;
import ru.kostrikov.gymbooking.repository.*;
import ru.kostrikov.gymbooking.service.*;
import ru.kostrikov.gymbooking.utils.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static ru.kostrikov.gymbooking.utils.UrlPath.*;

@Log4j2
@WebServlet(urlPatterns = {TRAINING_SESSION, TRAINING_SESSIONS})
public class TrainingSessionServlet extends HttpServlet {

    private GymService gyms;
    private TrainerService trainerService;
    private TrainingSessionService trainingService;


    @Override
    public void init(ServletConfig config) throws ServletException {

        try {
            SessionFactory sessionFactory = (SessionFactory) config.getServletContext().getAttribute("sessionFactory");
            var session = (Session) Proxy.newProxyInstance(SessionFactory.class.getClassLoader(), new Class[]{Session.class},
                    (proxy, method, args) -> method.invoke(sessionFactory.getCurrentSession(), args));

            var transactionInterceptor = new TransactionInterceptor(sessionFactory);

            gyms = new ByteBuddy()
                    .subclass(GymService.class)
                    .method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(transactionInterceptor))
                    .make()
                    .load(GymService.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor(GymRepository.class)
                    .newInstance(new GymRepository(session));

            var userService = new ByteBuddy()
                    .subclass(UserService.class)
                    .method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(transactionInterceptor))
                    .make()
                    .load(UserService.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor(UserRepository.class)
                    .newInstance(new UserRepository(session));

            trainerService = new ByteBuddy()
                    .subclass(TrainerService.class)
                    .method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(transactionInterceptor))
                    .make()
                    .load(TrainerService.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor(TrainerRepository.class, UserRepository.class, UserService.class)
                    .newInstance(new TrainerRepository(session), new UserRepository(session), userService);

            trainingService = new ByteBuddy()
                    .subclass(TrainingSessionService.class)
                    .method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(transactionInterceptor))
                    .make()
                    .load(TrainingSessionService.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor(TrainingSessionRepository.class, GymRepository.class, TrainerRepository.class)
                    .newInstance(new TrainingSessionRepository(session), new GymRepository(session), new TrainerRepository(session));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

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

                } catch (ConstraintViolationException e) {
                    ParametersProcessor.process(req, TrainingSessionDto.class, TrainingSessionDto.TrainingSessionDtoBuilder.class, Function.identity())
                            .ifPresent(dto -> req.setAttribute("training", dto));
                    req.setAttribute("errors", ValidationUtil.getMapOfViolationsToDisplay(e.getConstraintViolations()));
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
