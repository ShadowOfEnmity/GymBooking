package ru.kostrikov.gymbooking.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.kostrikov.gymbooking.dto.GymDto;
import ru.kostrikov.gymbooking.dto.GymPhotoDto;
import ru.kostrikov.gymbooking.interceptor.TransactionInterceptor;
import ru.kostrikov.gymbooking.mapper.PhotoMapper;
import ru.kostrikov.gymbooking.repository.GymPhotoRepository;
import ru.kostrikov.gymbooking.repository.GymRepository;
import ru.kostrikov.gymbooking.service.PhotoService;
import ru.kostrikov.gymbooking.utils.JspHelper;
import ru.kostrikov.gymbooking.utils.PropertiesUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static ru.kostrikov.gymbooking.utils.UrlPath.*;

@Log4j2
@WebServlet(urlPatterns = {PHOTOS, PHOTO, UPLOAD_PHOTO, IMAGE})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10,      // 10 MB
        maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
public class PhotoServlet extends HttpServlet {

//    private final PhotoService photoService = PhotoService.getInstance();

    private  PhotoService photoService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            SessionFactory sessionFactory = (SessionFactory) config.getServletContext().getAttribute("sessionFactory");
            var session = (Session) Proxy.newProxyInstance(SessionFactory.class.getClassLoader(), new Class[]{Session.class},
                    (proxy, method, args) -> method.invoke(sessionFactory.getCurrentSession(), args));

            var transactionInterceptor = new TransactionInterceptor(sessionFactory);
            photoService = new ByteBuddy()
                    .subclass(PhotoService.class)
                    .method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(transactionInterceptor))
                    .make()
                    .load(PhotoService.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor(GymPhotoRepository.class, GymRepository.class)
                    .newInstance(new GymPhotoRepository(session), new GymRepository(session));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html");
        try {
            long gymId = Optional.ofNullable((Long) request.getSession().getAttribute("gymId"))
                    .or(() -> Optional.ofNullable(request.getParameter("gymId")).map(Long::parseLong)).orElseGet(() -> 0L);
            Optional<String> alt = Optional.ofNullable(request.getParameter("alt"));

            Optional<String> open = Optional.ofNullable(request.getParameter("open"));
            Optional<String> delete = Optional.ofNullable(request.getParameter("delete"));
            String servletPath = request.getServletPath();

            Long photoId = Optional.ofNullable(request.getParameter("photoId")).map(Long::valueOf).orElse(0L);

            if (PHOTO.equals(servletPath)) {
                if (open.isPresent()) {
                    request.setAttribute("photoId", photoId);
//                request.setAttribute("alt", alt.get());
                    response.setStatus(HttpServletResponse.SC_OK);
                    request.getRequestDispatcher(JspHelper.getPath("Image")).forward(request, response);
                } else if (delete.isPresent() && gymId > 0) {
                    photoService.delete(photoId);
                    response.sendRedirect(Strings.concat(PHOTOS, "?gymId=%s".formatted(gymId)));
                } else if (gymId > 0) {
                    request.setAttribute("gymId", gymId);
                    request.getRequestDispatcher(JspHelper.getPath("UploadImagePhoto")).forward(request, response);
                } else response.sendError(HttpServletResponse.SC_NOT_FOUND);

            } else if (UPLOAD_PHOTO.equals(servletPath) && gymId > 0) {
                Part part = request.getPart("image");
                GymPhotoDto photoDto = GymPhotoDto.builder().image(part).alt(alt.orElseGet(() -> "")).gym(GymDto.builder().id(Long.toString(gymId)).build()).build();
                photoService.savePhoto(photoDto);
                response.sendRedirect(Strings.concat(PHOTOS, "?gymId=%s".formatted(gymId)));
            } else if (delete.isPresent() && gymId > 0) {
                photoService.delete(photoId);
                response.sendRedirect(Strings.concat(PHOTOS, "?gymId=%s".formatted(gymId)));
            } else response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (ServletException | IOException e) {
            log.error(e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html");
        try {
            Long photoId = Optional.ofNullable(request.getParameter("photoId")).map(Long::valueOf).orElse(0L);
            String servletPath = request.getServletPath();

            String referer = JspHelper.getRefererPath(Optional.ofNullable(request.getHeader("Referer")).orElseGet(() -> ""));

            if (!(GYMS.equals(referer) || PHOTOS.equals(referer))) {
                request.getSession().removeAttribute("gymId");
            }

            Long gymId = Optional.ofNullable(request.getParameter("gymId")).map(Long::parseLong).or(() -> Optional.ofNullable((Long) request.getSession().getAttribute("gymId"))).orElseGet(() -> 0L);

            int pageNumber = JspHelper.getPageByRequestParameter(request.getParameter("page"), 1);
            if (IMAGE.equals(servletPath) && (photoId > 0)) {
                String imagesUrl = PropertiesUtil.get("image.base.url");
                photoService.getPhoto(photoId).map(GymPhotoDto::getImageUrl)
                        .map(File::new)
                        .map(File::getName)
                        .map(imageName -> Paths.get(imagesUrl, PhotoMapper.IMAGE_FOLDER, imageName))
                        .filter(imagePath -> Files.exists(imagePath) && Files.isRegularFile(imagePath))
                        .ifPresent(path -> {
                            response.setContentType(getServletContext().getMimeType(path.toFile().getName()));
                            try (ServletOutputStream outputStream = response.getOutputStream()) {
                                Files.copy(path, outputStream);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });

            } else if (PHOTOS.equals(servletPath)) {
                if (gymId > 0) {
                    request.setAttribute("photos", photoService.getAllPhotosByGymId(pageNumber, JspHelper.getPageSize(), gymId));
                    request.setAttribute("itemsPerPage", JspHelper.getPageSize());
                    request.setAttribute("totalItems", photoService.getTotalPhotosByGym(gymId));
                    request.setAttribute("currentPage", pageNumber);
                    request.getSession().setAttribute("gymId", gymId);
                } else {
                    request.setAttribute("photos", photoService.getAllPhotos(pageNumber, JspHelper.getPageSize()));
                    request.setAttribute("itemsPerPage", JspHelper.getPageSize());
                    request.setAttribute("totalItems", photoService.getTotalPhotos());
                    request.setAttribute("currentPage", pageNumber);
                }
                request.getRequestDispatcher(JspHelper.getPath("Photos")).forward(request, response);
            } else response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (ServletException | IOException e) {
            log.error(e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
