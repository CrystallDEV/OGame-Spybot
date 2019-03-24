package de.crystalldev;

import de.crystalldev.Models.PlayerPlanet;
import de.crystalldev.Util.Settings;
import de.crystalldev.Util.Utility;
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
        window.setTitle(Settings.applicationTitle);
        window.setHeight(200);
        this.setupLoginScene();
        this.showLoginScene();
        //TODO reset all stuff on relogin
    }

    private void setupLoginScene() {
        // Declaring variables for loginScene
        HBox loginSceneLayout = new HBox(10);
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
            Settings.eMail = email.getText().trim();
            Settings.password = password.getText().trim();
            BrowserManager.getInstance().loginLobby(Settings.eMail, Settings.password);
            Utility.sleep(1000);
            Settings.server = serverTextField.getText().trim();
            Settings.userName = ingameNameTextField.getText().trim();
            BrowserManager.getInstance().loginUniverse(Settings.server, Settings.userName);
            Settings.playerPlanets = BrowserManager.getInstance().getAccountPlanets();
            Settings.activePlanet = Settings.playerPlanets.get(0);
            Utility.sleep(1000);
            this.setupMainScene();
            this.showMainScene();
        });
    }

    private void setupMainScene() {
        //Declaring variables for mainScene
        HBox mainSceneLayout = new HBox(10);

        for (PlayerPlanet planet : Settings.playerPlanets) {
            Button planetButton = new Button(planet.getCoordinates().toString());
            planetButton.setOnAction(event -> {
                Settings.activePlanet = planet;
                BrowserManager.getInstance().setRunning(true);
                new Thread(() -> BrowserManager.getInstance().espionageFarming(planet.getId())).start();

                mainSceneLayout.getChildren().removeAll(buttons);
                Button turnOff = new Button("Off");
                Button pause = new Button("Pause");
                mainSceneLayout.getChildren().addAll(turnOff, pause);

                turnOff.setOnAction(e -> {
                    BrowserManager.getInstance().setRunning(false);
                    mainSceneLayout.getChildren().addAll(buttons);
                    mainSceneLayout.getChildren().removeAll(turnOff, pause);
                });

                pause.setOnAction(event1 -> BrowserManager.getInstance().setPaused(!BrowserManager.getInstance().isPaused()));
            });
            buttons.add(planetButton);
        }

        Button scanGalaxyButton = new Button("Scan Galaxy");
        scanGalaxyButton.setOnAction(event -> {
            new Thread(() -> BrowserManager.getInstance().scanGalaxy(Settings.activePlanet.getCoordinates().getGalaxy(),
                    Settings.LOWER_SYSTEM, Settings.UPPER_SYSTEM, Settings.activePlanet.getId())
            ).start();
        });
        buttons.add(scanGalaxyButton);

        Button parseEspionageMessagesButton = new Button("Parse Espionage Messages");
        parseEspionageMessagesButton.setOnAction(event -> {
            new Thread(() -> BrowserManager.getInstance().parseEspionageMessages()).start();
        });
        buttons.add(parseEspionageMessagesButton);

        Button refresh = new Button("Refresh");
        refresh.setOnAction(event -> {
            new Thread(() -> BrowserManager.getInstance().refresh(Settings.activePlanet.getId())).start();
        });
        buttons.add(refresh);

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
