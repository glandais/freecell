package io.github.glandais.solitaire.common.solver;

import java.util.Arrays;

public record ByteArray(byte[] array) {

    @Override
    public boolean equals(Object o) {
        ByteArray byteArray = (ByteArray) o;
        return Arrays.equals(array, byteArray.array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }
}
