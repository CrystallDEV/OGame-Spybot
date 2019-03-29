package de.crystalldev.utils;

import de.crystalldev.models.Coordinates;
import de.crystalldev.models.PlayerPlanet;
import de.crystalldev.SettingsManager;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
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

    /**
     * Returns a random moon of the existing moons
     * @return
     */
    public static PlayerPlanet getRandomMoon() {
        ArrayList<PlayerPlanet> moons = new ArrayList<>();
        for (PlayerPlanet p : SettingsManager.getInstance().getPlayerPlanets()) {
            if (p.getCoordinates().getPlanetType() == Coordinates.PlanetType.MOON) {
                moons.add(p);
            }
        }
        Random r = new Random();
        return moons.get(Math.round(r.nextInt() * moons.size()));
    }
}
