package de.crystalldev.Util;

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
}
