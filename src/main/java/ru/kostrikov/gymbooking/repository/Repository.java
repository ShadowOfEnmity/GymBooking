package ru.kostrikov.gymbooking.repository;

import ru.kostrikov.gymbooking.entity.BaseEntity;

import java.io.Serializable;
import java.util.*;

public interface Repository<K extends Serializable, E extends BaseEntity<K>> {
    E save(E entity);

    void delete(E entity);

    E update(E entity);

    default Optional<E> findById(K id) {
        return findById(id, Collections.emptyMap());
    }

    List<E> findAll(int pageNumber, int pageSize, Map<String, Object> properties);

    default List<E> findAll(int pageNumber, int pageSize) {
        return findAll(pageNumber, pageSize, Collections.emptyMap());
    }

    List<E> findAll();

    Optional<E> findById(K id, Map<String, Object> properties);

}
