package ru.kostrikov.gymbooking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.kostrikov.gymbooking.dto.UserDto;
import ru.kostrikov.gymbooking.entity.User;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "personalInfo.firstName", target = "firstName")
    @Mapping(source = "personalInfo.lastName", target = "lastName")
    @Mapping(source = "login", target = "login")
    @Mapping(source = "personalInfo.phone", target = "phone")
    @Mapping(source = "personalInfo.email", target = "email")
    @Mapping(source = "personalInfo.role", target = "role")
    @Mapping(target = "password", ignore = true)
//    @Mapping(target = "oldPassword", ignore = true)
    @Mapping(target = "fullName", expression = "java(entity.getPersonalInfo().getFirstName()+\" \"+entity.getPersonalInfo().getLastName())")
    UserDto toDto(User entity);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "firstName", target = "personalInfo.firstName")
    @Mapping(source = "lastName", target = "personalInfo.lastName")
    @Mapping(source = "login", target = "login")
    @Mapping(source = "phone", target = "personalInfo.phone")
    @Mapping(source = "email", target = "personalInfo.email")
    @Mapping(source = "role", target = "personalInfo.role")
    @Mapping(target = "password", source = "password")
    User toEntity(UserDto dto);
}
