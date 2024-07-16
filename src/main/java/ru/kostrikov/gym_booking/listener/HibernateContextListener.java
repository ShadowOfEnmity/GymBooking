package ru.kostrikov.gym_booking.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.validation.ValidatorFactory;
import lombok.extern.log4j.Log4j2;
import ru.kostrikov.gym_booking.utils.HibernateSessionFactoryProxy;
import ru.kostrikov.gym_booking.utils.ValidationUtil;

import java.util.Optional;

@Log4j2
@WebListener
public class HibernateContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        HibernateSessionFactoryProxy.getSessionFactory();
        log.info("Session factory has been initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ValidationUtil.closeValidatorFactory();
        HibernateSessionFactoryProxy.closeSessionFactory();
        log.info("Session factory has been closed");
    }
}