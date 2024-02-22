package io.github.glandais.freecell.printer.gui;

import dev.aurumbyte.sypherengine.core.GameManager;
import dev.aurumbyte.sypherengine.core.SypherEngine;
import dev.aurumbyte.sypherengine.core.config.EngineConfig;
import dev.aurumbyte.sypherengine.core.graphics.Renderer;
import io.github.glandais.freecell.Logger;
import io.github.glandais.freecell.board.Board;
import io.github.glandais.freecell.board.MovementScore;
import io.github.glandais.freecell.cards.enums.CardEnum;
import io.github.glandais.freecell.printer.BoardPrinter;
import io.github.glandais.freecell.serde.Serde;
import io.github.glandais.freecell.serde.SerializableBoard;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.*;

public class BoardGuiPrinter extends GameManager implements BoardPrinter {

    private static final Image BACK = new Image(Objects.requireNonNull(BoardGuiPrinter.class.getResourceAsStream("/images/back.png")));

    private Board board;
    private List<MovementScore> movements;
    private float ellapsed = 0.0f;
    private float movementDuration = 0.7f;
    int tick = -1;
    PrintableBoard printableBoardFrom = null;
    PrintableBoard printableBoardTo = null;
    PrintableBoard printableBoard = null;
    boolean spacePressed = false;
    boolean paused = false;

    private Map<CardEnum, Image> cards;

    public BoardGuiPrinter() {
        this.board = new Board();
        this.movements = new ArrayList<>();
        cards = new EnumMap<>(CardEnum.class);
        for (CardEnum cardEnum : CardEnum.values()) {
            cards.put(cardEnum, getImage(cardEnum));
        }

        EngineConfig engineConfig = new EngineConfig();
        engineConfig.setWindowResolution(1280, 960);
        engineConfig.setTitle("freecell");

        SypherEngine.init(this, engineConfig);
        new Thread(SypherEngine::run).start();
    }

    private Image getImage(CardEnum cardEnum) {
        String prefix = switch (cardEnum.getCardOrderEnum()) {
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
        String suffix = cardEnum.getCardSuiteEnum().name().toLowerCase() + "s";
        String resource = "/images/" + prefix + "_of_" + suffix + ".png";
        return new Image(Objects.requireNonNull(BoardGuiPrinter.class.getResourceAsStream(resource)));
    }

    @Override
    public void init(SypherEngine sypherEngine) {
        sypherEngine.getRenderer().setBackgroundColor(Color.DARKGREEN);
    }

    @Override
    public void print(Board board) {
        synchronized (this) {
            this.movements = new ArrayList<>();
            reset(board);
        }
    }

    @Override
    public void printMovements(Board board, List<MovementScore> movements) {
        synchronized (this) {
            this.movements = movements;
            reset(board);
        }
    }

    private void reset(Board board) {
        this.board = board;
        this.printableBoardFrom = null;
        this.printableBoardTo = null;
        this.printableBoard = null;
        this.tick = -1;
        this.ellapsed = 0.0f;
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
        ellapsed = ellapsed + v;
        synchronized (this) {
            if (movements == null || movements.isEmpty()) {
                this.printableBoard = new PrintableBoard(board);
            } else {
                int newTick = (int) Math.round(Math.floor(ellapsed / movementDuration));
                if (newTick != tick) {
                    int i = newTick % movements.size();
                    if (i == 0) {
                        // reset board
                        this.board = new Board(this.board);
                        this.printableBoardTo = new PrintableBoard(this.board);
                    }
                    this.printableBoardFrom = this.printableBoardTo;
                    Logger.infoln(Serde.toJson(SerializableBoard.fromBoard(this.board)));
                    Logger.infoln("possibleMovements");
                    List<MovementScore> possibleMovements = this.board.getPossibleMovements();
                    for (MovementScore possibleMovement : possibleMovements) {
                        Logger.infoln(possibleMovement);
                    }
                    Logger.infoln("appliedMovement");
                    Logger.infoln(movements.get(i));
                    this.board.applyMovement(movements.get(i).movement());
                    this.printableBoardTo = new PrintableBoard(this.board);
                    tick = newTick;
                }
                float delta = (ellapsed - (tick * movementDuration)) / movementDuration;
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

}
