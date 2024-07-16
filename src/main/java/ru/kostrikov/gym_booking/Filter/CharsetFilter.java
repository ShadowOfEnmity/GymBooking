package ru.kostrikov.gym_booking.Filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import ru.kostrikov.gym_booking.service.UserService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CharsetFilter implements Filter {

    UserService userService = UserService.getInstance();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        userService.initialize();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        chain.doFilter(request, response);
    }
}
