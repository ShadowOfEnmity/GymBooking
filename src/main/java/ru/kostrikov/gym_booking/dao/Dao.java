package ru.kostrikov.gym_booking.dao;

import org.hibernate.Session;
import ru.kostrikov.gym_booking.exception.ValidationException;

import java.util.List;
import java.util.Optional;

public interface Dao<K, E> {
    Optional<E> findById(K id, Session session);

    List<E> findAll(int pageNumber, int pageSize, Session session);

    void update(E entity, Session session);

    public E save(E entity, Session session) throws ValidationException;

    public void delete(E entity, Session session);
}