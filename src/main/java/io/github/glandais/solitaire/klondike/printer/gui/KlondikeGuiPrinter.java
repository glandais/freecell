package io.github.glandais.solitaire.klondike.printer.gui;

import dev.aurumbyte.sypherengine.core.GameManager;
import dev.aurumbyte.sypherengine.core.SypherEngine;
import dev.aurumbyte.sypherengine.core.config.EngineConfig;
import dev.aurumbyte.sypherengine.core.graphics.Renderer;
import io.github.glandais.solitaire.common.Logger;
import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.cards.CardEnum;
import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.common.move.MovementScore;
import io.github.glandais.solitaire.common.printer.SolitairePrinter;
import io.github.glandais.solitaire.klondike.Klondike;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.serde.Serde;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import lombok.SneakyThrows;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class KlondikeGuiPrinter extends GameManager implements SolitairePrinter<KlondikePilesEnum> {

    private static final Image BACK = new Image(Objects.requireNonNull(KlondikeGuiPrinter.class.getResourceAsStream("/images/back.png")));

    private Board<KlondikePilesEnum> originalBoard;
    private Board<KlondikePilesEnum> board;
    private List<MovementScore<KlondikePilesEnum>> moves;
    private float elapsed = 0.0f;
    private float movementDuration = 0.7f;
    int tick = -1;
    PrintableBoard printableBoardFrom = null;
    PrintableBoard printableBoardTo = null;
    PrintableBoard printableBoard = null;
    boolean spacePressed = false;
    boolean paused = false;

    private final Map<CardEnum, Image> cards;

    private final CountDownLatch cdl = new CountDownLatch(1);

    @SneakyThrows
    public KlondikeGuiPrinter() {
        this.originalBoard = Klondike.INSTANCE.getRandomBoard();
        this.board = this.originalBoard.copy();
        this.moves = new ArrayList<>();
        cards = new EnumMap<>(CardEnum.class);
        for (CardEnum cardEnum : CardEnum.values()) {
            cards.put(cardEnum, getImage(cardEnum));
        }

        EngineConfig engineConfig = new EngineConfig();
        engineConfig.setWindowResolution(1280, 960);
        engineConfig.setTitle("klondike");

        SypherEngine.init(this, engineConfig);
        new Thread(() -> {
            SypherEngine.run();
            cdl.countDown();
            System.exit(0);
        }).start();
    }

    @SneakyThrows
    public void awaitExit() {
        cdl.await();
    }

    private Image getImage(CardEnum cardEnum) {
        String prefix = switch (cardEnum.getOrderEnum()) {
            case ACE -> "ace";
            case TWO -> "2";
            case THREE -> "3";
            case FOUR -> "4";
            case FIVE -> "5";
            case SIX -> "6";
            case SEVEN -> "7";
            case HEIGHT -> "8";
            case NINE -> "9";
            case TEN -> "10";
            case JACK -> "jack";
            case QUEEN -> "queen";
            case KING -> "king";
        };
        String suffix = cardEnum.getSuiteEnum().name().toLowerCase() + "s";
        String resource = "/images/" + prefix + "_of_" + suffix + ".png";
        return new Image(Objects.requireNonNull(KlondikeGuiPrinter.class.getResourceAsStream(resource)));
    }

    @Override
    public void init(SypherEngine sypherEngine) {
        sypherEngine.getRenderer().setBackgroundColor(Color.DARKGREEN);
    }

    @Override
    public void print(Board<KlondikePilesEnum> board) {
        synchronized (this) {
            this.moves = new ArrayList<>();
            reset(board);
        }
    }

    @Override
    public void printMovements(Board<KlondikePilesEnum> board, List<MovementScore<KlondikePilesEnum>> moves) {
        synchronized (this) {
            this.moves = moves;
            reset(board);
        }
    }

    private void reset(Board<KlondikePilesEnum> board) {
        this.originalBoard = board.copy();
        this.board = this.originalBoard.copy();
        this.printableBoardFrom = null;
        this.printableBoardTo = null;
        this.printableBoard = null;
        this.tick = -1;
        this.elapsed = 0.0f;
    }

    @Override
    public void update(float v) {
        boolean newSpacePressed = getInputHandler().keyListener.isDown(KeyCode.SPACE);
        if (spacePressed != newSpacePressed) {
            if (!newSpacePressed) {
                paused = !paused;
            }
        }
        spacePressed = newSpacePressed;

        if (paused) {
            return;
        }
        elapsed = elapsed + v;
        synchronized (this) {
            if (moves == null || moves.isEmpty()) {
                this.printableBoard = new PrintableBoard(board);
            } else {
                int newTick = (int) Math.round(Math.floor(elapsed / movementDuration));
                if (newTick != tick) {
                    int i = newTick % moves.size();
                    if (i == 0) {
                        // reset board
                        this.board = this.originalBoard.copy();
                        this.printableBoardTo = new PrintableBoard(this.board);
                    }
                    this.printableBoardFrom = this.printableBoardTo;
                    Logger.infoln(Serde.toJson(this.board));
                    Logger.infoln("possibleMovements");
                    List<Movement<KlondikePilesEnum>> possibleMovements = this.board.computePossibleMovements();
                    for (Movement<KlondikePilesEnum> possibleMovement : possibleMovements) {
                        Logger.infoln(possibleMovement);
                    }
                    Logger.infoln("appliedMovement");
                    Logger.infoln(moves.get(i));
                    this.board.applyMovement(moves.get(i));
                    this.printableBoardTo = new PrintableBoard(this.board);
                    tick = newTick;
                }
                float delta = (elapsed - (tick * movementDuration)) / movementDuration;
                this.printableBoard = this.printableBoardFrom.interpolate(this.printableBoardTo, delta);
            }
        }
    }

    @Override
    public void render(Renderer renderer) {
        synchronized (this) {
            if (this.printableBoard != null) {
                for (PrintableCard printableCard : printableBoard) {
                    if (printableCard.faceUp()) {
                        renderer.drawImage(cards.get(printableCard.card()), printableCard.position(), Constants.CARD_WIDTH, Constants.CARD_HEIGHT);
                    } else {
                        renderer.drawImage(BACK, printableCard.position(), Constants.CARD_WIDTH, Constants.CARD_HEIGHT);
                    }
                }
            }
        }
    }

    @Override
    public void stop() {
        Platform.exit();
    }

}
