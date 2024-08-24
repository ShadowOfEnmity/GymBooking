package ru.kostrikov.gymbooking.utils;

import lombok.experimental.UtilityClass;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;
import ru.kostrikov.gymbooking.converter.DateConverter;
import ru.kostrikov.gymbooking.converter.TimeConverter;
import ru.kostrikov.gymbooking.entity.*;

import java.util.Properties;

@UtilityClass
public class HibernateUtil {

    public static Configuration buildConfiguration() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(TrainingSession.class);
        configuration.addAnnotatedClass(Booking.class);
        configuration.addAnnotatedClass(Gym.class);
        configuration.addAnnotatedClass(GymPhoto.class);
        configuration.addAnnotatedClass(Trainer.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAttributeConverter(new TimeConverter(), true);
        configuration.addAttributeConverter(new DateConverter(), true);

        configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());

        return configuration;
    }

    private static Properties getProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/gym_booking");
        props.setProperty("hibernate.connection.username", "admin");
        props.setProperty("hibernate.connection.password", "123");
        props.setProperty("hibernate.current_session_context_class", "thread");
        props.setProperty("hibernate.show_sql", "true");
        props.setProperty("hibernate.format_sql", "true");
        props.put("hibernate.hbm2ddl.auto", "update");
        return props;
    }


    public static SessionFactory buildSessionFactory() {
        var configuration = buildConfiguration();
        configuration.setProperties(getProperties());
        configuration.configure();
        return configuration.buildSessionFactory();
    }
}
