package de.crystalldev.Util;

import de.crystalldev.Models.PlayerPlanet;

import java.util.ArrayList;

/**
 * Created by Crystall on 03/24/2019
 * Settings that contains all important information about the client
 */
public class Settings {
    public static String userName;
    public static String eMail;
    public static String password;
    public static String server;
    public static String serverAddress;

    //Active working range of the sun systems
    public static int LOWER_SYSTEM = 1;
    public static int UPPER_SYSTEM = 499;
    public static int PROBES_PER_SPY = 50;

    public static String applicationTitle = "Spy-Bot Ogame";
    public final static String ESPIONAGEFILE = "spyreports";
    public static int actionsSinceAttackDetected = 0;
    public static ArrayList<PlayerPlanet> playerPlanets = new ArrayList<>();
    public static PlayerPlanet activePlanet;
}
