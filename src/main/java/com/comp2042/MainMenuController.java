package com.comp2042;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class MainMenuController {

    @FXML
    public void initialize() {
        // Optional: any setup code
    }

    @FXML
    private void startButtonClicked(ActionEvent event) {
        try {
            SoundManager.getInstance().stopBackgroundMusic();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameLayout.fxml"));
            Parent root = loader.load();
            GuiController c = loader.getController();
            new GameController(c);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource())
                    .getScene().getWindow();

            boolean isFullScreen = stage.isFullScreen();
            boolean isMaximized = stage.isMaximized();

            stage.setScene(new Scene(root));

            if (isFullScreen) {
                stage.setFullScreen(true);
            } else if (isMaximized) {
                stage.setMaximized(true);
            }

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exitButtonClicked() {
        System.exit(0);
    }
}
