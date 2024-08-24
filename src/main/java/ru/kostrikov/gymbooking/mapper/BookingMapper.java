package ru.kostrikov.gymbooking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.kostrikov.gymbooking.dto.BookingDto;
import ru.kostrikov.gymbooking.entity.Booking;
import ru.kostrikov.gymbooking.entity.PaymentStatus;
import ru.kostrikov.gymbooking.entity.Status;

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

}
