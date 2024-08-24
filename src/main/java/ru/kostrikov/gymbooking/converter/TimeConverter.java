package ru.kostrikov.gymbooking.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Optional;

@Converter(autoApply = true)
public class TimeConverter implements AttributeConverter<LocalTime, Time> {

    @Override
    public Time convertToDatabaseColumn(LocalTime localTime) {
        return Optional.ofNullable(localTime)
                .map(Time::valueOf)
                .orElse(null);
    }

    @Override
    public LocalTime convertToEntityAttribute(Time time) {
        return Optional.ofNullable(time)
                .map(Time::toLocalTime)
                .orElse(null);
    }
}
