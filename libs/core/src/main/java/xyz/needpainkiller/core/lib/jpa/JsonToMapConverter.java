package xyz.needpainkiller.core.lib.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Converter
public class JsonToMapConverter extends BaseConverter implements AttributeConverter<Map<String, Serializable>, String> {

    @Override
    @SuppressWarnings("unchecked")
    public String convertToDatabaseColumn(Map<String, Serializable> attribute) {
        String value;
        try {
            value = mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            value = null;
        }
        return value;
    }

    @Override
    public Map<String, Serializable> convertToEntityAttribute(String dbData) {
        Map<String, Serializable> entity;
        if (dbData != null && !dbData.isEmpty()) {
            try {
                entity = mapper.readValue(dbData, HashMap.class);
            } catch (JsonProcessingException e) {
                entity = null;
            }
        } else {
            entity = null;
        }
        return entity;
    }
}