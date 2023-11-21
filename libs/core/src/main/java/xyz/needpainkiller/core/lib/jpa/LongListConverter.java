package xyz.needpainkiller.core.lib.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.Converter;

import java.util.List;

@Converter
public class LongListConverter extends ListConverter<Long> {


    @Override
    @SuppressWarnings("unchecked")
    public String convertToDatabaseColumn(List<Long> attribute) {
        String value;
        try {
            value = mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            value = null;
        }
        return value;
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        List<Integer> integerList;
        List<Long> entity;
        if (dbData != null && !dbData.isEmpty()) {
            try {
                integerList = mapper.readValue(dbData, List.class);
                entity = integerList.stream().map(Integer::longValue).toList();
            } catch (JsonProcessingException e) {
                entity = null;
            }
        } else {
            entity = null;
        }
        return entity;
    }
}