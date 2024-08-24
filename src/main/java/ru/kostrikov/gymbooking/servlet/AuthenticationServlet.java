package ru.kostrikov.gymbooking.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.kostrikov.gymbooking.dto.UserDto;
import ru.kostrikov.gymbooking.interceptor.TransactionInterceptor;
import ru.kostrikov.gymbooking.repository.UserRepository;
import ru.kostrikov.gymbooking.service.UserService;
import ru.kostrikov.gymbooking.utils.JspHelper;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Optional;

import static ru.kostrikov.gymbooking.utils.UrlPath.*;

@Log4j2
@WebServlet(urlPatterns = {LOGIN, LOGOUT})
public class AuthenticationServlet extends HttpServlet {
    //    private final UserService userService = UserService.getInstance();
    private UserService userService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            SessionFactory sessionFactory = (SessionFactory) config.getServletContext().getAttribute("sessionFactory");
            var session = (Session) Proxy.newProxyInstance(SessionFactory.class.getClassLoader(), new Class[]{Session.class},
                    (proxy, method, args1) -> method.invoke(sessionFactory.getCurrentSession(), args1));

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

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String logout = req.getServletPath();
            if (LOGOUT.equals(logout)) {
                HttpSession session = req.getSession();
                Optional.ofNullable(session.getAttribute("user")).ifPresent(obj -> session.invalidate());
                resp.sendRedirect(Strings.concat(req.getContextPath(), LOGIN));
            } else {
                req.getRequestDispatcher(JspHelper.getPath("Login")).forward(req, resp);
            }
        } catch (ServletException | IOException e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        userService.login(req.getParameter("login"), req.getParameter("password")).ifPresentOrElse(userDto -> onLoginSuccess(userDto, req, resp), () -> {
            onLoginFail(req, resp);
        });
    }


    @SneakyThrows
    private void onLoginFail(HttpServletRequest req, HttpServletResponse resp) {
        log.error("Incorrect login or password");
        req.setAttribute("error", true);
        req.getRequestDispatcher(JspHelper.getPath("Login")).forward(req, resp);
    }

    @SneakyThrows
    private void onLoginSuccess(UserDto userDto, HttpServletRequest req, HttpServletResponse resp) {
        log.info("User {} logged in", userDto.getFullName());
        HttpSession session = req.getSession();
        session.setMaxInactiveInterval(-1);
        session.setAttribute("user", userDto);
        resp.sendRedirect(HOME);
    }
}
