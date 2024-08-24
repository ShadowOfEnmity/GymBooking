package ru.kostrikov.gymbooking.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.validation.ValidatorFactory;
import org.hibernate.SessionFactory;
import ru.kostrikov.gymbooking.utils.HibernateUtil;
import ru.kostrikov.gymbooking.utils.ValidationUtil;

@WebListener
public class ValidatorListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ValidationUtil.getValidatorFactory();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ValidationUtil.closeValidatorFactory();
    }
}
