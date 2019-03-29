package de.crystalldev;

import de.crystalldev.models.PlayerPlanet;
import de.crystalldev.utils.Utility;
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

import java.util.Objects;

/**
 * Created by Crystall on 02/29/2019
 */
public class Main extends Application {

    private Stage window;
    private boolean isScanning = false;
    private Thread scanThread = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle(SettingsManager.getInstance().getApplicationTitle());
        window.setHeight(300);
        window.setWidth(400);
        window.getIcons().add(new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("images/icon.jpg"))));
        SettingsManager.getInstance().loadConfigValues();
        this.showLoginScreen();
    }

    @Override
    public void stop() {
        System.out.println("Stage is closing");
        SettingsManager.getInstance().saveConfigValues();
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
        TextField eMailTextField = new TextField(SettingsManager.getInstance().getEMail());
        grid.add(eMailTextField, 1, 1);

        //username field
        Label userName = new Label("User Name:");
        grid.add(userName, 0, 2);
        TextField userTextField = new TextField(SettingsManager.getInstance().getUserName());
        grid.add(userTextField, 1, 2);

        //server field
        Label serverLabel = new Label("Server:");
        grid.add(serverLabel, 0, 3);
        TextField serverTextField = new TextField(SettingsManager.getInstance().getServer());
        grid.add(serverTextField, 1, 3);

        //password field
        Label pw = new Label("Password:");
        grid.add(pw, 0, 4);
        PasswordField pwBox = new PasswordField();
        pwBox.setText(SettingsManager.getInstance().getPassword());
        grid.add(pwBox, 1, 4);

        //login button
        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        RadioButton radioBtn = new RadioButton("Save password");
        hbBtn.getChildren().add(radioBtn);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);

        grid.add(hbBtn, 1, 5);


        //action target text
        final Text actionTarget = new Text();
        grid.add(actionTarget, 1, 7);

        btn.setOnAction(event -> {
            //TODO check for entered content
            SettingsManager.getInstance().setEMail(eMailTextField.getText().trim());
            SettingsManager.getInstance().setPassword(pwBox.getText().trim());
            SettingsManager.getInstance().setServer(serverTextField.getText().trim());
            SettingsManager.getInstance().setUserName(userTextField.getText().trim());
            SettingsManager.getInstance().setSavePassword(radioBtn.isSelected());

            BrowserManager.getInstance().loginLobby();
            Utility.sleep(1000);
            BrowserManager.getInstance().loginUniverse();

            SettingsManager.getInstance().setPlayerPlanets(BrowserManager.getInstance().getAccountPlanets());
            SettingsManager.getInstance().setActivePlanet(SettingsManager.getInstance().getPlayerPlanets().get(0));

            Utility.sleep(1000);
            this.showMainScene();
        });

        Scene scene = new Scene(grid, 300, 275);
        window.setScene(scene);
        window.show();
    }

    private void showMainScene() {
        window.setWidth(900);
        window.setHeight(600);

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

        Label appTitle = new Label(SettingsManager.getInstance().getApplicationTitle());
        BorderPane pane = new BorderPane();

        //define header
        HBox headerBox = new HBox(10);
        headerBox.getChildren().addAll(logo, headerLeftSpring, appTitle, searchBox);
        pane.setTop(headerBox);

        //define middle content
        TabPane contentTabPane = new TabPane();
        contentTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        //main tab
        Tab mainTab = new Tab("Spybot");
        this.addSpybotContent(mainTab);

        //settings tab
        Tab settingsTab = new Tab("Settings");
        this.addSettingsContent(settingsTab);

        contentTabPane.getTabs().addAll(mainTab, settingsTab);
        pane.setCenter(contentTabPane);

        //define footer content
        HBox footer = new HBox(5);
        footer.setId("footer");
        Label footerTitle = new Label("Developed by CrystallDEV (github.com/CrystallDEV)");
        footer.getChildren().add(footerTitle);
        pane.setBottom(footer);
        Scene scene = new Scene(pane);
        //TODO css stylesheet?
//        scene.setUserAgentStylesheet("javafxpert/layoutsanstears/ui/myStyles.css");

        HBox.setHgrow(headerLeftSpring, Priority.ALWAYS);
        window.setScene(scene);
        window.show();
    }

    private void addSpybotContent(Tab mainTab) {
        //Declaring variables for mainScene
        VBox mainSceneLayout = new VBox(10);

        for (PlayerPlanet planet : SettingsManager.getInstance().getPlayerPlanets()) {
            Button planetButton = new Button(planet.getCoordinates().toString());
            planetButton.setOnAction(event -> {
                SettingsManager.getInstance().setActivePlanet(planet);
                BrowserManager.getInstance().setRunning(true);
                new Thread(() -> BrowserManager.getInstance().espionageFarming(planet.getId())).start();

//                turnOff.setOnAction(e -> {
//                    BrowserManager.getInstance().setRunning(false);
//                    mainSceneLayout.getChildren().addAll(buttons);
//                    mainSceneLayout.getChildren().removeAll(turnOff, pause);
//                });
//
//                pause.setOnAction(event1 -> BrowserManager.getInstance().setPaused(!BrowserManager.getInstance().isPa
//                used()));
                mainSceneLayout.getChildren().add(planetButton);
            });
        }

        //scan galaxy button
        Button scanGalaxyButton = new Button("Scan galaxy");
        scanGalaxyButton.setOnAction(event -> toggleGalaxyScan(scanGalaxyButton));
        mainSceneLayout.getChildren().add(scanGalaxyButton);

        //parse espionage messages button
        Button parseEspionageMessagesButton = new Button("Parse Espionage Messages");
        parseEspionageMessagesButton.setOnAction(event -> new Thread(() -> BrowserManager.getInstance().parseEspionageMessages()).start());
        mainSceneLayout.getChildren().add(parseEspionageMessagesButton);

        //refresh button
        Button refresh = new Button("Refresh");
        refresh.setOnAction(event -> new Thread(() -> BrowserManager.getInstance().refresh(SettingsManager.getInstance().getActivePlanet().getId())).start());
        mainSceneLayout.getChildren().add(refresh);

        mainTab.setContent(mainSceneLayout);
    }

    private void addSettingsContent(Tab settingsTab) {
        VBox settingsSceneLayout = new VBox(10);
        settingsSceneLayout.setPadding(new Insets(15, 20, 10, 10));

        Label lowerSystemLabel = new Label("Lower sun system:");
        TextField lowerSystem = new TextField(String.valueOf(SettingsManager.getInstance().getLowerSystem()));
        settingsSceneLayout.getChildren().addAll(lowerSystemLabel, lowerSystem);

        Label upperSystemLabel = new Label("Upper sun system:");
        TextField upperSystem = new TextField(String.valueOf(SettingsManager.getInstance().getUpperSystem()));
        settingsSceneLayout.getChildren().addAll(upperSystemLabel, upperSystem);

        Label probesPerSpyLabel = new Label("Probes per spy:");
        TextField probesPerSpy = new TextField(String.valueOf(SettingsManager.getInstance().getProbesPerSpy()));
        settingsSceneLayout.getChildren().addAll(probesPerSpyLabel, probesPerSpy);

        Label espionageFileLabel = new Label("Espionage file name:");
        TextField espionageFileName = new TextField(SettingsManager.getInstance().getEspionageFile());

        settingsSceneLayout.getChildren().addAll(espionageFileLabel, espionageFileName);
        settingsTab.setContent(settingsSceneLayout);
    }

    private void toggleGalaxyScan(Button scanGalaxyButton) {
        if (isScanning) {
            scanGalaxyButton.setText("Scan galaxy");
            scanThread.interrupt();
            isScanning = false;
            scanThread = null;
        } else {
            scanGalaxyButton.setText("Stop galaxy scan");
            scanThread = new Thread(() -> BrowserManager.getInstance().scanGalaxy(SettingsManager.getInstance().getActivePlanet().getCoordinates().getGalaxy(),
                    SettingsManager.getInstance().getLowerSystem(), SettingsManager.getInstance().getUpperSystem(), SettingsManager.getInstance().getActivePlanet().getId())
            );
            scanThread.start();
            isScanning = true;
        }
    }

    private void toggleEspionageFarming(Button planetButton) {
        //TODO
    }
}
