package ru.kostrikov.gym_booking.Filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import ru.kostrikov.gym_booking.utils.JspHelper;
import ru.kostrikov.gym_booking.utils.UrlPath;

import java.io.IOException;
import java.util.Set;

import static ru.kostrikov.gym_booking.utils.UrlPath.*;

@Log4j2
public class AuthorizationFilter implements Filter {

    public static final Set<String> PUBLIC_PATH = Set.of(LOGIN, USER);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        var uri = ((HttpServletRequest) servletRequest).getRequestURI();
        try {
            if (isPublicPath(uri) || isUserLoggedIn(servletRequest)) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                ((HttpServletResponse) servletResponse).sendRedirect(UrlPath.buildUrlPathWithContext(((HttpServletRequest) servletRequest).getContextPath(), LOGIN));
            }

        } catch (ServletException | IOException e) {
            log.error(e.getMessage(), e);
            ((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isUserLoggedIn(ServletRequest servletRequest) {
        var user = ((HttpServletRequest) servletRequest).getSession().getAttribute("user");
        return user != null;
    }

    private boolean isPublicPath(String uri) {
        return PUBLIC_PATH.stream().anyMatch(uri::equals);
    }
}
