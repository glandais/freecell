package io.github.glandais.solitaire.common;

import io.github.glandais.solitaire.common.cards.CardEnum;
import io.github.glandais.solitaire.common.cards.ColorEnum;
import io.github.glandais.solitaire.common.move.MovableStack;
import io.github.glandais.solitaire.common.move.Move;
import io.github.glandais.solitaire.common.move.MovementScore;
import io.github.glandais.solitaire.common.solver.States;
import lombok.experimental.UtilityClass;
import org.fusesource.jansi.Ansi;

import java.util.Collection;

import static org.fusesource.jansi.Ansi.ansi;

@UtilityClass
public class Logger {

    public static final boolean DEBUG = false;

    public void debug(Object s) {
        if (DEBUG) {
            infoln(s);
        }
    }

    public void info(Object s) {
        if (s instanceof String str) {
            System.out.print(str);
        } else if (s instanceof CardEnum cardEnum) {
            String label = cardEnum.getSuiteEnum().getLabel() + cardEnum.getOrderEnum().getLabel();
            info(ansi().bgBright(Ansi.Color.WHITE).fgBright(cardEnum.getColorEnum() == ColorEnum.RED ? Ansi.Color.RED : Ansi.Color.BLACK).a(label).reset().toString());
        } else if (s instanceof Collection<?> collection) {
            info("[");
            boolean first = true;
            for (Object o : collection) {
                if (!first) {
                    info(",");
                }
                info(o);
                first = false;
            }
            info("]");
        } else if (s instanceof MovableStack<?> movableStack) {
            info(movableStack.from());
            info(" ");
            info(movableStack.cards());
        } else if (s instanceof MovementScore<?> movement) {
            info(movement.getFrom());
            info("->");
            info(movement.getTo());
            info(" ");
            info(movement.getCards());
            info(" ");
            info(movement.getScore());
            info(" ");
            info(movement.getDebug());
        } else if (s instanceof Move<?> movement) {
            info(movement.getFrom());
            info("->");
            info(movement.getTo());
            info(" ");
            info(movement.getCards());
        } else if (s instanceof States<?> states) {
            infoln("statesSize : " + states.getStatesSize());
            infoln("statesPut : " + states.getStatesPut());
            infoln("statesPresent : " + states.getStatesPresent());
            info("statesRemoved : " + states.getStatesRemoved());
        } else {
            System.out.print(s);
        }
    }

    public void infoln() {
        System.out.println();
    }

    public void infoln(Object s) {
        info(s);
        infoln();
    }

}
