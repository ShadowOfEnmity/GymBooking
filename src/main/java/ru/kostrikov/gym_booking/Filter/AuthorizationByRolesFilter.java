package ru.kostrikov.gym_booking.Filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import ru.kostrikov.gym_booking.dto.UserDto;
import ru.kostrikov.gym_booking.service.UserService;
import ru.kostrikov.gym_booking.utils.JspHelper;
import ru.kostrikov.gym_booking.utils.UrlPath;

import java.io.IOException;
import java.util.Set;

import static ru.kostrikov.gym_booking.utils.UrlPath.*;

@Log4j2
public class AuthorizationByRolesFilter implements Filter {

    public static final Set<String> EXCLUDED_TRAINER_PATHS = Set.of(UPLOAD_PHOTO, USERS);
    public static final Set<String> EXCLUDED_USER_PATHS = Set.of(UPLOAD_PHOTO, USERS, TRAINING_SESSION);
    private final UserService userService = UserService.getInstance();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {

        var uri = ((HttpServletRequest) request).getRequestURI();

        UserDto user = (UserDto) ((HttpServletRequest) request).getSession().getAttribute("user");
        try {

            if (user == null || isAvailable(uri, user)) {
                chain.doFilter(request, response);
            } else ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch (ServletException | IOException e) {
            log.error(e.getMessage(), e);
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public boolean isAvailable(String uri, UserDto user) {
        return userService.isAdmin(user)
                || (userService.isUser(user) && EXCLUDED_USER_PATHS.stream().noneMatch(uri::equals))
                || (userService.isTrainer(user) && EXCLUDED_TRAINER_PATHS.stream().noneMatch(uri::equals));
    }
}
