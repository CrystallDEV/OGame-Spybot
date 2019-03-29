package de.crystalldev;

import de.crystalldev.models.PlayerPlanet;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by Crystall on 03/29/2019
 * Contains all attributes that are changeable in the settings menu
 */
public class SettingsManager {

    private static SettingsManager instance;

    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }

    @Getter @Setter
    private String userName = "";
    @Getter @Setter
    private String eMail = "";
    @Getter @Setter
    private String password = "";
    @Getter @Setter
    private String server = "";
    @Getter @Setter
    private String serverAddress = "";

    //Active working range of the sun systems
    @Getter @Setter
    private int lowerSystem = 1;
    @Getter @Setter
    private int upperSystem = 499;
    @Getter @Setter
    private int probesPerSpy = 50;
    @Getter @Setter
    private String espionageFile = "spyreports";
    @Getter @Setter
    private ArrayList<PlayerPlanet> playerPlanets = new ArrayList<>();
    @Getter @Setter
    private boolean savePassword = false;

    @Getter @Setter
    private String applicationTitle = "Spy-Bot Ogame";
    @Getter @Setter
    private int actionsSinceAttackDetected = 0;
    @Getter @Setter
    private PlayerPlanet activePlanet;


    void saveConfigValues() {
        long start = System.currentTimeMillis();
        try (OutputStream outputStream = new FileOutputStream("config.properties")) {
            Properties prop = new Properties();

//            if (outputStream == null) {
//                System.out.println("No config file found. Continuing without config.");
//                return;
//            }

            // get the property value and print it out
            if (userName.length() > 0) {
                prop.setProperty("userName", userName);
            }
            if (eMail.length() > 0) {
                prop.setProperty("eMail", eMail);
            }
            if (password.length() > 0 && savePassword) {
                prop.setProperty("password", password);
            }
            if (server.length() > 0) {
                prop.setProperty("server", server);
            }
            if (lowerSystem > 0) {
                prop.setProperty("lowerSystem", String.valueOf(lowerSystem));
            }
            if (upperSystem > 0) {
                prop.setProperty("upperSystem", String.valueOf(upperSystem));
            }
            if (probesPerSpy > 0) {
                prop.setProperty("probesPerSpy", String.valueOf(probesPerSpy));
            }
            if (espionageFile.length() > 0) {
                prop.setProperty("espionageFile", espionageFile);
            }
            prop.store(outputStream, null);
            System.out.println("Saved config in " + (System.currentTimeMillis() - start) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception: " + e);
        }
    }

    void loadConfigValues() {
        long start = System.currentTimeMillis();
        try (InputStream inputStream = new FileInputStream("config.properties")) {
            Properties prop = new Properties();

            if (inputStream.available() <= 0) {
                System.out.println("No config file found. Continuing without config.");
                return;
            }
            prop.load(inputStream);

            // get the property value and print it out
            userName = prop.getProperty("userName");
            eMail = prop.getProperty("eMail");
            password = prop.getProperty("password");
            server = prop.getProperty("server");
            lowerSystem = prop.getProperty("lowerSystem").length() > 0 ? Integer.parseInt(prop.getProperty("lowerSystem")) : 1;
            upperSystem = prop.getProperty("upperSystem").length() > 0 ? Integer.parseInt(prop.getProperty("upperSystem")) : 499;
            probesPerSpy = prop.getProperty("probesPerSpy").length() > 0 ? Integer.parseInt(prop.getProperty("probesPerSpy")) : 50;
            espionageFile = prop.getProperty("espionageFile");

            System.out.println("Loaded config in " + (System.currentTimeMillis() - start) + " ms");
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }
}
