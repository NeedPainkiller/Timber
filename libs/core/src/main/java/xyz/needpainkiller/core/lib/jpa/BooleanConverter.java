package xyz.needpainkiller.core.lib.jpa;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BooleanConverter implements AttributeConverter<Boolean, Integer> {

    private static final int YES = 1;
    private static final int NO = 0;


    @Override
    public Integer convertToDatabaseColumn(Boolean attribute) {
        if (attribute == null) return NO;
        return attribute ? YES : NO;
    }

    @Override
    public Boolean convertToEntityAttribute(Integer dbData) {
        return dbData.equals(YES);
    }
}