package ru.kostrikov.gymbooking.Filter;

import jakarta.servlet.*;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.kostrikov.gymbooking.interceptor.TransactionInterceptor;
import ru.kostrikov.gymbooking.repository.UserRepository;
import ru.kostrikov.gymbooking.service.UserService;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;

public class CharsetFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            SessionFactory sessionFactory = (SessionFactory) filterConfig.getServletContext().getAttribute("sessionFactory");
            var session = (Session) Proxy.newProxyInstance(SessionFactory.class.getClassLoader(), new Class[]{Session.class},
                    (proxy, method, args) -> method.invoke(sessionFactory.getCurrentSession(), args));

            var transactionInterceptor = new TransactionInterceptor(sessionFactory);

            var userService = new ByteBuddy()
                    .subclass(UserService.class)
                    .method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(transactionInterceptor))
                    .make()
                    .load(UserService.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor(UserRepository.class)
                    .newInstance(new UserRepository(session));

            userService.initialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        chain.doFilter(request, response);
    }
}
