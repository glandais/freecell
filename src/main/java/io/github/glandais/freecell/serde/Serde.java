package io.github.glandais.freecell.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class Serde {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public static void save(String fileName, Object o) {
        objectMapper.writeValue(new File(fileName), o);
    }

    @SneakyThrows
    public static String toJson(Object o) {
        return objectMapper.writeValueAsString(o);
    }

    @SneakyThrows
    public static <T> T load(String fileName, Class<T> clazz) {
        return objectMapper.readValue(new File(fileName), clazz);
    }
}
