package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Load the digital font
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);

        URL location = getClass().getClassLoader().getResource("mainMenu.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Parent root = fxmlLoader.load();

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        SoundManager.getInstance().playBackgroundMusic("menu.wav");
    }

    @Override
    public void stop() throws Exception {
        SoundManager.getInstance().stopBackgroundMusic();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}