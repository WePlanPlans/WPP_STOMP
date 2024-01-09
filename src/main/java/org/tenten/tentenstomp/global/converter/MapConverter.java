package org.tenten.tentenstomp.global.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

@Converter
public class MapConverter implements AttributeConverter<Map, String> {
    protected final ObjectMapper objectMapper;

    public MapConverter() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String convertToDatabaseColumn(Map map) {
        if (ObjectUtils.isEmpty(map)) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(map); // 3
        } catch (Exception e) {
            throw new RuntimeException(e);
        }    }

    @Override
    public Map<String, Long> convertToEntityAttribute(String s) {

        if (StringUtils.hasText(s)) {
            try {
                return objectMapper.readValue(s, Map.class); // 5
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
