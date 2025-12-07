package com.comp2042;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.effect.Reflection;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GridPane ghostPanel;

    @FXML
    private GridPane nextBrickPanel;

    @FXML
    private GridPane holdPanel;

    @FXML
    private VBox pauseMenu;

    @FXML
    private BorderPane gameBoard;

    @FXML
    private GameOverPanel gameOverPanel;

    private Rectangle[][] displayMatrix;
    private InputEventListener eventListener;
    private Rectangle[][] rectangles;
    private Rectangle[][] ghostRectangles;
    private Rectangle[][] holdRectangles;
    private java.util.List<Rectangle[][]> nextRectangles = new java.util.ArrayList<>();
    private Timeline timeLine;

    // Optimization: Cache DropShadows to avoid creating them every frame
    private final DropShadow[] shadowCache = new DropShadow[8];
    private final DropShadow ghostShadow;

    {
        // Initialize ghost shadow
        ghostShadow = new DropShadow();
        ghostShadow.setColor(Color.web("#ffffff", 0.5));
        ghostShadow.setRadius(10);
        ghostShadow.setSpread(0.2);
    }

    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                // Toggle pause with P key
                if (keyEvent.getCode() == KeyCode.P) {
                    togglePause();
                    keyEvent.consume();
                    return;
                }

                if (!isPause.get() && !isGameOver.get()) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.C) {
                        refreshBrick(eventListener.onHoldEvent());
                        keyEvent.consume();
                    }
                }

                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }
            }
        });
        gameOverPanel.setVisible(false);
        pauseMenu.setVisible(false);

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
    }

    private void togglePause() {
        if (isGameOver.get())
            return;

        isPause.set(!isPause.get());
        if (isPause.get()) {
            timeLine.pause();
            pauseMenu.setVisible(true);
        } else {
            timeLine.play();
            pauseMenu.setVisible(false);
            gamePanel.requestFocus();
        }
    }

    @FXML
    private void resumeGame(ActionEvent event) {
        togglePause();
    }

    @FXML
    private void backToMenu(ActionEvent event) {
        try {
            timeLine.stop();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainMenu.fxml"));
            Parent root = loader.load();

            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene()
                    .getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        brickPanel.setLayoutX(
                gameBoard.getLayoutX() + gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap()
                        + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(
                -42 + gameBoard.getLayoutY() + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap()
                        + brick.getyPosition() * BRICK_SIZE);

        ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                ghostRectangles[i][j] = rectangle;
                ghostPanel.add(rectangle, j, i);
            }
        }

        // Initialize Next Piece Rectangles (3 pieces)
        nextRectangles.clear();
        for (int k = 0; k < 3; k++) {
            Rectangle[][] nextMatrix = new Rectangle[4][4];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                    rectangle.setFill(Color.TRANSPARENT);
                    nextMatrix[i][j] = rectangle;
                    // proper spacing: each piece takes 4 rows, plus 1 row gap -> offset = k * 5
                    nextBrickPanel.add(rectangle, j, i + (k * 5));
                }
            }
            nextRectangles.add(nextMatrix);
        }

        holdRectangles = new Rectangle[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                holdRectangles[i][j] = rectangle;
                holdPanel.add(rectangle, j, i);
            }
        }

        timeLine = new Timeline(
                new KeyFrame(Duration.millis(400), ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();

        // Render the initial state of next/hold bricks
        refreshBrick(brick);
    }

    private Paint getFillColor(int i) {
        switch (i) {
            case 0:
                return Color.TRANSPARENT;
            case 1:
                return Color.AQUA;
            case 2:
                return Color.BLUEVIOLET;
            case 3:
                return Color.DARKGREEN;
            case 4:
                return Color.YELLOW;
            case 5:
                return Color.RED;
            case 6:
                return Color.BEIGE;
            case 7:
                return Color.BURLYWOOD;
            default:
                return Color.WHITE;
        }
    }

    private void refreshBrick(ViewData brick) {
        if (!isPause.get()) {
            brickPanel.setLayoutX(gameBoard.getLayoutX() + gamePanel.getLayoutX()
                    + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(-42 + gameBoard.getLayoutY() + gamePanel.getLayoutY()
                    + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                    setGhostRectangleData(brick.getBrickData()[i][j], ghostRectangles[i][j]);
                }
            }
            // Update Ghost Panel Layout
            ghostPanel.setLayoutX(gameBoard.getLayoutX() + gamePanel.getLayoutX()
                    + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            ghostPanel.setLayoutY(-42 + gameBoard.getLayoutY() + gamePanel.getLayoutY()
                    + brick.getGhostYPosition() * brickPanel.getHgap() + brick.getGhostYPosition() * BRICK_SIZE);

            // Update Hold Panel
            if (brick.getHoldBrickData() != null) {
                for (int i = 0; i < brick.getHoldBrickData().length; i++) {
                    for (int j = 0; j < brick.getHoldBrickData()[i].length; j++) {
                        setRectangleData(brick.getHoldBrickData()[i][j], holdRectangles[i][j]);
                    }
                }
            }
        }

        // Update Next Panels
        java.util.List<int[][]> nextDataList = brick.getNextBrickData();
        for (int k = 0; k < nextDataList.size() && k < nextRectangles.size(); k++) {
            int[][] nextData = nextDataList.get(k);
            Rectangle[][] nextRects = nextRectangles.get(k);
            for (int i = 0; i < nextData.length; i++) {
                for (int j = 0; j < nextData[i].length; j++) {
                    setRectangleData(nextData[i][j], nextRects[i][j]);
                }
            }
        }
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
        if (color != 0) {
            if (shadowCache[color] == null) {
                DropShadow dropShadow = new DropShadow();
                dropShadow.setColor((Color) getFillColor(color));
                dropShadow.setRadius(10);
                dropShadow.setSpread(0.4);
                shadowCache[color] = dropShadow;
            }
            rectangle.setEffect(shadowCache[color]);
        } else {
            rectangle.setEffect(null);
        }
    }

    private void setGhostRectangleData(int color, Rectangle rectangle) {
        if (color == 0) {
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setEffect(null);
        } else {
            // Use a semi-transparent gray or matching color
            Color ghostColor = Color.web("#ffffff", 0.3);
            rectangle.setFill(ghostColor);

            rectangle.setEffect(ghostShadow);
        }
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    private void moveDown(MoveEvent event) {
        if (!isPause.get()) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel(
                        "+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    @FXML
    private Label scoreLabel;

    @FXML
    private Label linesLabel;

    @FXML
    private Label levelLabel;

    public void bindScore(IntegerProperty integerProperty) {
        scoreLabel.textProperty().bind(integerProperty.asString());
    }

    public void bindLines(IntegerProperty integerProperty) {
        linesLabel.textProperty().bind(integerProperty.asString());
    }

    public void bindLevel(IntegerProperty integerProperty) {
        levelLabel.textProperty().bind(integerProperty.asString());
    }

    public void updateGameSpeed(int level) {
        if (timeLine != null) {
            timeLine.stop();
        }
        // Formula: Speed decreases as level increases. Max level 10 -> ~100ms.
        double delay = Math.max(100, 400 - (level - 1) * 33);
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(delay),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        if (!isPause.get() && !isGameOver.get()) {
            timeLine.play();
        }
    }

    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    public void pauseGame(ActionEvent actionEvent) {
        togglePause();
        gamePanel.requestFocus();
    }

}
