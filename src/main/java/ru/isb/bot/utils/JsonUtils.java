package ru.isb.bot.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @SneakyThrows
    public static <T> T parseStringJsonToObject(String content, Class<T> clazz) {
        return mapper.readValue(content, clazz);
    }

    @SneakyThrows
    public static String parseObjectToString(Object object) {
        return mapper.writeValueAsString(object);
    }
}
