import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class Main extends Application {

    Stage window;
    Scene loginScene, mainScene, settingsScene;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BrowserManager browserManager = BrowserManager.instance();
        window = primaryStage;

        // Declaring variables for loginScene
        HBox loginSceneLayout = new HBox(20);
        Label emailLabel = new Label("E-Mail:");
        TextField email = new TextField("");
        Label passwordLabel = new Label("Password:");
        PasswordField password = new PasswordField();
        password.setPromptText("Your password");
        Label serverLabel = new Label("Server:");
        TextField serverTextField = new TextField("Europa");
        Label ingameNameLabel = new Label("User Name:");
        TextField ingameNameTextField = new TextField("Vice Orcus");
        Button loginButton = new Button("Login");


        loginSceneLayout.getChildren().addAll(emailLabel, email, passwordLabel, password, serverLabel, serverTextField, ingameNameLabel, ingameNameTextField, loginButton);
        loginScene = new Scene(loginSceneLayout);

        loginButton.setOnAction(event -> {
            Utility.eMail = email.getText().trim();
            Utility.password = password.getText().trim();
            browserManager.loginLobby(Utility.eMail, Utility.password);
            Utility.sleep(1000);
            Utility.server = serverTextField.getText().trim();
            Utility.userName = ingameNameTextField.getText().trim();
            browserManager.loginUniverse(Utility.server, Utility.userName);
            Utility.sleep(1000);
            window.setScene(mainScene);
        });


        //Declaring variables for mainScene
        Button startFarmBotButton = new Button("5 22 12");
        Button startFarmBotPlani2 = new Button("5 24 12");
        Button startFarmBotPlani3 = new Button("5 40 12");

        Button scanGalaxyButton = new Button("Scan Galaxy");
        Button parseEspionageMessagesButton = new Button("Parse Espionage Messages");

        Button refresh = new Button("Refresh");
        HBox mainSceneLayout = new HBox(30);


        startFarmBotButton.setOnAction(event -> {
            Utility.activePlanet = "34016475";
            browserManager.setRunning(true);
            new Thread(() -> new Thread(() -> {
                browserManager.refresh("33620855");
            }).start()).start();
            mainSceneLayout.getChildren().removeAll(startFarmBotButton, startFarmBotPlani2, startFarmBotPlani3, scanGalaxyButton, parseEspionageMessagesButton);
            Button turnOff = new Button("Off");
            Button pause = new Button("Pause");
            mainSceneLayout.getChildren().addAll(turnOff, pause);

            turnOff.setOnAction(event15 -> {
                browserManager.setRunning(false);
                mainSceneLayout.getChildren().addAll(startFarmBotButton, startFarmBotPlani2, startFarmBotPlani3, scanGalaxyButton, parseEspionageMessagesButton);
                mainSceneLayout.getChildren().removeAll(turnOff, pause);
            });
        });
        startFarmBotPlani2.setOnAction(event -> {
            Utility.activePlanet = "33816463";
            browserManager.setRunning(true);
            new Thread(() -> new Thread(() -> {
                browserManager.espionageFarming("33816463");
            }).start()).start();
            mainSceneLayout.getChildren().removeAll(startFarmBotButton, startFarmBotPlani2, startFarmBotPlani3, scanGalaxyButton, parseEspionageMessagesButton);
            Button turnOff = new Button("Off");
            Button pause = new Button("Pause");
            mainSceneLayout.getChildren().addAll(turnOff, pause);

            turnOff.setOnAction(event14 -> {
                browserManager.setRunning(false);
                mainSceneLayout.getChildren().addAll(startFarmBotButton, startFarmBotPlani2, startFarmBotPlani3, scanGalaxyButton, parseEspionageMessagesButton);
                mainSceneLayout.getChildren().removeAll(turnOff, pause);
            });

            pause.setOnAction(event1 -> browserManager.setPaused(!browserManager.isPaused()));

        });

        startFarmBotPlani3.setOnAction(event -> {
            Utility.activePlanet = "33816344";
            browserManager.setRunning(true);
            new Thread() {
                public void run() {
                    new Thread(() -> {
                        browserManager.espionageFarming("33816344");
                    }).start();


                }
            }.start();
            mainSceneLayout.getChildren().removeAll(startFarmBotButton, startFarmBotPlani2, startFarmBotPlani3, scanGalaxyButton, parseEspionageMessagesButton);
            Button turnOff = new Button("Off");
            Button pause = new Button("Pause");
            mainSceneLayout.getChildren().addAll(turnOff, pause);

            turnOff.setOnAction(event13 -> {
                browserManager.setRunning(false);
                mainSceneLayout.getChildren().addAll(startFarmBotButton, startFarmBotPlani2, startFarmBotPlani3, scanGalaxyButton, parseEspionageMessagesButton);
                mainSceneLayout.getChildren().removeAll(turnOff, pause);
            });


            pause.setOnAction(event12 -> browserManager.setPaused(!browserManager.isPaused()));

        });

        scanGalaxyButton.setOnAction(event -> {
            Utility.activePlanet = "34016475";
            browserManager.scanGalaxy(1, 1, 499, "34016475");

        });

        parseEspionageMessagesButton.setOnAction(event -> browserManager.parseEspionageMessages());

        refresh.setOnAction(event -> {
            Utility.activePlanet = "34016475";
            browserManager.refresh("34016475");

        });


        mainSceneLayout.getChildren().addAll(startFarmBotButton, startFarmBotPlani2, startFarmBotPlani3, scanGalaxyButton, parseEspionageMessagesButton);
        mainScene = new Scene(mainSceneLayout);

        window.setScene(loginScene);
        window.show();
    }
}
