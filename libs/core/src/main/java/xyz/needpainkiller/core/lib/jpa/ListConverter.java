package xyz.needpainkiller.core.lib.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

@Converter
public class ListConverter<T> extends BaseConverter implements AttributeConverter<List<T>, String> {


    @Override
    @SuppressWarnings("unchecked")
    public String convertToDatabaseColumn(List<T> attribute) {
        String value;
        try {
            value = mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            value = null;
        }
        return value;
    }

    @Override
    public List<T> convertToEntityAttribute(String dbData) {
        List<T> entity;
        if (dbData != null && !dbData.isEmpty()) {
            try {
                entity = mapper.readValue(dbData, List.class);
            } catch (JsonProcessingException e) {
                entity = null;
            }
        } else {
            entity = null;
        }
        return entity;
    }
}