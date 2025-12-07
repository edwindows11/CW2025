package com.comp2042;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class GameOverPanel extends BorderPane {

    private final Button playAgainButton;

    public GameOverPanel() {
        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");

        playAgainButton = new Button("Play Again");
        playAgainButton.getStyleClass().add("playAgainButton");

        VBox content = new VBox(20); // 20px spacing
        content.setAlignment(Pos.CENTER);
        content.setPrefWidth(200);
        content.setMaxWidth(200);
        content.getChildren().addAll(gameOverLabel, playAgainButton);

        setCenter(content);
    }

    public void setPlayAgainAction(EventHandler<ActionEvent> handler) {
        playAgainButton.setOnAction(handler);
    }

}
