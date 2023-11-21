package xyz.needpainkiller.core.lib.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.Converter;

import java.util.List;

@Converter
public class IntegerListConverter extends ListConverter<Integer> {


    @Override
    @SuppressWarnings("unchecked")
    public String convertToDatabaseColumn(List<Integer> attribute) {
        String value;
        try {
            value = mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            value = null;
        }
        return value;
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        List<Integer> entity;
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