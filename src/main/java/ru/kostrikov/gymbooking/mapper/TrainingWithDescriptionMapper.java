package ru.kostrikov.gymbooking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.kostrikov.gymbooking.dto.TrainingSessionWithPresentationDto;
import ru.kostrikov.gymbooking.entity.TrainingSession;

@Mapper
public interface TrainingWithDescriptionMapper {
    TrainingWithDescriptionMapper INSTANCE = Mappers.getMapper(TrainingWithDescriptionMapper.class);
    String PRESENTATION_FORMAT = "Training session.Gym: %s, trainer: %s,type: %s, date: %s, start time: %s, duration: %s, capacity: %s";

    @Mapping(target = "id", source = "id")
    @Mapping(target = "presentation", expression = "java(\"" + PRESENTATION_FORMAT + "\".formatted(entity.getGym().getName(),\n" +
            "                        entity.getTrainer().getPersonalInfo().getFirstName() + \" \" + entity.getTrainer().getPersonalInfo().getLastName(),\n" +
            "                        entity.getType(),\n" +
            "                        entity.getDate(),\n" +
            "                        entity.getStartTime(),\n" +
            "                        entity.getDuration(),\n" +
            "                        entity.getCapacity()))")
    TrainingSessionWithPresentationDto toDto(TrainingSession entity);

}