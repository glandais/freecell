package io.github.glandais.solitaire.klondike.printer.gui;

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
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import lombok.SneakyThrows;

import java.security.SecureRandom;
import java.util.*;

public class KlondikeGuiPrinter implements SolitairePrinter<KlondikePilesEnum> {

    private static final Random R = new SecureRandom();

    private static final Image BACK = new Image(Objects.requireNonNull(KlondikeGuiPrinter.class.getResourceAsStream("/images/back.png")));

    private static final Image WHITE = new Image(Objects.requireNonNull(KlondikeGuiPrinter.class.getResourceAsStream("/images/white.png")));

    private Board<KlondikePilesEnum> board;
    private List<MovementScore<KlondikePilesEnum>> moves;
    private float elapsed = 0.0f;
    private float movementDuration = 0.7f;
    int tick = -1;
    PrintableBoard printableBoardFrom = null;
    PrintableBoard printableBoardTo = null;
    PrintableBoard printableBoard = null;
    boolean paused = false;

    private boolean playable;
    private PlayableBoard playableBoard;

    private final Map<CardEnum, Image> cards;

    @SneakyThrows
    public KlondikeGuiPrinter(boolean playable) {
        this.playable = playable;
        print(Klondike.INSTANCE.getRandomBoard());
        cards = new EnumMap<>(CardEnum.class);
        for (CardEnum cardEnum : CardEnum.values()) {
            cards.put(cardEnum, getImage(cardEnum));
        }
        KlondikeGuiApplication.klondikeGuiPrinter = this;
        KlondikeGuiApplication.start();
    }

    @SneakyThrows
    public void awaitExit() {
        KlondikeGuiApplication.exitCdl.await();
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
        System.out.println(board);
        this.board = board;
        this.printableBoardFrom = null;
        this.printableBoardTo = null;
        this.printableBoard = new PrintableBoard(board);
        this.tick = -1;
        this.elapsed = 0.0f;
        if (playable) {
            playableBoard = new PlayableBoard(board, printableBoard);
        }
    }

    public void update(float v) {
        if (!playable) {
            updateReplay(v);
        }
    }

    public void keyReleased(KeyCode code) {
        if (code == KeyCode.SPACE) {
            paused = !paused;
        }
    }

    private void updateReplay(float v) {
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
                                speed.x,
                                speed.y + 5000 * v
                        );
                        printableCard.setSpeed(newSpeed);
                        Vector2 position = printableCard.getPosition();
                        Vector2 newPosition = new Vector2(
                                position.x + newSpeed.x * v,
                                position.y + newSpeed.y * v
                        );
                        if (newPosition.x < 0) {
                            newPosition.x = -newPosition.x;
                            newSpeed = new Vector2(
                                    -newSpeed.x,
                                    newSpeed.y
                            );
                            printableCard.setSpeed(newSpeed);
                        }
                        if (newPosition.x > Constants.X_BOUND) {
                            newPosition.x = newPosition.x - (newPosition.x - Constants.X_BOUND);
                            newSpeed = new Vector2(
                                    -newSpeed.x,
                                    newSpeed.y
                            );
                            printableCard.setSpeed(newSpeed);
                        }
                        if (newPosition.y < 0) {
                            newPosition.y = -newPosition.y;
                            newSpeed = new Vector2(
                                    newSpeed.x,
                                    -newSpeed.y
                            );
                            printableCard.setSpeed(newSpeed);
                        }
                        if (newPosition.y > Constants.Y_BOUND) {
                            newPosition.y = newPosition.y - (newPosition.y - Constants.Y_BOUND);
                            newSpeed = new Vector2(
                                    newSpeed.x * 0.9,
                                    -newSpeed.y * 0.9
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

    public void render(GraphicsContext renderer) {
        synchronized (this) {
            if (this.printableBoard != null) {
                for (PrintableCard printableCard : printableBoard.getStarts()) {
                    drawPrintableCard(renderer, printableCard);
                }
                for (PrintableCard printableCard : printableBoard) {
                    drawPrintableCard(renderer, printableCard);
                }
            }
        }
    }

    private void drawPrintableCard(GraphicsContext renderer, PrintableCard printableCard) {
        Image image = switch (printableCard.getFace()) {
            case FRONT -> cards.get(printableCard.getCard());
            case BACK -> BACK;
            case WHITE -> WHITE;
        };
        renderer.drawImage(image, printableCard.getPosition().x, printableCard.getPosition().y, Constants.CARD_WIDTH, Constants.CARD_HEIGHT);
    }

    @Override
    public void stop() {
        Platform.exit();
    }

    public void mouseClicked(double x, double y, int clickCount) {
        if (playable) {
            playableBoard.mouseClicked(x, y, clickCount);
        }
    }

    public void mousePressed(double x, double y) {
        if (playable) {
            playableBoard.mousePressed(x, y);
        }
    }

    public void mouseDragged(double x, double y) {
        if (playable) {
            playableBoard.mouseDragged(x, y);
        }
    }

    public void mouseReleased(double x, double y) {
        if (playable) {
            playableBoard.mouseReleased(x, y);
        }
    }

    public void undo() {
        if (playable) {
            playableBoard.undo();
        }
    }

    public void redo() {
        if (playable) {
            playableBoard.redo();
        }
    }
}
