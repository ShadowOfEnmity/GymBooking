package ru.kostrikov.gymbooking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.kostrikov.gymbooking.dto.TrainerDto;
import ru.kostrikov.gymbooking.dto.UserDto;
import ru.kostrikov.gymbooking.entity.Trainer;

import java.math.BigDecimal;

@Mapper
public interface TrainerMapper {
    TrainerMapper INSTANCE = Mappers.getMapper(TrainerMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "specialization", target = "specialization")
    @Mapping(target = "experience", source = "experience")
    @Mapping(target = "rating", source = "rating")
    @Mapping(target = "availability", source = "availability", resultType = boolean.class)
    @Mapping(target = "user", expression = "java(ru.kostrikov.gymbooking.mapper.TrainerMapper.INSTANCE.transformUser(entity))")
    @Mapping(target = "presentation", expression = "java(ru.kostrikov.gymbooking.mapper.TrainerMapper.INSTANCE.presentation(entity))")
    TrainerDto toDto(Trainer entity);

    @Mapping(source = "id", target = "id", resultType = Long.class)
    @Mapping(source = "specialization", target = "specialization", resultType = String.class)
    @Mapping(source = "experience", target = "experience", resultType = String.class)
    @Mapping(source = "rating", target = "rating", resultType = BigDecimal.class, numberFormat = "#.00")
    @Mapping(source = "availability", target = "availability", resultType = boolean.class)
    @Mapping(source = "user.login", target = "login")
    @Mapping(source = "user.password", target = "password")
    @Mapping(source = "user.email", target = "personalInfo.email")
    @Mapping(source = "user.firstName", target = "personalInfo.firstName")
    @Mapping(source = "user.lastName", target = "personalInfo.lastName")
    @Mapping(source = "user.phone", target = "personalInfo.phone")
    @Mapping(source = "user.role", target = "personalInfo.role")
    Trainer toEntity(TrainerDto dto);

    @Named("transformUser")
    default UserDto transformUser(Trainer entity) {
        return UserMapper.INSTANCE.toDto((ru.kostrikov.gymbooking.entity.User) entity);
    }

    default String presentation(Trainer entity) {
        return "Trainer: %s, %s, %s, %s, %s"
                .formatted(entity.getPersonalInfo().getFirstName() + " " + entity.getPersonalInfo().getLastName(), entity.getSpecialization(), entity.getExperience(), entity.getRating(), entity.getAvailability());
    }
}
