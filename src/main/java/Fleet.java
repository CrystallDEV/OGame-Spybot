import javafx.util.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;

public class Fleet {
    public static final int ATTACK_MISSION = 1;
    public static final int TRANSPORT_MISSION = 3;
    public static final int DEPLOYMENT_MISSION = 4;
    public static final int ESPIONAGE_MISSION = 6;
    public static final int COLONIZATION_MISSION = 7;
    public static final int RECYCLE_MISSION = 8;
    public static final int EXPEDITION_MISSION = 15;

    int speed, mission;
    ArrayList<Pair<Ships, Integer>> ships = new ArrayList<>();
    Coordinates target;
    String origin;
    int metall, crystal, deuterium;

    public Fleet() {
    }

    public static int getAttackMission() {
        return ATTACK_MISSION;
    }

    public int getMetall() {
        return metall;
    }

    public void setMetall(int metall) {
        this.metall = metall;
    }

    public int getCrystal() {
        return crystal;
    }

    public void setCrystal(int crystal) {
        this.crystal = crystal;
    }

    public int getDeuterium() {
        return deuterium;
    }

    public void setDeuterium(int deuterium) {
        this.deuterium = deuterium;
    }

    public String getOrigin() {
        return origin;
    }

    public Coordinates getTarget() {
        return target;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setTarget(String targetCoordinates) {
        System.out.println(targetCoordinates);
        this.target = new Coordinates(targetCoordinates);
    }

    public void setTarget(Coordinates targetCoordinates) {
        this.target = targetCoordinates;
    }

    public void setMission(int mission) {
        this.mission = mission;
    }

    public void setShips(Pair... ships) {
        for (Pair ship : ships) {
            this.ships.add(ship);
        }


    }

    public int getMission() {
        return this.mission;
    }


    public boolean getShips(WebDriver driver) {
        boolean hadErrors = false;
        WebElement LIGHTFIGHTERTextField = driver.findElement(By.xpath("//*[@id=\"ship_204\"]"));
        WebElement HEAVYFIGHTERTextField = driver.findElement(By.xpath("//*[@id=\"ship_205\"]"));
        WebElement CRUISERTextField = driver.findElement(By.xpath("//*[@id=\"ship_206\"]"));
        WebElement BATTLESHIPTextField = driver.findElement(By.xpath("//*[@id=\"ship_207\"]"));
        WebElement BATTLECRUISERTextField = driver.findElement(By.xpath("//*[@id=\"ship_215\"]"));
        WebElement DESTROYERTextField = driver.findElement(By.xpath("//*[@id=\"ship_213\"]"));
        WebElement BOMBERTextField = driver.findElement(By.xpath("//*[@id=\"ship_211\"]"));
        WebElement DEATHSTARTextField = driver.findElement(By.xpath("//*[@id=\"ship_214\"]"));
        WebElement SMALLCARGOTextField = driver.findElement(By.xpath("//*[@id=\"ship_202\"]"));
        WebElement LARGECARGOTextField = driver.findElement(By.xpath("//*[@id=\"ship_203\"]"));
        WebElement COLONYSHIPTextField = driver.findElement(By.xpath("//*[@id=\"ship_208\"]"));
        WebElement RECYCLERTextField = driver.findElement(By.xpath("//*[@id=\"ship_209\"]"));
        WebElement ESPIONAGEPROBETextField = driver.findElement(By.xpath("//*[@id=\"ship_210\"]"));

        for (Pair ship : ships) {

            try {
                Pair<Ships, Integer> pair = ship;
                Utility.sleep(500);

                switch (pair.getKey()) {
                    case SMALLCARGO:
                        SMALLCARGOTextField.sendKeys(pair.getValue() + "");
                        break;
                    case LARGECARGO:
                        LARGECARGOTextField.sendKeys(pair.getValue() + "");
                        break;
                    case LIGHTFIGHTER:
                        LIGHTFIGHTERTextField.sendKeys(pair.getValue() + "");
                        break;
                    case HEAVYFIGHTER:
                        HEAVYFIGHTERTextField.sendKeys(pair.getValue() + "");
                        break;
                    case CRUISER:
                        CRUISERTextField.sendKeys(pair.getValue() + "");
                        break;
                    case BATTLESHIP:
                        BATTLESHIPTextField.sendKeys(pair.getValue() + "");
                        break;
                    case COLONYSHIP:
                        COLONYSHIPTextField.sendKeys(pair.getValue() + "");
                        break;
                    case RECYCLER:
                        RECYCLERTextField.sendKeys(pair.getValue() + "");
                        break;
                    case ESPIONAGEPROBE:
                        ESPIONAGEPROBETextField.sendKeys(pair.getValue() + "");
                        break;
                    case BOMBER:
                        BOMBERTextField.sendKeys(pair.getValue() + "");
                        break;
                    case DESTROYER:
                        DESTROYERTextField.sendKeys(pair.getValue() + "");
                        break;
                    case DEATHSTAR:
                        DEATHSTARTextField.sendKeys(pair.getValue() + "");
                        break;
                    case BATTLECRUISER:
                        BATTLECRUISERTextField.sendKeys(pair.getValue() + "");
                        break;
                    default:
                        System.out.println("Issue assigning Ship Variables @Fleet.java getShips(Pair... ships))");

                }

            } catch (Exception e) {
                hadErrors = true;
            }

        }
        return hadErrors;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getSpeed() {
        return this.speed / 10 + "";
    }
}
