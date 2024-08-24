package ru.kostrikov.gymbooking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.kostrikov.gymbooking.dto.TrainingSessionDto;
import ru.kostrikov.gymbooking.entity.TrainingSession;

@Mapper
public interface TrainingSessionMapper {
    TrainingSessionMapper INSTANCE = Mappers.getMapper(TrainingSessionMapper.class);
//    String PRESENTATION_FORMAT = "Training session.Gym: %s, trainer: %s,type: %s, date: %s, start time: %s, duration: %s, capacity: %s";

    @Mapping(target = "id", source = "id")
//    @Mapping(target = "gym", source = "gym.id")
//    @Mapping(target = "trainer", source = "trainer.id")
    @Mapping(target = "gym",  expression = "java(ru.kostrikov.gymbooking.mapper.GymMapper.INSTANCE.toDto(entity.getGym()))")
    @Mapping(target = "trainer", expression = "java(ru.kostrikov.gymbooking.mapper.TrainerMapper.INSTANCE.toDto(entity.getTrainer()))")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "date", source = "date", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "startTime", source = "startTime", dateFormat = "HH:mm")
    @Mapping(target = "duration", source = "duration")
    @Mapping(target = "price", source = "price", numberFormat = "#.00")
    @Mapping(target = "capacity", source = "capacity")
    TrainingSessionDto toDto(TrainingSession entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "gym", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "description", source = "description")
    @Mapping(target = "date", source = "date", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "startTime", source = "startTime", dateFormat = "HH:mm")
    @Mapping(target = "duration", source = "duration", resultType = Integer.class)
    @Mapping(target = "price", source = "price", numberFormat = "#.00")
    @Mapping(target = "capacity", source = "capacity", resultType = Integer.class)
    TrainingSession toEntity(TrainingSessionDto dto);

}