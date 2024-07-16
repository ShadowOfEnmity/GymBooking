package ru.kostrikov.gym_booking.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import ru.kostrikov.gym_booking.dto.UserDto;
import ru.kostrikov.gym_booking.service.UserService;
import ru.kostrikov.gym_booking.utils.JspHelper;

import java.io.IOException;
import java.util.Optional;

import static ru.kostrikov.gym_booking.utils.UrlPath.*;

@Log4j2
@WebServlet(urlPatterns = {LOGIN, LOGOUT})
public class AuthenticationServlet extends HttpServlet {
    private final UserService userService = UserService.getInstance();

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
