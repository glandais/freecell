package io.github.glandais.freecell.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class Serde {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public static void save(BoardMovements boardMovements) {
        objectMapper.writeValue(new File("board.json"), boardMovements);
    }

    @SneakyThrows
    public static BoardMovements load() {
        return objectMapper.readValue(new File("board.json"), BoardMovements.class);
    }
}
