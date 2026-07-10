package com.procureiq.springboot_app.shared.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.postgresql.util.PGobject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Converter(autoApply = false)
public class JsonMapConverter implements AttributeConverter<Map<String, Object>, Object> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            PGobject po = new PGobject();
            po.setType("jsonb");
            po.setValue(objectMapper.writeValueAsString(attribute));
            return po;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(Object dbData) {
        if (dbData == null) {
            return new HashMap<>();
        }
        try {
            String value = dbData instanceof PGobject ? ((PGobject) dbData).getValue() : dbData.toString();
            return objectMapper.readValue(value, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            return new HashMap<>();
        }
    }
}
