package com.gemseeker.gemteleprompter;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * A simple teleprompter application with basic features such as font and font size
 * customization, scroll speed settings, animation speed setting, preview display
 * etc.
 *
 * @author Gem Seeker 06-15-2018
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainfxml.fxml"));
        MainScreenController mainScreenController;
        try {
            Pane pane = (Pane) loader.load();
            mainScreenController = loader.getController();
            Output outputFrame = new Output(mainScreenController);
            mainScreenController.setOutput(outputFrame);
            
            primaryStage.setOnCloseRequest((event) -> {
                mainScreenController.onCloseRequest();
            });
            
            primaryStage.setScene(new Scene(pane));
            primaryStage.show();
            
        } catch(IOException e) {
            System.err.println(e);
        }
    }
}
