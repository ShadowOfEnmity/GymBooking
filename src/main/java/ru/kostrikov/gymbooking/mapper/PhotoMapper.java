package ru.kostrikov.gymbooking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.kostrikov.gymbooking.dto.GymPhotoDto;
import ru.kostrikov.gymbooking.entity.GymPhoto;

@Mapper
public interface PhotoMapper {
    PhotoMapper INSTANCE = Mappers.getMapper(PhotoMapper.class);
    String IMAGE_FOLDER = "gym_photos/";

    @Mapping(source = "id", target = "id")
    @Mapping(source = "imageUrl", target = "imageUrl")
    @Mapping(source = "alt", target = "alt")
    @Mapping(source = "gym", target = "gym")
    GymPhotoDto toDto(GymPhoto entity);

    @Mapping(source = "id", target = "id")
    @Mapping(target = "imageUrl", expression = "java(ru.kostrikov.gymbooking.utils.FileManagementUtils.generateNewImageName(\"" + IMAGE_FOLDER + "\"+dto.getImage().getSubmittedFileName()))")
    @Mapping(source = "alt", target = "alt")
    @Mapping(source = "gym", target = "gym")
    GymPhoto toEntity(GymPhotoDto dto);


}
