package de.crystalldev.util;

import de.crystalldev.models.PlayerPlanet;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
    public final static String ESPIONAGEFILE = "spyreports";
    public static String userName;
    public static String eMail;
    public static String password;
    public static String server;
    public static String serverAddress;
    public static int actionsSinceAttackDetected = 0;
    public static ArrayList<PlayerPlanet> playerPlanets = new ArrayList<>();
    public static String activePlanet;

    /**
     * Lets the thread sleep for a random amount of time
     * @param milliseconds
     */
    public static void sleep(int milliseconds) {
        try {
            Thread.sleep((ThreadLocalRandom.current().nextInt(milliseconds, (int) (milliseconds * 1.5 + 1))));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String regexString(String stringToAnalyse, String regexString) {
        Pattern p = Pattern.compile(regexString);
        Matcher m = p.matcher(stringToAnalyse);
        return m.find() ? m.group() : null;
    }
}
