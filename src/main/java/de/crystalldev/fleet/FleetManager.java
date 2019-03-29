package de.crystalldev.fleet;

import de.crystalldev.BrowserManager;
import de.crystalldev.models.Coordinates;
import de.crystalldev.SettingsManager;
import de.crystalldev.utils.Utility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Created by Crystall on 03/29/2019
 */
public class FleetManager {

    private static FleetManager instance;

    public static FleetManager getInstance() {
        if (instance == null) {
            instance = new FleetManager();
        }
        return instance;
    }

    public void saveFleet() {
        WebElement metal = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"resources_metal\"]"));
        WebElement crystal = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"resources_crystal\"]"));
        WebElement deuterium = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"resources_deuterium\"]"));
        Fleet fleet = new Fleet();
        fleet.setOrigin(SettingsManager.getInstance().getActivePlanet().getId());
        //TODO set target to a desired moon or random moon
        fleet.setTarget("[5:28:12] Moon");
        fleet.setMaxShips();
        fleet.setMission(Fleet.DEPLOYMENT_MISSION);
        fleet.setMetall(Integer.parseInt(metal.getText().replace(".", "")));
        fleet.setCrystal(Integer.parseInt(crystal.getText().replace(".", "")));
        fleet.setDeuterium(Integer.parseInt(deuterium.getText().replace(".", "")) - 500000);
        fleet.setSpeed(10);

        int failureCount = 0;
        while (true) {
            try {
                if (failureCount > 300) {
                    System.out.println("If your fleet is not safe by now, it never will be LOL");
                    break;
                }
                sendFleet(fleet);
                System.out.println("Sent fleet.");
                break;
            } catch (Exception e) {
                failureCount++;
                e.printStackTrace();
                System.out.println("Saving fleet failed. Retrying lol");
            }
        }
        BrowserManager.getInstance().checkAttack();
    }

    private void sendFleet(Fleet fleet) {
        System.out.println("Sending Fleet");
        BrowserManager.getInstance().getDriver().get(SettingsManager.getInstance().getServerAddress() + "/game/index.php?page=fleet1&cp=" + fleet.getOrigin());
        Utility.sleep(2000);
        fleet.getShips(BrowserManager.getInstance().getDriver());
        Utility.sleep(750);
        WebElement fleet1ContinueButton = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"continue\"]"));
        BrowserManager.getInstance().clickElement(fleet1ContinueButton);

        Utility.sleep(2500);

        WebElement galaxyTextField = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"galaxy\"]"));
        WebElement systemTextField = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"system\"]"));
        WebElement planetTextField = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"position\"]"));
        WebElement speedButton = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"speedLinks\"]/a[" + fleet.getSpeed() + "]"));
        WebElement fleet2ContinueButton = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"continue\"]/span"));

        Utility.sleep(1000);
        galaxyTextField.click();
        Utility.sleep(150);
        galaxyTextField.sendKeys(fleet.getTarget().getGalaxy() + "");
        Utility.sleep(750);
        systemTextField.click();
        Utility.sleep(150);
        systemTextField.sendKeys(fleet.getTarget().getSystem() + "");
        Utility.sleep(750);
        planetTextField.click();
        Utility.sleep(150);
        planetTextField.sendKeys(fleet.getTarget().getPlanet() + "");
        Utility.sleep(750);
        speedButton.click();
        Utility.sleep(500);
        if (fleet.getTarget().getPlanetType() == Coordinates.PlanetType.PLANET) {
            WebElement selectPlanet = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"pbutton\"]"));
            selectPlanet.click();
        } else if (fleet.getTarget().getPlanetType() == Coordinates.PlanetType.MOON) {
            WebElement selectPlanet = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"mbutton\"]"));
            selectPlanet.click();
        } else if (fleet.getTarget().getPlanetType() == Coordinates.PlanetType.DEBRIS) {
            WebElement selectPlanet = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"dbutton\"]"));
            selectPlanet.click();
        }

        Utility.sleep(500);
        BrowserManager.getInstance().clickElement(fleet2ContinueButton);
        Utility.sleep(2000);

        WebElement missionButton = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"missionButton" + fleet.getMission() + "\"]"));
        WebElement sendFleetButton = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"start\"]/span"));

        if (missionButton.getAttribute("class").equals("off")) {
            return;
        }
        missionButton.click();
        Utility.sleep(900);

        if (fleet.getMetall() != 0) {
            WebElement metalTextField = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"metal\"]"));
            metalTextField.sendKeys(fleet.getMetall() + "");
            Utility.sleep(500);
        }
        if (fleet.getCrystal() != 0) {
            WebElement crystalTextField = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"crystal\"]"));
            crystalTextField.sendKeys(fleet.getCrystal() + "");
            Utility.sleep(500);
        }
        if (fleet.getDeuterium() != 0) {
            WebElement deuteriumTextField = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"deuterium\"]"));
            deuteriumTextField.sendKeys(fleet.getDeuterium() + "");
            Utility.sleep(500);
        }

        sendFleetButton.click();
    }

    public void sendFleetInactiveFast(Fleet fleet) {
        Coordinates target = fleet.getTarget();
        BrowserManager.getInstance().getUrl(SettingsManager.getInstance().getServerAddress() + "/game/index.php?page=fleet1&galaxy=" + target.getGalaxy() + "&system=" + target.getSystem() + "&position=" + target.getPlanet() + "&type=1&mission=" + fleet.getMission() + "&cp=" + fleet.getOrigin());

        try {
            WebElement playerStatusSpan = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"fleetStatusBar\"]/ul/li[3]/span[2]"));
            if (playerStatusSpan.getAttribute("class").equals("honorRank rank_starlord3 tooltip") ||
                    playerStatusSpan.getAttribute("class").equals("honorRank rank_starlord2 tooltip") ||
                    playerStatusSpan.getAttribute("class").equals("honorRank rank_bandit3 tooltipHTML")) {
                playerStatusSpan = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"fleetStatusBar\"]/ul/li[3]/span[3]"));
            }
            if (!((playerStatusSpan.getAttribute("class").equals("status_abbr_longinactive")) || (playerStatusSpan.getAttribute("class").equals("status_abbr_inactive")))) {
                System.out.println(playerStatusSpan.getAttribute("class"));
                return;
            }
        } catch (org.openqa.selenium.NoSuchElementException e) {
            System.out.println("Some issues with loading Fleet1. Waiting some time");
            Utility.sleep(5000);
            return;
        }

        WebElement slotsUsedSpan = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"slots\"]/div[1]/span"));
        String[] slotsUsedSpanHelper = slotsUsedSpan.getText().replace("Flotten", "").replace(":", "").trim().split("/");
        int slotsInUse = Integer.parseInt(slotsUsedSpanHelper[0]);
        int maxSlots = Integer.parseInt(slotsUsedSpanHelper[1]);
        boolean fleetSlotsFull = slotsInUse >= (maxSlots);

        while (fleetSlotsFull) {
            System.out.println("All de.crystalldev.fleet.Fleet Slots are FULL waiting 17-27 seconds");
            Utility.sleep(15000);

            BrowserManager.getInstance().getUrl(SettingsManager.getInstance().getServerAddress() + "/game/index.php?page=fleet1&galaxy=" + target.getGalaxy() + "&system=" + target.getSystem() + "&position=" + target.getPlanet() + "&type=1&mission=" + fleet.getMission() + "&cp=" + fleet.getOrigin());
            slotsUsedSpan = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"slots\"]/div[1]/span"));
            slotsUsedSpanHelper = slotsUsedSpan.getText().replace("Flotten", "").replace(":", "").trim().split("/");

            System.out.println("Slots in use: " + slotsUsedSpanHelper[0] + "/ " + slotsUsedSpanHelper[1]);

            slotsInUse = Integer.parseInt(slotsUsedSpanHelper[0]);
            maxSlots = Integer.parseInt(slotsUsedSpanHelper[1]);
            fleetSlotsFull = slotsInUse >= (maxSlots);
        }

        while (fleet.getShips(BrowserManager.getInstance().getDriver())) {
            System.out.println("No de.crystalldev.fleet.Ships available. Waiting 20-30 seconds");
            Utility.sleep(20000);
            BrowserManager.getInstance().getUrl(SettingsManager.getInstance().getServerAddress() + "/game/index.php?page=fleet1&galaxy=" + target.getGalaxy() + "&system=" + target.getSystem() + "&position=" + target.getPlanet() + "&type=1&mission=" + fleet.getMission() + "&cp=" + fleet.getOrigin());
        }

        Utility.sleep(750);
        WebElement fleet1ContinueButton = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"continue\"]"));
        BrowserManager.getInstance().clickElement(fleet1ContinueButton);
        Utility.sleep(1200);

        if (!BrowserManager.getInstance().getDriver().getCurrentUrl().equals(SettingsManager.getInstance().getServerAddress() + "/game/index.php?page=fleet2")) {
            System.out.println("Not page 2 yet. Clicking again");
            BrowserManager.getInstance().clickElement(fleet1ContinueButton);
        }
        Utility.sleep(2000);

        WebElement speedButton = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"speedLinks\"]/a[" + fleet.getSpeed() + "]"));
        WebElement fleet2ContinueButton = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"continue\"]/span"));

        speedButton.click();
        Utility.sleep(250);
        BrowserManager.getInstance().clickElement(fleet2ContinueButton);
        Utility.sleep(2500);

        if (!BrowserManager.getInstance().getDriver().getCurrentUrl().equals(SettingsManager.getInstance().getServerAddress() + "/game/index.php?page=fleet3")) {
            System.out.println("Not page 3 yet. Clicking again");
            BrowserManager.getInstance().clickElement(fleet2ContinueButton);
            Utility.sleep(2500);
        }

        WebElement missionButton = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"missionButton" + fleet.getMission() + "\"]"));
        WebElement sendFleetButton = BrowserManager.getInstance().getDriver().findElement(By.xpath("//*[@id=\"start\"]/span"));

        if (missionButton.getAttribute("class").equals("off")) {
            System.out.println("Send de.crystalldev.fleet.Fleet Failure");
            return;
        }

        BrowserManager.getInstance().clickElement(sendFleetButton);
        Utility.sleep(2000);

        if (BrowserManager.getInstance().getDriver().getCurrentUrl().equals(SettingsManager.getInstance().getServerAddress() + "/game/index.php?page=fleet1")) {
            System.out.println("Sent Fleet successfully");
        } else {
            Utility.sleep(1000);
            System.out.println("Clicking again!");
            BrowserManager.getInstance().clickElement(sendFleetButton);
        }
        Utility.sleep(1000);
    }
}
