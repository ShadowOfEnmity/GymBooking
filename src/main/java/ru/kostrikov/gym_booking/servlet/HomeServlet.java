package ru.kostrikov.gym_booking.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.kostrikov.gym_booking.dto.UserDto;
import ru.kostrikov.gym_booking.entity.Role;
import ru.kostrikov.gym_booking.utils.UrlPath;

import java.io.IOException;

import static ru.kostrikov.gym_booking.utils.UrlPath.*;

@WebServlet(HOME)
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        UserDto user = (UserDto) req.getSession().getAttribute("user");
        Role.find(user.getRole()).ifPresent(r -> {
            try {
                selectHomePathByRole(resp, req, r);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void selectHomePathByRole(HttpServletResponse resp, HttpServletRequest req, Role role) throws IOException {
        if (role == Role.TRAINER) {
            resp.sendRedirect(UrlPath.buildUrlPathWithContext(req.getContextPath(), TRAINING_SESSIONS));
        } else if (role == Role.USER) {
            resp.sendRedirect(UrlPath.buildUrlPathWithContext(req.getContextPath(), BOOKINGS));
        } else {
            resp.sendRedirect(UrlPath.buildUrlPathWithContext(req.getContextPath(), USERS));
        }
    }
}
