package ru.kostrikov.gymbooking.service;

import jakarta.persistence.EntityGraph;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.hibernate.graph.GraphSemantic;
import ru.kostrikov.gymbooking.dto.GymPhotoDto;
import ru.kostrikov.gymbooking.entity.Gym;
import ru.kostrikov.gymbooking.entity.GymPhoto;
import ru.kostrikov.gymbooking.mapper.PhotoMapper;
import ru.kostrikov.gymbooking.repository.GymPhotoRepository;
import ru.kostrikov.gymbooking.repository.GymRepository;
import ru.kostrikov.gymbooking.utils.PropertiesUtil;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

//@NoArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PhotoService {
    //    private static final PhotoService INSTANCE = new PhotoService();
//    private final SessionFactory sessionFactory = HibernateSessionFactoryProxy.getSessionFactory();
//    private final GymPhotoDao photoDao = GymPhotoDao.getInstance();
//    private final GymDao gymDao = GymDao.getInstance();
    private final GymPhotoRepository photoRepository;
    private final GymRepository gymRepository;
    private final String basePath = PropertiesUtil.get("image.base.url");

//    public static PhotoService getInstance() {
//        return INSTANCE;
//    }

    public Optional<GymPhotoDto> getPhoto(Long id) {
//        @Cleanup Session session = sessionFactory.openSession();
        return photoRepository.findById(id).map(PhotoMapper.INSTANCE::toDto);
    }

    @Transactional
    @SneakyThrows
    public void savePhoto(GymPhotoDto photoDto) {
//        @Cleanup Session session = sessionFactory.openSession();

        GymPhoto photo = PhotoMapper.INSTANCE.toEntity(photoDto);

        String imagePath = photo.getImageUrl();

        upload(imagePath, photoDto.getImage().getInputStream());

        gymRepository.findById(Long.valueOf(photoDto.getGym().getId())).ifPresent(photo::setGym);

        photoRepository.save(photo);
    }


    @SneakyThrows
    public void upload(String imagePath, InputStream image) {
        Path imageFullPath = Path.of(basePath, imagePath);
        try (image) {
            Files.createDirectories(imageFullPath.getParent());
            Files.write(imageFullPath, image.readAllBytes(), CREATE, TRUNCATE_EXISTING);
        }
    }

    @Transactional
    public void delete(Long id) {
//        @Cleanup Session session = sessionFactory.openSession();
        EntityGraph<GymPhoto> graph = gymRepository.getEntityManager().createEntityGraph(GymPhoto.class);
        graph.addSubgraph("Gym");
        Map<String, Object> properties = new HashMap<>();
        properties.put(GraphSemantic.LOAD.getJakartaHintName(), graph);
        Optional<GymPhoto> gymPhoto = photoRepository.findById(id, properties);
        gymPhoto.ifPresent(photo -> photo.setGym(null));
        gymPhoto.ifPresent(photoRepository::delete);
    }

    @SneakyThrows
    public Optional<InputStream> get(String imagePath) {
        var imageFullPath = Path.of(basePath, imagePath);

        return Files.exists(imageFullPath)
                ? Optional.of(Files.newInputStream(imageFullPath))
                : Optional.empty();
    }

    @Transactional
    public List<GymPhotoDto> getAllPhotosByGymId(int pageNumber, int pageSize, long gymId) {
//        @Cleanup Session session = sessionFactory.openSession();
        return photoRepository.findAllByGymId(pageNumber, pageSize, gymId).stream().map(PhotoMapper.INSTANCE::toDto).toList();
    }

    @Transactional
    public List<GymPhotoDto> getAllPhotos(int pageNumber, int pageSize) {
//        @Cleanup Session session = sessionFactory.openSession();
        return photoRepository.findAll(pageNumber, pageSize).stream().map(PhotoMapper.INSTANCE::toDto).toList();
    }

    @Transactional
    public long getTotalPhotos() {
//        @Cleanup Session session = sessionFactory.openSession();
        return photoRepository.countPhotos();
    }

    @Transactional
    public long getTotalPhotosByGym(Long gymId) {
//        @Cleanup Session session = sessionFactory.openSession();
        return photoRepository.countPhotosByGym(gymId);
    }


}
