package ru.kostrikov.gym_booking.utils;

import lombok.experimental.UtilityClass;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;
import ru.kostrikov.gym_booking.converter.DateConverter;
import ru.kostrikov.gym_booking.converter.TimeConverter;
import ru.kostrikov.gym_booking.entity.*;

import java.util.Properties;

@UtilityClass
public class HibernateUtil {
    public static SessionFactory buildSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(TrainingSession.class);
        configuration.addAnnotatedClass(Booking.class);
        configuration.addAnnotatedClass(Gym.class);
        configuration.addAnnotatedClass(GymPhoto.class);
        configuration.addAnnotatedClass(Trainer.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAttributeConverter(new TimeConverter(), true);
        configuration.addAttributeConverter(new DateConverter(), true);

        Properties props = new Properties();
        props.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/gym_booking");
        props.setProperty("hibernate.connection.username", "");
        props.setProperty("hibernate.connection.password", "");
        props.setProperty("hibernate.show_sql", "true");
        props.setProperty("hibernate.format_sql", "true");
        props.put("hibernate.hbm2ddl.auto", "update");
//        props.setProperty("hibernate.hbm2ddl.auto", "create");

        configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
        configuration.setProperties(props);
        configuration.configure();

        return configuration.buildSessionFactory();
    }
}
