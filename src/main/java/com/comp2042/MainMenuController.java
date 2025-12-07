package com.comp2042;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class MainMenuController {

    @FXML
    private javafx.scene.control.Slider volumeSlider;

    @FXML
    public void initialize() {
        if (volumeSlider != null) {
            volumeSlider.setValue(SoundManager.getInstance().getVolume() * 100);
            volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                SoundManager.getInstance().setVolume(newValue.doubleValue() / 100.0);
            });
        }
    }

    @FXML
    private void startButtonClicked(ActionEvent event) {
        try {
            SoundManager.getInstance().stopBackgroundMusic();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameLayout.fxml"));
            Parent root = loader.load();
            GuiController c = loader.getController();
            // c.setVolume(volumeSlider.getValue()); // Pass volume if needed, but
            // SoundManager is singleton

            GameController gameController = new GameController(c); // Use existing constructor if applicable, or just
                                                                   // c.init
            // The previous code had new GameController(c) which implies GameController
            // logic exists.
            // Warning: Original file didn't show GameController class but used it. Assuming
            // it works.

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
