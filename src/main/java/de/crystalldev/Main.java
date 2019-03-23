package de.crystalldev;

import de.crystalldev.models.PlayerPlanet;
import de.crystalldev.util.Utility;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

    private Stage window;
    private Scene loginScene, mainScene, settingsScene;
    private ArrayList<Button> buttons = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        this.setupLoginScene();
        this.showLoginScene();
        //TODO reset all stuff on relogin
    }

    private void setupLoginScene() {
        // Declaring variables for loginScene
        HBox loginSceneLayout = new HBox(20);
        Label emailLabel = new Label("E-Mail:");
        TextField email = new TextField("");
        Label passwordLabel = new Label("Password:");
        PasswordField password = new PasswordField();
        password.setPromptText("Your password");
        Label serverLabel = new Label("Server:");
        TextField serverTextField = new TextField("Yildun");
        Label ingameNameLabel = new Label("User Name:");
        TextField ingameNameTextField = new TextField("Crystall");
        Button loginButton = new Button("Login");

        loginSceneLayout.getChildren().addAll(emailLabel, email, passwordLabel, password, serverLabel, serverTextField, ingameNameLabel, ingameNameTextField, loginButton);
        loginScene = new Scene(loginSceneLayout);

        loginButton.setOnAction(event -> {
            Utility.eMail = email.getText().trim();
            Utility.password = password.getText().trim();
            BrowserManager.getInstance().loginLobby(Utility.eMail, Utility.password);
            Utility.sleep(1000);
            Utility.server = serverTextField.getText().trim();
            Utility.userName = ingameNameTextField.getText().trim();
            BrowserManager.getInstance().loginUniverse(Utility.server, Utility.userName);
            Utility.playerPlanets = BrowserManager.getInstance().getAccountPlanets();
            Utility.sleep(1000);
            this.setupMainScene();
            this.showMainScene();
        });
    }

    private void setupMainScene() {
        //Declaring variables for mainScene
        HBox mainSceneLayout = new HBox(80);

        for (PlayerPlanet planet : Utility.playerPlanets) {
            Button planetButton = new Button(planet.getCoordinates().toString());

            planetButton.setOnAction(event -> {
                Utility.activePlanet = planet.getId();
                BrowserManager.getInstance().setRunning(true);
                new Thread(() -> BrowserManager.getInstance().refresh(planet.getId())).start();
                mainSceneLayout.getChildren().removeAll(buttons);
                Button turnOff = new Button("Off");
                Button pause = new Button("Pause");
                mainSceneLayout.getChildren().addAll(turnOff, pause);

                turnOff.setOnAction(e -> {
                    BrowserManager.getInstance().setRunning(false);
                    mainSceneLayout.getChildren().addAll(buttons);
                    mainSceneLayout.getChildren().removeAll(turnOff, pause);
                });
            });
            buttons.add(planetButton);
        }

        Button scanGalaxyButton = new Button("Scan Galaxy");
        Button parseEspionageMessagesButton = new Button("Parse Espionage Messages");
        Button refresh = new Button("Refresh");

//        startFarmBotButton.setOnAction(event -> {
//            Utility.activePlanet = "34016475";
//            BrowserManager.getInstance().setRunning(true);
//            new Thread(() -> new Thread(() -> {
//                BrowserManager.getInstance().refresh("33620855");
//            }).start()).start();
//            mainSceneLayout.getChildren().removeAll(startFarmBotButton, startFarmBotPlani2, startFarmBotPlani3, scanGalaxyButton, parseEspionageMessagesButton);
//            Button turnOff = new Button("Off");
//            Button pause = new Button("Pause");
//            mainSceneLayout.getChildren().addAll(turnOff, pause);
//
//            turnOff.setOnAction(event15 -> {
//                BrowserManager.getInstance().setRunning(false);
//                mainSceneLayout.getChildren().addAll(startFarmBotButton, startFarmBotPlani2, startFarmBotPlani3, scanGalaxyButton, parseEspionageMessagesButton);
//                mainSceneLayout.getChildren().removeAll(turnOff, pause);
//            });
//        });
//
//        startFarmBotPlani2.setOnAction(event -> {
//            Utility.activePlanet = "33816463";
//            BrowserManager.getInstance().setRunning(true);
//            new Thread(() -> new Thread(() -> {
//                BrowserManager.getInstance().espionageFarming("33816463");
//            }).start()).start();
//            mainSceneLayout.getChildren().removeAll(startFarmBotButton, startFarmBotPlani2, startFarmBotPlani3, scanGalaxyButton, parseEspionageMessagesButton);
//            Button turnOff = new Button("Off");
//            Button pause = new Button("Pause");
//            mainSceneLayout.getChildren().addAll(turnOff, pause);
//
//            turnOff.setOnAction(event14 -> {
//                BrowserManager.getInstance().setRunning(false);
//                mainSceneLayout.getChildren().addAll(startFarmBotButton, startFarmBotPlani2, startFarmBotPlani3, scanGalaxyButton, parseEspionageMessagesButton);
//                mainSceneLayout.getChildren().removeAll(turnOff, pause);
//            });
//
//            pause.setOnAction(event1 -> BrowserManager.getInstance().setPaused(!BrowserManager.getInstance().isPaused()));
//
//        });
//
//        startFarmBotPlani3.setOnAction(event -> {
//            Utility.activePlanet = "33816344";
//            BrowserManager.getInstance().setRunning(true);
//            new Thread(() -> new Thread(() -> {
//                BrowserManager.getInstance().espionageFarming("33816344");
//            }).start()).start();
//            mainSceneLayout.getChildren().removeAll(startFarmBotButton, startFarmBotPlani2, startFarmBotPlani3, scanGalaxyButton, parseEspionageMessagesButton);
//            Button turnOff = new Button("Off");
//            Button pause = new Button("Pause");
//            mainSceneLayout.getChildren().addAll(turnOff, pause);
//
//            turnOff.setOnAction(event13 -> {
//                BrowserManager.getInstance().setRunning(false);
//                mainSceneLayout.getChildren().addAll(startFarmBotButton, startFarmBotPlani2, startFarmBotPlani3, scanGalaxyButton, parseEspionageMessagesButton);
//                mainSceneLayout.getChildren().removeAll(turnOff, pause);
//            });
//
//
//            pause.setOnAction(event12 -> BrowserManager.getInstance().setPaused(!BrowserManager.getInstance().isPaused()));
//
//        });

        scanGalaxyButton.setOnAction(event -> BrowserManager.getInstance().scanGalaxy(1, 1, 499, Utility.activePlanet));
        parseEspionageMessagesButton.setOnAction(event -> BrowserManager.getInstance().parseEspionageMessages());
        refresh.setOnAction(event -> BrowserManager.getInstance().refresh(Utility.activePlanet));
        mainSceneLayout.getChildren().addAll(buttons);
        mainScene = new Scene(mainSceneLayout);
    }

    private void setupSettingsScene() {
        //TODO
    }

    private void showLoginScene() {
        window.setScene(loginScene);
        window.show();
    }

    private void showMainScene() {
        window.setScene(mainScene);
        window.show();
    }

    private void showSettingsScene() {
        //TODO
    }
}
