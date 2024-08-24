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
import ru.kostrikov.gymbooking.interceptor.TransactionInterceptor;
import ru.kostrikov.gymbooking.repository.*;
import ru.kostrikov.gymbooking.service.*;
import ru.kostrikov.gymbooking.utils.*;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.function.Function;

import static ru.kostrikov.gymbooking.utils.UrlPath.GYM;
import static ru.kostrikov.gymbooking.utils.UrlPath.GYMS;

@Log4j2
@WebServlet(urlPatterns = {GYMS, GYM})
public class GymServlet extends HttpServlet {

    //    private final GymService gymService = GymService.getInstance();
    private GymService gymService;


    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            SessionFactory sessionFactory = (SessionFactory) config.getServletContext().getAttribute("sessionFactory");
            var session = (Session) Proxy.newProxyInstance(SessionFactory.class.getClassLoader(), new Class[]{Session.class},
                    (proxy, method, args) -> method.invoke(sessionFactory.getCurrentSession(), args));

            var transactionInterceptor = new TransactionInterceptor(sessionFactory);

            gymService = new ByteBuddy()
                    .subclass(GymService.class)
                    .method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(transactionInterceptor))
                    .make()
                    .load(GymService.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor(GymRepository.class)
                    .newInstance(new GymRepository(session));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


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
            } catch (ConstraintViolationException e) {
                req.setAttribute("errors", ValidationUtil.getMapOfViolationsToDisplay(e.getConstraintViolations()));
                req.getRequestDispatcher(JspHelper.getPath("Gym")).forward(req, resp);
            }
        } catch (ServletException | IOException e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
