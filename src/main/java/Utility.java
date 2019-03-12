import com.google.gson.Gson;
import org.openqa.selenium.json.Json;

import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
    public final static String ESPIONAGEFILE = "spyreports";
    public static String userName;
    public static String eMail;
    public static String password;
    public static String server;
    public static String serverAdress;
    public static int actionsSinceAttackDetected = 0;
    public static String activePlanet;

    public static boolean importFromJson() {
        try {
            //TODO
//            URL url = Utility.class.getClassLoader().getResource("config.json");
//            JSONArray file = new Json(String.valueOf(url));
//            Gson gson = new Gson();
//
//            gson.fromJson(file, Utility.class);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static void sleep(int miliseconds) {
        try {
            Thread.currentThread().sleep((ThreadLocalRandom.current().nextInt(miliseconds, (int) (miliseconds * 1.5 + 1))));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String regexString(String stringToAnalyse, String regexString) {
        Pattern p = Pattern.compile(regexString);
        Matcher m = p.matcher(stringToAnalyse);
        while (m.find()) {
            return m.group();
        }
        return null;
    }
}
