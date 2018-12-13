package playing;

import authoringInterface.View;
import gameplay.GameData;
import gameplay.Initializer;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import social.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Optional;

public class MainPlayer {

    private Initializer myInitializer;
    private Stage myStage;
    private User myUser;
    private File myFile;
    private String myReferencePath;
    private String myName;

    public MainPlayer(User user, String referencePath, String gameName) {
        myName = gameName;
        myUser = user;
        myReferencePath = referencePath;
        System.out.println("Reference path is " + myReferencePath);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Start Game");
        ButtonType loadButton = new ButtonType("Continue");
        ButtonType newGameButton = new ButtonType("New Game");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(loadButton, newGameButton, cancelButton);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == loadButton) {
            if (myUser != null) {
                String xmlString = myUser.getGameState(myReferencePath);
                if (xmlString.equals("")) {
                    myFile = getNewGameFile();
                } else {
                    try {
                        myFile = new File(getClass().getClassLoader().getResource("GameProgress.xml").getFile());
                        FileWriter fileWriter = new FileWriter(myFile);
                        fileWriter.write(xmlString);
                        fileWriter.close();
                    } catch (Exception e) { }
                }
            } else { // myUser is null
                myFile = getNewGameFile();
            }
        } else if (result.get() == newGameButton) {
            myFile = getNewGameFile();
        }
        launchGame();
    }

    public static void main(String[] args) {
        //launch(args);
    }

    private File getNewGameFile() {
        try{
            return new File(myReferencePath);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void launchGame() {
        if (myUser != null) myUser.tweet(String.format("Currently playing %s!", myName));
        System.out.println("myFile is " + myFile);
        myInitializer = new Initializer(myFile);
        myStage = new Stage();
        myInitializer.setScreenSize(700, 500);
        Scene newScene = new Scene(myInitializer.getRoot(), View.GAME_WIDTH, View.GAME_HEIGHT);
        /*Button saveButton = new Button("Save state");
        saveButton.setLayoutY(View.GAME_HEIGHT);
        saveButton.setMinHeight(50);
        saveButton.setMinWidth(View.GAME_WIDTH);
        saveButton.setOnMouseClicked(e -> {
            try {
                // TODO: saveGameData Needs to be fixed
                myUser.saveGameState(myReferencePath, GameData.saveGameData());
            } catch (Exception ex) {
            }
        });
        myInitializer.getRoot().getChildren().add(saveButton);*/
        myStage.setScene(newScene);
        myStage.show();
    }
}
