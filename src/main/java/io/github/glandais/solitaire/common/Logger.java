package io.github.glandais.solitaire.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Logger {

    public static final boolean DEBUG = false;

    public void debug(Object s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }

    public void info(Object s) {
        System.out.print(s);
    }

    public void infoln() {
        System.out.println();
    }

    public void infoln(Object s) {
        System.out.println(s);
    }

}
