package io.github.glandais.solitaire.klondike.printer.gui;

import dev.aurumbyte.sypherengine.core.GameManager;
import dev.aurumbyte.sypherengine.core.SypherEngine;
import dev.aurumbyte.sypherengine.core.config.EngineConfig;
import dev.aurumbyte.sypherengine.core.graphics.Renderer;
import dev.aurumbyte.sypherengine.util.math.Vector2;
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

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class KlondikeGuiPrinter extends GameManager implements SolitairePrinter<KlondikePilesEnum> {

    private static final Random R = new SecureRandom();

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
        printMovements(Klondike.INSTANCE.getRandomBoard(), new ArrayList<>());
        cards = new EnumMap<>(CardEnum.class);
        for (CardEnum cardEnum : CardEnum.values()) {
            cards.put(cardEnum, getImage(cardEnum));
        }

        EngineConfig engineConfig = new EngineConfig();
        engineConfig.setWindowResolution(Constants.WIDTH, Constants.HEIGHT);
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
        this.board = board;
        this.originalBoard = board.copy();
        this.printableBoardFrom = null;
        this.printableBoardTo = null;
        this.printableBoard = new PrintableBoard(board);
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
                    tick = newTick;
                    if (newTick < moves.size()) {
                        this.printableBoardFrom = this.printableBoardTo;
                        Logger.infoln(Serde.toJson(this.board));
                        Logger.infoln("possibleMovements");
                        List<Movement<KlondikePilesEnum>> possibleMovements = this.board.computePossibleMovements();
                        for (Movement<KlondikePilesEnum> possibleMovement : possibleMovements) {
                            Logger.infoln(possibleMovement);
                        }
                        Logger.infoln("appliedMovement");
                        Logger.infoln(moves.get(tick));
                        this.board.applyMovement(moves.get(tick));
                        this.printableBoardTo = new PrintableBoard(this.board);
                    } else if (newTick == moves.size()) {
                        this.printableBoard = new PrintableBoard(board);
                        for (PrintableCard printableCard : this.printableBoard) {
                            printableCard.setSpeed(new Vector2(R.nextFloat(-500, 500), R.nextFloat(-500, 500)));
                        }
                    }
                }
                if (tick >= moves.size()) {
                    for (PrintableCard printableCard : this.printableBoard) {
                        Vector2 speed = printableCard.getSpeed();
                        Vector2 newSpeed = new Vector2(
                                speed.xPos,
                                speed.yPos + 5000f * v
                        );
                        printableCard.setSpeed(newSpeed);
                        Vector2 position = printableCard.getPosition();
                        Vector2 newPosition = new Vector2(
                                position.xPos + newSpeed.xPos * v,
                                position.yPos + newSpeed.yPos * v
                        );
                        if (newPosition.xPos < 0) {
                            newPosition.xPos = -newPosition.xPos;
                            newSpeed = new Vector2(
                                    -newSpeed.xPos,
                                    newSpeed.yPos
                            );
                            printableCard.setSpeed(newSpeed);
                        }
                        if (newPosition.xPos > Constants.X_BOUND) {
                            newPosition.xPos = newPosition.xPos - (newPosition.xPos - Constants.X_BOUND);
                            newSpeed = new Vector2(
                                    -newSpeed.xPos,
                                    newSpeed.yPos
                            );
                            printableCard.setSpeed(newSpeed);
                        }
                        if (newPosition.yPos < 0) {
                            newPosition.yPos = -newPosition.yPos;
                            newSpeed = new Vector2(
                                    newSpeed.xPos,
                                    -newSpeed.yPos
                            );
                            printableCard.setSpeed(newSpeed);
                        }
                        if (newPosition.yPos > Constants.Y_BOUND) {
                            newPosition.yPos = newPosition.yPos - (newPosition.yPos - Constants.Y_BOUND);
                            newSpeed = new Vector2(
                                    newSpeed.xPos * 0.9f,
                                    -newSpeed.yPos * 0.9f
                            );
                            printableCard.setSpeed(newSpeed);
                        }
                        printableCard.setPosition(newPosition);
                    }
                } else if (this.printableBoardFrom != null && this.printableBoardTo != null) {
                    float delta = (elapsed - (tick * movementDuration)) / movementDuration;
                    this.printableBoard = this.printableBoardFrom.interpolate(this.printableBoardTo, delta);
                }
            }
        }
    }

    @Override
    public void render(Renderer renderer) {
        synchronized (this) {
            if (this.printableBoard != null) {
                for (PrintableCard printableCard : printableBoard) {
                    if (printableCard.isFaceUp()) {
                        renderer.drawImage(cards.get(printableCard.getCard()), printableCard.getPosition(), Constants.CARD_WIDTH, Constants.CARD_HEIGHT);
                    } else {
                        renderer.drawImage(BACK, printableCard.getPosition(), Constants.CARD_WIDTH, Constants.CARD_HEIGHT);
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
