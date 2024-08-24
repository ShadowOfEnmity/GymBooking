package ru.kostrikov.gymbooking.listener;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Cache;
import org.hibernate.*;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.stat.Statistics;
import ru.kostrikov.gymbooking.utils.HibernateUtil;

import javax.naming.NamingException;
import javax.naming.Reference;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Log4j2
@WebListener
public class SessionFactoryProxy implements ServletContextListener, SessionFactory {

    private static final String SESSION_FACTORY_KEY = "sessionFactory";

    private SessionFactory delegate;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        delegate = HibernateUtil.buildSessionFactory();
        servletContext.setAttribute(SESSION_FACTORY_KEY, this);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if(delegate.isOpen()) {
            delegate.close();
        }
    }

    public static SessionFactory getSessionFactory(ServletContext servletContext) {
        return (SessionFactory) servletContext.getAttribute(SESSION_FACTORY_KEY);
    }


    @Override
    public SessionFactoryOptions getSessionFactoryOptions() {
        return delegate.getSessionFactoryOptions();
    }

    @Override
    public SessionBuilder withOptions() {
        return delegate.withOptions();
    }

    @Override
    public Session openSession() throws HibernateException {
        return delegate.openSession();
    }

    @Override
    public Session getCurrentSession() throws HibernateException {
        return delegate.getCurrentSession();
    }

    @Override
    public StatelessSessionBuilder withStatelessOptions() {
        return delegate.withStatelessOptions();
    }

    @Override
    public StatelessSession openStatelessSession() {
        return delegate.openStatelessSession();
    }

    @Override
    public StatelessSession openStatelessSession(Connection connection) {
        return delegate.openStatelessSession();
    }


    @Override
    public EntityManager createEntityManager() {
        return delegate.createEntityManager();
    }

    @Override
    public EntityManager createEntityManager(Map map) {
        return delegate.createEntityManager(map);
    }

    @Override
    public EntityManager createEntityManager(SynchronizationType synchronizationType) {
        return delegate.createEntityManager(synchronizationType);
    }

    @Override
    public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
        return delegate.createEntityManager(synchronizationType, map);
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return delegate.getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return delegate.getMetamodel();
    }

    @Override
    public boolean isOpen() {
        return delegate.isOpen();
    }

    @Override
    public void close() throws HibernateException {
    }

    @Override
    public Map<String, Object> getProperties() {
        return delegate.getProperties();
    }

    @Override
    public Statistics getStatistics() throws HibernateException {
        return delegate.getStatistics();
    }

    @Override
    public boolean isClosed() throws HibernateException {
        return delegate.isClosed();
    }

    @Override
    public Cache getCache() {
        return delegate.getCache();
    }

    @Override
    public PersistenceUnitUtil getPersistenceUnitUtil() {
        return delegate.getPersistenceUnitUtil();
    }

    @Override
    public void addNamedQuery(String name, Query query) {
        delegate.addNamedQuery(name, query);
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return delegate.unwrap(cls);
    }

    @Override
    public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
        delegate.addNamedEntityGraph(graphName, entityGraph);
    }

    @Override
    public <T> List<EntityGraph<? super T>> findEntityGraphsByType(Class<T> entityClass) {
        return delegate.findEntityGraphsByType(entityClass);
    }

    @Override
    public Set<String> getDefinedFilterNames() {
        return delegate.getDefinedFilterNames();
    }

    @Override
    public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
        return delegate.getFilterDefinition(filterName);
    }

    @Override
    public boolean containsFetchProfileDefinition(String name) {
        return delegate.containsFetchProfileDefinition(name);
    }

    @Override
    public Reference getReference() throws NamingException {
        return delegate.getReference();
    }

}