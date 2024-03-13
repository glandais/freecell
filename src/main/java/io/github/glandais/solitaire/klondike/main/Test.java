package io.github.glandais.solitaire.klondike.main;

import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
        long[] a1 = new long[] { 12, 847, 5121, 15151};
        long[] a2 = new long[] { 12, 847, 5221, 15151};
        System.out.println(Arrays.hashCode(a1));
        System.out.println(Arrays.hashCode(a2));
    }

}
