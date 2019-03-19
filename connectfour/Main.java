
package com.internshala.connectfour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;

    @Override
        public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane grid=loader.load();
        controller = loader.getController();
        controller.createPlayground();
        MenuBar menubar=createmenu();
        menubar.prefWidthProperty().bind(primaryStage.widthProperty());
        Pane pane= (Pane) grid.getChildren().get(0);
        pane.getChildren().add(menubar);
        Scene scene = new Scene(grid);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four");
        primaryStage.setResizable(false);
        primaryStage.show();

    }
    public MenuBar createmenu()
    {
        Menu filemenu=new Menu("File");
        MenuItem newgame=new MenuItem("New Game");
        newgame.setOnAction(event -> controller.resetgame());
        SeparatorMenuItem s=new SeparatorMenuItem();
        MenuItem reset=new MenuItem("Reset Game");
        reset.setOnAction(event -> controller.resetgame());
        SeparatorMenuItem s1=new SeparatorMenuItem();
        MenuItem exit=new MenuItem("Exit Game");
        exit.setOnAction(event -> exitgame());
        filemenu.getItems().addAll(newgame,s,reset,s1,exit);
        Menu helpmenu= new Menu("Help");
        MenuItem about=new MenuItem("About");
        about.setOnAction(event -> about());
        helpmenu.getItems().addAll(about);
        MenuBar menubar=new MenuBar();
        menubar.getMenus().addAll(filemenu,helpmenu);
        return menubar;

    }

    private void about() {
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four Game");
        alert.setHeaderText("How to Play?");
        alert.setContentText("Connect Four is a two-player connection game"+
                "in which the players first choose a color and " +
                "then take turns dropping colored discs from the top"+
                " into a seven-column, six-row vertically suspended grid."+
                "The pieces fall straight down, occupying the next available "+
                "space within the column. The objective of the game is to be the first to"+
                "form a horizontal, vertical, or diagonal line of four of one's own discs."+
                "Connect Four is a solved game. The first player can always win by playing the"+
                "right moves.");
        alert.show();
    }

    private void exitgame() {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {

        launch(args);
    }
}
