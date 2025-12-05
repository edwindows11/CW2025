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
    private void startButtonClicked(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gameLayout.fxml"));
        Parent root = loader.load();
        GuiController c = loader.getController();
        new GameController(c);

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource())
                .getScene().getWindow();

        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void exitButtonClicked() {
        System.exit(0);
    }
}
