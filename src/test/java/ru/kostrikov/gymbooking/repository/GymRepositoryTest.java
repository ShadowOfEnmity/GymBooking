package ru.kostrikov.gymbooking.repository;

import lombok.Cleanup;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cache.jcache.internal.JCacheRegionFactory;
import org.hibernate.cache.spi.CacheImplementor;
import org.hibernate.cache.spi.Region;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;
import org.hibernate.graph.SubGraph;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.stat.Statistics;
import org.hibernate.type.Type;
import org.junit.jupiter.api.*;
import ru.kostrikov.gymbooking.entity.*;
import ru.kostrikov.gymbooking.util.HibernateTestUtil;
import ru.kostrikov.gymbooking.util.TestDataImporter;

import javax.cache.Cache;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static ru.kostrikov.gymbooking.util.TestDataImporter.createGym;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class GymRepositoryTest {

    private SessionFactory sessionFactory;

    @BeforeEach
    void setUp() {
        sessionFactory = HibernateTestUtil.buildSessionFactory();
        TestDataImporter.importData(sessionFactory);

    }

    @AfterEach
    void tearDown() {
        if (sessionFactory != null && sessionFactory.isOpen()) {
            sessionFactory.close();
        }
    }

    @Test
    void verifyCountGyms() {
        sessionFactory.getCache().evictAllRegions();
        @Cleanup Session session = sessionFactory.openSession();
        GymRepository repository = new GymRepository(session);
        long amount = repository.countGyms();
        assertThat(amount).isEqualTo(4L);
    }

    @Test
    void verifyFindAllGymsIsCorrect() {
        @Cleanup var session = sessionFactory.openSession();
        sessionFactory.getCache().evictAllRegions();

        session.beginTransaction();
        GymRepository gymRepository = new GymRepository(session);

        Gym gymFitnessSport = createGym("Фитнес-клуб \"Спорт\"", "ул. Ленина, 10", "Современный фитнес-центр с бассейном", "55.7558", "37.6173", "+7(495)123-45-67", "www.sportclub.ru");
        Gym gymZone = createGym("GymZone", "пр. Мира, 5", "Тренажерный зал с персональными тренерами", "55.7522", "37.6218", "+7(499)987-65-43", "www.gymzone.ru");
        Gym gymFitLife = createGym("FitLife", "ул. Пушкина, 15", "Фитнес-клуб с групповыми занятиями", "55.7587", "37.6195", "+7(495)555-44-33", "www.fitlife.ru");
        Gym gymCrossFitPower = createGym("CrossFit Power", "пр. Победы, 20", "Кроссфит-зал с опытными тренерами", "55.0000", "37.2143", "+7(499)888-77-66", "www.crossfitpower.ru");

        List<Gym> actualResult = gymRepository.findAll();

        session.getTransaction().commit();
        assertThat(actualResult).hasSize(4).containsExactlyInAnyOrder(gymFitnessSport, gymZone, gymFitLife, gymCrossFitPower);
    }

    @Test
    void verifyNewGymIsSaved() {
        @Cleanup Session session = sessionFactory.openSession();
        sessionFactory.getCache().evictAllRegions();
        GymRepository gymRepository = new GymRepository(session);
        Gym gym = createGym("Galaxy", "Тургенева, 13", "Просто хороший зал", "55.7397", "33.2135", "+7(300)123-55-66", "www.galaxy.ru");

        session.beginTransaction();
        Gym persistedGym = gymRepository.save(gym);
        session.refresh(persistedGym);
        session.getTransaction().commit();

        assertThat(persistedGym.getId()).isNotNull();
        assertThat(persistedGym.getName()).isEqualTo("Galaxy");
    }

    @DisplayName("Caching GYM test. Set \'true\' or \'false\' in use_query_cache and use_second_level_cache to manage caching")
    @Test
    void verifyCachingMechanism() {

        sessionFactory.getCache().evictAllRegions();

        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();
        RootGraph<?> gymGraph = session.getEntityGraph("gymWithPhotos");

        Map<String, Object> properties = Map.of(GraphSemantic.LOAD.getJakartaHintName(), gymGraph);
        Gym gym1 = session.find(Gym.class, 1L, properties);

        session.getTransaction().commit();

        SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) sessionFactory;
        CacheImplementor cacheImplementor = sessionFactoryImpl.getCache();

        assertAll("Gym and photo are not cached",
                () -> assertThat(cacheImplementor.contains(Gym.class, 1L)).isTrue(),
                () -> assertThat(cacheImplementor.contains(GymPhoto.class, 1L)).isTrue()
        );
    }

    @Test
    void verifyUsingEntityGraph() {
        sessionFactory.getCache().evictAllRegions();
        sessionFactory.getStatistics().setStatisticsEnabled(true);

        @Cleanup Session session = sessionFactory.openSession();
        @Cleanup Session session2 = sessionFactory.openSession();
        GymRepository gymRepository = new GymRepository(session);
        GymRepository gymRepository2 = new GymRepository(session2);
        sessionFactory.getStatistics().clear();

        session.beginTransaction();
        RootGraph<Gym> gymGraph = session.createEntityGraph(Gym.class);

        gymGraph.addAttributeNodes("training");
        SubGraph<TrainingSession> trainingSessionSubGraph = gymGraph.addSubgraph("training", TrainingSession.class);
        trainingSessionSubGraph.addAttributeNodes("trainer");

        Map<String, Object> properties = Map.of(GraphSemantic.FETCH.getJakartaHintName(), gymGraph);

        Optional<Gym> gymUsingGraph = gymRepository.findById(1L, properties);

        gymUsingGraph
                .flatMap(g -> Optional.ofNullable(g.getTraining()))
                .orElseGet(Collections::emptyList).stream().map(TrainingSession::getTrainer).toList();

        session.getTransaction().commit();
        long numberOfQueriesWithGraph = sessionFactory.getStatistics().getPrepareStatementCount();

        sessionFactory.getCache().evictAllRegions();
        sessionFactory.getStatistics().clear();
        session2.beginTransaction();
        Optional<Gym> gymWithoutGraph = gymRepository2.findById(1L);
        gymWithoutGraph
                .flatMap(g -> Optional.ofNullable(g.getTraining()))
                .orElseGet(Collections::emptyList).stream().map(TrainingSession::getTrainer).toList();

        session2.getTransaction().commit();
        long numberOfQueriesWithoutGraph = sessionFactory.getStatistics().getPrepareStatementCount();

        assertAll("Entity graph doesn't work",
                () -> assertThat(numberOfQueriesWithGraph).isEqualTo(1L),
                () -> assertThat(numberOfQueriesWithoutGraph).isEqualTo(2L)
        );
    }

    @Test
    void verifyGymIsDeleted() {
        @Cleanup var session = sessionFactory.openSession();
        GymRepository gymRepository = new GymRepository(session);
        session.beginTransaction();
        gymRepository.delete(session.get(Gym.class, 2L));
        sessionFactory.getCache().evictAllRegions();
        Gym gym = session.get(Gym.class, 2L);
        session.getTransaction().commit();
        assertThat(gym).isNull();
    }
}