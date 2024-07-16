package ru.kostrikov.gym_booking.utils;

import lombok.Value;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.hibernate.SessionFactory;

@Log4j2
@UtilityClass
public class HibernateSessionFactoryProxy {
    private static final ThreadLocal<SessionFactory> sessionFactoryThreadLocal = new ThreadLocal<>();

    public SessionFactory getSessionFactory() {
        SessionFactory sessionFactory = sessionFactoryThreadLocal.get();
        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory();
            sessionFactoryThreadLocal.set(sessionFactory);
        }
        log.debug("Session factory has been created: {}", sessionFactoryThreadLocal::get);
        return sessionFactory;
    }

    private SessionFactory buildSessionFactory() {
        try {
            return HibernateUtil.buildSessionFactory();
        } catch (Throwable ex) {
            log.error("Error has been occurred: {}", () -> ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public void closeSessionFactory() {
        SessionFactory sessionFactory = sessionFactoryThreadLocal.get();
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactoryThreadLocal.remove();
            log.debug("Session factory has been closed: {}", () -> sessionFactory);
        }
    }
}