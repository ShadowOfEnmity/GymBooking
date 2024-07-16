package ru.kostrikov.gym_booking.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import ru.kostrikov.gym_booking.dto.TrainerDto;
import ru.kostrikov.gym_booking.dto.UserDto;
import ru.kostrikov.gym_booking.entity.Role;
import ru.kostrikov.gym_booking.exception.ValidationException;
import ru.kostrikov.gym_booking.service.TrainerService;
import ru.kostrikov.gym_booking.service.UserService;
import ru.kostrikov.gym_booking.utils.JspHelper;
import ru.kostrikov.gym_booking.utils.ParametersProcessor;
import ru.kostrikov.gym_booking.utils.UrlPath;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static ru.kostrikov.gym_booking.utils.UrlPath.*;

@Log4j2
@WebServlet(urlPatterns = {USERS, USER})
public class UserServlet extends HttpServlet {
    private final UserService userService = UserService.getInstance();
    private final TrainerService trainerService = TrainerService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/html");

        try {
            Optional.ofNullable(req.getParameter("role")).map(Role::find).ifPresent(role -> req.setAttribute("role", role));

            String servletPath = req.getServletPath();
            Optional<UserDto> userDtoOptional = Optional.ofNullable(req.getParameter("userId")).map(Long::valueOf).map(userService::findById)
                    .orElse(Optional.ofNullable((UserDto) req.getSession().getAttribute("user")));

            if (USERS.equals(servletPath)) {
                int pageNumber = JspHelper.getPageByRequestParameter(req.getParameter("page"), 1);
                req.setAttribute("items", userService.findAll(pageNumber, JspHelper.getPageSize()));
                req.setAttribute("itemsPerPage", JspHelper.getPageSize());
                req.setAttribute("totalItems", userService.getTotalUsers());
                req.setAttribute("currentPage", pageNumber);
                req.getRequestDispatcher(JspHelper.getPath("Users")).forward(req, resp);
            } else if (USER.equals(servletPath) && userDtoOptional.isPresent()) {
                userDtoOptional.filter(userService::isTrainer).flatMap(dto -> trainerService.findById(Long.parseLong(dto.getId()))).ifPresent(trainerDto -> trainerService.setRequestParamsForTrainerEdit(trainerDto, req));
                userDtoOptional.filter(dto -> userService.isUser(dto) || userService.isAdmin(dto)).ifPresent(userDto -> userService.setRequestParamsForUserEdit(userDto, req));
                req.getRequestDispatcher(JspHelper.getPath("UserCard")).forward(req, resp);
            } else {
                req.setAttribute("roles", Arrays.stream(Role.values()).filter(role -> role != Role.ADMIN).toArray());
                req.getRequestDispatcher(JspHelper.getPath("UserCard")).forward(req, resp);
            }
        } catch (ServletException | IOException e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String servletPath = req.getServletPath();
            Optional<Role> optionalRole = Optional.ofNullable(req.getParameter("role")).flatMap(Role::find);

            Optional<UserDto> userDtoFromRequest = Optional.ofNullable(req.getParameter("userId")).filter(s -> !s.isEmpty()).map(Long::parseLong).map(userService::findById)
                    .orElse(Optional.ofNullable((UserDto) req.getSession().getAttribute("user")));
            Map<String, Object> map = new HashMap<>();
            userDtoFromRequest.ifPresent(dto -> map.put("id", dto.getId()));
            if (USER.equals(servletPath)) {
                try {
                    if (optionalRole.isPresent()) {
                        Role role = optionalRole.get();

                        switch (role) {
                            case ADMIN, USER ->
                                    ParametersProcessor.process(req, UserDto.class, UserDto.UserDtoBuilder.class, map, dto -> userService.saveOrUpdate(dto, userDtoFromRequest.isEmpty()));
                            case TRAINER -> {
                                Optional<UserDto> userDto = ParametersProcessor.process(req, UserDto.class, UserDto.UserDtoBuilder.class, map, Function.identity());
                                if (userDto.isPresent()) {
                                    map.put("user", userDto.get());
                                    ParametersProcessor.process(req, TrainerDto.class, TrainerDto.TrainerDtoBuilder.class, map, dto -> trainerService.saveOrUpdate(dto, userDtoFromRequest.isEmpty()));
                                }
                            }
                            default -> throw new IllegalStateException("Unexpected value: " + role);
                        }
                        resp.sendRedirect(UrlPath.buildUrlPathWithContext(req.getContextPath(), HOME));
                    } else {
                        resp.sendRedirect(UrlPath.buildUrlPathWithContext(req.getContextPath(), LOGIN));
                    }
                } catch (ValidationException e) {
                    userDtoFromRequest.filter(userService::isTrainer)
                            .flatMap(userDto -> ParametersProcessor.process(req, UserDto.class, UserDto.UserDtoBuilder.class, map, Function.identity()))
                            .flatMap(trainerDto -> ParametersProcessor.process(req, TrainerDto.class, TrainerDto.TrainerDtoBuilder.class, map, Function.identity()))
                            .ifPresent(trainerDto -> trainerService.setRequestParamsForTrainerEdit(trainerDto, req));


                    userDtoFromRequest.filter(dto -> userService.isUser(dto) || userService.isAdmin(dto))
                            .flatMap(userDto -> ParametersProcessor.process(req, UserDto.class, UserDto.UserDtoBuilder.class, map, Function.identity()))
                            .ifPresent(userDto -> userService.setRequestParamsForUserEdit(userDto, req));

                    req.setAttribute("errors", e.getErrors());
                    req.getRequestDispatcher(JspHelper.getPath("UserCard")).forward(req, resp);
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (ServletException | IOException e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

