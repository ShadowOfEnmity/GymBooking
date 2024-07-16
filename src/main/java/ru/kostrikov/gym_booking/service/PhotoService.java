package ru.kostrikov.gym_booking.service;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.kostrikov.gym_booking.dao.GymDao;
import ru.kostrikov.gym_booking.dao.GymPhotoDao;
import ru.kostrikov.gym_booking.dto.GymPhotoDto;
import ru.kostrikov.gym_booking.dto.TrainingSessionDto;
import ru.kostrikov.gym_booking.entity.Gym;
import ru.kostrikov.gym_booking.entity.GymPhoto;
import ru.kostrikov.gym_booking.mapper.PhotoMapper;
import ru.kostrikov.gym_booking.mapper.TrainingSessionMapper;
import ru.kostrikov.gym_booking.utils.HibernateSessionFactoryProxy;
import ru.kostrikov.gym_booking.utils.PropertiesUtil;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PhotoService {
    private static final PhotoService INSTANCE = new PhotoService();
    private final SessionFactory sessionFactory = HibernateSessionFactoryProxy.getSessionFactory();
    private final GymPhotoDao photoDao = GymPhotoDao.getInstance();
    private final GymDao gymDao = GymDao.getInstance();
    private final String basePath = PropertiesUtil.get("image.base.url");

    public static PhotoService getInstance() {
        return INSTANCE;
    }

    public Optional<GymPhotoDto> getPhoto(Long id) {
        @Cleanup Session session = sessionFactory.openSession();
        return photoDao.findById(id, session).map(PhotoMapper.INSTANCE::toDto);
    }

    @SneakyThrows
    public void savePhoto(GymPhotoDto photoDto) {
        @Cleanup Session session = sessionFactory.openSession();

        GymPhoto photo = PhotoMapper.INSTANCE.toEntity(photoDto);

        String imagePath = photo.getImageUrl();

        upload(imagePath, photoDto.getImage().getInputStream());

        gymDao.findById(Long.valueOf(photoDto.getGym().getId()), session).ifPresent(photo::addGymPhoto);

        photoDao.save(photo, session);
    }


    @SneakyThrows
    public void upload(String imagePath, InputStream image) {
        Path imageFullPath = Path.of(basePath, imagePath);
        try (image) {
            Files.createDirectories(imageFullPath.getParent());
            Files.write(imageFullPath, image.readAllBytes(), CREATE, TRUNCATE_EXISTING);
        }
    }

    public void delete(Long id) {
        @Cleanup Session session = sessionFactory.openSession();
        photoDao.findById(id, session).ifPresent(photo -> photoDao.delete(photo, session));
    }

    @SneakyThrows
    public Optional<InputStream> get(String imagePath) {
        var imageFullPath = Path.of(basePath, imagePath);

        return Files.exists(imageFullPath)
                ? Optional.of(Files.newInputStream(imageFullPath))
                : Optional.empty();
    }

    public List<GymPhotoDto> getAllPhotosByGymId(int pageNumber, int pageSize, long gymId) {
        @Cleanup Session session = sessionFactory.openSession();
        return photoDao.findAllByGymId(pageNumber, pageSize, gymId, session).stream().map(PhotoMapper.INSTANCE::toDto).toList();
    }

    public List<GymPhotoDto> getAllPhotos(int pageNumber, int pageSize) {
        @Cleanup Session session = sessionFactory.openSession();
        return photoDao.findAll(pageNumber, pageSize, session).stream().map(PhotoMapper.INSTANCE::toDto).toList();
    }

    public long getTotalPhotos() {
        @Cleanup Session session = sessionFactory.openSession();
        return photoDao.countPhotos(session);
    }

    public long getTotalPhotosByGym(Long gymId) {
        @Cleanup Session session = sessionFactory.openSession();
        return photoDao.countTrainingByGym(gymId, session);
    }


}
