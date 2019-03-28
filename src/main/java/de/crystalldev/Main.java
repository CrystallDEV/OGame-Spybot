package de.crystalldev;

import de.crystalldev.Models.PlayerPlanet;
import de.crystalldev.Util.Settings;
import de.crystalldev.Util.Utility;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Objects;

public class Main extends Application {

    private Stage window;
    private Scene loginScene, mainScene, settingsScene;
    private ArrayList<Button> buttons = new ArrayList<>();
    private boolean isScanning = false;
    private Thread scanThread = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle(Settings.applicationTitle);
        window.setHeight(300);
        window.setWidth(400);
        window.getIcons().add(new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("images/icon.jpg"))));
        this.showLoginScreen();
    }

    private void showLoginScreen() {
        //create login screen
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        //title text
        Text sceneTitle = new Text("Welcome to OGame-Spybot");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        //email field
        Label eMail = new Label("E-Mail: ");
        grid.add(eMail, 0, 1);
        TextField eMailTextField = new TextField();
        grid.add(eMailTextField, 1, 1);

        //username field
        Label userName = new Label("User Name:");
        grid.add(userName, 0, 2);
        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 2);

        //server field
        Label serverLabel = new Label("Server:");
        grid.add(serverLabel, 0, 3);
        TextField serverTextField = new TextField();
        grid.add(serverTextField, 1, 3);

        //password field
        Label pw = new Label("Password:");
        grid.add(pw, 0, 4);
        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 4);

        //login button
        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 5);

        //action target text
        final Text actionTarget = new Text();
        grid.add(actionTarget, 1, 7);

        btn.setOnAction(event -> {
            new Thread(() -> {
                Settings.eMail = eMailTextField.getText().trim();
                Settings.password = pwBox.getText().trim();
                BrowserManager.getInstance().loginLobby(Settings.eMail, Settings.password);
                Utility.sleep(1000);
                Settings.server = serverTextField.getText().trim();
                Settings.userName = userTextField.getText().trim();
                BrowserManager.getInstance().loginUniverse(Settings.server, Settings.userName);
                Settings.playerPlanets = BrowserManager.getInstance().getAccountPlanets();
                Settings.activePlanet = Settings.playerPlanets.get(0);
                Utility.sleep(1000);
                this.showMainScene();
            }).start();
        });

        Scene scene = new Scene(grid, 300, 275);
        window.setScene(scene);
        window.show();
    }

    private void showMainScene() {
        Region headerLeftSpring = new Region();

        ImageView logo = new ImageView(
                new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("images/icon.jpg")))
        );

        logo.setFitHeight(40);
        logo.setFitWidth(40);
        logo.setPreserveRatio(true);

        HBox searchBox = new HBox(5);
        TextArea searchArea = new TextArea();
        searchArea.setPrefHeight(40);
        searchArea.setPrefWidth(120);

        Button searchGoButton = new Button("Go");
        searchBox.getChildren().addAll(searchArea, searchGoButton);

        Label appTitle = new Label(Settings.applicationTitle);

        BorderPane pane = new BorderPane();

        //define header
        HBox headerBox = new HBox(10);
        headerBox.getChildren().addAll(logo, headerLeftSpring, appTitle, searchBox);
        pane.setTop(headerBox);

        //define middle content
        TabPane contentTabPane = new TabPane();

        Tab mainTab = new Tab();

        Tab settingsTab = new Tab();

        contentTabPane.getTabs().addAll(mainTab, settingsTab);
        pane.setCenter(contentTabPane);

        //define footer content

        HBox footer = new HBox(5);
        footer.setId("footer");
        Label footerTitle = new Label("Footer right");
        footer.getChildren().add(footerTitle);
        pane.setBottom(footer);
        Scene scene = new Scene(pane);
        //TODO css stylesheet?
//        scene.setUserAgentStylesheet("javafxpert/layoutsanstears/ui/myStyles.css");

        HBox.setHgrow(headerLeftSpring, Priority.ALWAYS);
        window.setTitle("Layout Sans Tears: Exercise");
        window.setScene(scene);
        window.show();
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

        Button scanGalaxyButton = new Button("Scan galaxy");
        scanGalaxyButton.setOnAction(event -> toggleGalaxyScan(scanGalaxyButton));
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

    private void toggleGalaxyScan(Button scanGalaxyButton) {
        if (isScanning) {
            scanGalaxyButton.setText("Scan galaxy");
            try {
                scanThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            scanGalaxyButton.setText("Stop galaxy scan");
            new Thread(() -> BrowserManager.getInstance().scanGalaxy(Settings.activePlanet.getCoordinates().getGalaxy(),
                    Settings.LOWER_SYSTEM, Settings.UPPER_SYSTEM, Settings.activePlanet.getId())
            ).start();
        }
    }

    private void setupSettingsScene() {
        VBox settingsSceneLayout = new VBox(10);
        settingsSceneLayout.setPadding(new Insets(15, 20, 10, 10));

        Label lowerSystemLabel = new Label("Lower sun system:");
        TextField lowerSystem = new TextField("");
        settingsSceneLayout.getChildren().addAll(lowerSystemLabel, lowerSystem);

        Label upperSystemLabel = new Label("Upper sun system:");
        TextField upperSystem = new TextField("");
        settingsSceneLayout.getChildren().addAll(upperSystemLabel, upperSystem);

        Label probesPerSpyLabel = new Label("Probes per spy:");
        TextField probesPerSpy = new TextField("");
        settingsSceneLayout.getChildren().addAll(probesPerSpyLabel, probesPerSpy);

        Label espionageFileLabel = new Label("Espionage file name:");
        TextField espionageFileName = new TextField("spyreports");
        Button loginButton = new Button("Login");

        settingsSceneLayout.getChildren().addAll(espionageFileLabel, espionageFileName, loginButton);
        settingsScene = new Scene(settingsSceneLayout);
    }
}
