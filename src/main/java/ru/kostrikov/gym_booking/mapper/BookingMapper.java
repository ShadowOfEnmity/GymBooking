package ru.kostrikov.gym_booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.kostrikov.gym_booking.dto.BookingDto;
import ru.kostrikov.gym_booking.entity.Booking;
import ru.kostrikov.gym_booking.entity.PaymentStatus;
import ru.kostrikov.gym_booking.entity.Status;

@Mapper
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "bookingDate", target = "bookingDate", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "paymentStatus", target = "paymentStatus")
//    @Mapping(source = "training", target = "training", qualifiedByName = "transformTraining")
    BookingDto toDto(Booking entity);

    @Mapping(source = "id", target = "id",  resultType = Long.class)
    @Mapping(source = "bookingDate", target = "bookingDate", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "status", target = "status", qualifiedByName = "transformStatus")
    @Mapping(source = "paymentStatus", target = "paymentStatus", qualifiedByName = "transformPaymentStatus")
    Booking toEntity(BookingDto dto);

    @Named("transformPaymentStatus")
    default PaymentStatus transformPaymentStatus(String status) {
        return PaymentStatus.find(status).orElseThrow();
    }

    @Named("transformStatus")
    default Status transformStatus(String status) {
        return Status.find(status).orElseThrow();
    }

//    @Named("transformTraining")
//    default List<TrainingSessionDto> transformTraining(List<TrainingSession> training) {
//        return training.stream().map(t -> TrainingSessionDto.builder().id(t.getId()).presentation(
//                """
//                        Training session.Gym: %s, trainer: %s,type: %s, date: %s, start time: %s, duration: %s, capacity: %s
//                                                """.formatted(t.getGym().getName(),
//                        t.getTrainer().getPersonalInfo().getFirstName() + " " + t.getTrainer().getPersonalInfo().getLastName(),
//                        t.getType(),
//                        t.getDate(),
//                        t.getStartTime(),
//                        t.getDuration(),
//                        t.getCapacity()
//                )
//        ).build()).toList();
//
//    }
}
