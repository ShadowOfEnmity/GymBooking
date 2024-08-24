package ru.kostrikov.gymbooking.service;

import jakarta.persistence.EntityGraph;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.hibernate.graph.GraphSemantic;
import ru.kostrikov.gymbooking.dto.GymDto;
import ru.kostrikov.gymbooking.entity.Gym;
import ru.kostrikov.gymbooking.entity.User;
import ru.kostrikov.gymbooking.mapper.GymMapper;
import ru.kostrikov.gymbooking.repository.GymRepository;
import ru.kostrikov.gymbooking.utils.ValidationUtil;

import java.util.*;

//@NoArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class GymService {
    private final GymRepository gymRepository;

    @Transactional
    public long getTotalGyms() {
//        @Cleanup Session session = sessionFactory.openSession();
        return gymRepository.countGyms();
    }

    @Transactional
    public List<GymDto> findAll(int pageNumber, int pageSize) {
//        @Cleanup Session session = sessionFactory.openSession();
        return gymRepository.findAll(pageNumber, pageSize).stream().map(GymMapper.INSTANCE::toDto).toList();
    }

    @Transactional
    public List<GymDto> findAll() {
//        @Cleanup Session session = sessionFactory.openSession();
        return gymRepository.findAll().stream().map(GymMapper.INSTANCE::toDto).toList();
    }

    @Transactional
    public Optional<GymDto> findById(long id) {
//        @Cleanup Session session = sessionFactory.openSession();
        return gymRepository.findById(id).map(GymMapper.INSTANCE::toDto).or(Optional::empty);
    }

    @Transactional
    public GymDto saveOrUpdate(GymDto gym, boolean isNew) {
        Set<ConstraintViolation<GymDto>> gymViolations = ValidationUtil.validate(gym);

        if (!gymViolations.isEmpty()) {
            throw new ConstraintViolationException(gymViolations);
        }
//        @Cleanup Session session = sessionFactory.openSession();
        Gym entity = GymMapper.INSTANCE.toEntity(gym);
        if (isNew) {
            return GymMapper.INSTANCE.toDto(gymRepository.save(entity));
        } else {
            return update(gym);
        }
    }

    @Transactional
    public GymDto update(GymDto dto) {
        Gym entity = GymMapper.INSTANCE.toEntity(dto);
        Optional<Gym> existingGymOptional = gymRepository.findById(entity.getId());
        if (existingGymOptional.isPresent()) {
            var existingGym = existingGymOptional.get();
            existingGym.setId(entity.getId());
            existingGym.setName(entity.getName());
            existingGym.setAddress(entity.getAddress());
            existingGym.setDescription(entity.getDescription());
            existingGym.setLongitude(entity.getLongitude());
            existingGym.setLatitude(entity.getLatitude());
            existingGym.setPhone(entity.getPhone());
            existingGym.setWebsite(entity.getWebsite());
            return GymMapper.INSTANCE.toDto(gymRepository.update(existingGym));
        }
        return dto;
    }

    @Transactional
    public void delete(Long id){
        EntityGraph<Gym> graph = gymRepository.getEntityManager().createEntityGraph(Gym.class);
        graph.addSubgraph("training");
        Map<String, Object> properties = new HashMap<>();
        properties.put(GraphSemantic.LOAD.getJakartaHintName(), graph);
        Optional<Gym> gym = gymRepository.findById(id, properties);
        gym.map(Gym::getTraining).map(ArrayList::new).stream()
                .flatMap(Collection::stream).forEach(tSession -> tSession.setGym(null));
        gym.ifPresent(gymRepository::delete);
    }
}
