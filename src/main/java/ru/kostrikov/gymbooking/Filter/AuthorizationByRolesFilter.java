package ru.kostrikov.gymbooking.Filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.kostrikov.gymbooking.dto.UserDto;
import ru.kostrikov.gymbooking.interceptor.TransactionInterceptor;
import ru.kostrikov.gymbooking.repository.UserRepository;
import ru.kostrikov.gymbooking.service.PhotoService;
import ru.kostrikov.gymbooking.service.UserService;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Set;

import static ru.kostrikov.gymbooking.utils.UrlPath.*;

@Log4j2
public class AuthorizationByRolesFilter implements Filter {

    public static final Set<String> EXCLUDED_TRAINER_PATHS = Set.of(UPLOAD_PHOTO, USERS);
    public static final Set<String> EXCLUDED_USER_PATHS = Set.of(UPLOAD_PHOTO, USERS, TRAINING_SESSION);
    //    private final UserService userService = UserService.getInstance();
    private UserService userService;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            SessionFactory sessionFactory = (SessionFactory) filterConfig.getServletContext().getAttribute("sessionFactory");
            var session = (Session) Proxy.newProxyInstance(SessionFactory.class.getClassLoader(), new Class[]{Session.class},
                    (proxy, method, args) -> method.invoke(sessionFactory.getCurrentSession(), args));

            var transactionInterceptor = new TransactionInterceptor(sessionFactory);

            userService = new ByteBuddy()
                    .subclass(UserService.class)
                    .method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(transactionInterceptor))
                    .make()
                    .load(PhotoService.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor(UserRepository.class)
                    .newInstance(new UserRepository(session));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
