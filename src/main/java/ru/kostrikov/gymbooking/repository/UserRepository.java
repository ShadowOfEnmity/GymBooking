package ru.kostrikov.gymbooking.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.extern.log4j.Log4j2;
import ru.kostrikov.gymbooking.entity.Role;
import ru.kostrikov.gymbooking.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
public class UserRepository extends RepositoryBase<Long, User> {

    public UserRepository(EntityManager entityManager) {
        super(User.class, entityManager);
    }

    public long countUsers() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<User> root = criteriaQuery.from(getClazz());
        criteriaQuery.select(criteriaBuilder.count(root));
        return getEntityManager().createQuery(criteriaQuery).getResultStream().findFirst().orElse(0L);
    }

    @Override
    public User save(User entity) {
        var persistedUser = super.save(entity);
        log.info("New {} {} {} is created", entity.getPersonalInfo().getRole(), entity.getPersonalInfo().getFirstName(), entity.getPersonalInfo().getLastName());
        return persistedUser;
    }

    @Override
    public void delete(User entity) {
        super.delete(entity);
        log.info("User {} is deleted", entity);
    }

    @Override
    public List<User> findAll(int pageNumber, int pageSize, Map<String, Object> properties) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(getClazz());
        Root<User> root = criteriaQuery.from(getClazz());
        criteriaQuery.select(root)
                .orderBy(
                        criteriaBuilder.asc(root.get("personalInfo").get("firstName")),
                        criteriaBuilder.asc(root.get("personalInfo").get("lastName"))
                );
        TypedQuery<User> query = getEntityManager().createQuery(criteriaQuery);
        query.setFirstResult((pageNumber - 1) * pageSize);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    public User findByLoginAndPassword(String login, String password) {
        return getEntityManager().createQuery("FROM User u WHERE u.login = :login AND u.password =:password", User.class)
                .setParameter("login", login)
                .setParameter("password", password)
                .getResultStream().findFirst().orElse(null);
    }

    public boolean userWithRoleExist(Role role) {
        return getEntityManager().createQuery("SELECT COUNT(u) FROM User u WHERE u.personalInfo.role = :role", long.class)
                .setParameter("role", role)
                .getResultList().size() > 0;
    }
}
