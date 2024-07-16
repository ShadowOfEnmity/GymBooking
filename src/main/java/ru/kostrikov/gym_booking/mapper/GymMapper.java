package ru.kostrikov.gym_booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.kostrikov.gym_booking.dto.BookingDto;
import ru.kostrikov.gym_booking.dto.GymDto;
import ru.kostrikov.gym_booking.dto.UserDto;
import ru.kostrikov.gym_booking.entity.Booking;
import ru.kostrikov.gym_booking.entity.Gym;
import ru.kostrikov.gym_booking.entity.Trainer;

import java.math.BigDecimal;

@Mapper
public interface GymMapper {
    GymMapper INSTANCE = Mappers.getMapper(GymMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "latitude", target = "latitude")
    @Mapping(source = "longitude", target = "longitude")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "website", target = "website")
    @Mapping(target = "presentation", expression = "java(ru.kostrikov.gym_booking.mapper.GymMapper.INSTANCE.presentation(entity))")

    GymDto toDto(Gym entity);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "latitude", target = "latitude")
    @Mapping(source = "longitude", target = "longitude")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "website", target = "website")
    Gym toEntity(GymDto dto);

//    @Named("presentation")
    default String presentation(Gym entity) {
        return "Gym: %s, %s, %s, %s (lat), %s (lon), %s, %s"
                .formatted(entity.getName(), entity.getAddress(), entity.getDescription(), entity.getLatitude(), entity.getLongitude(), entity.getPhone(), entity.getWebsite());
    }
}
