import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.*;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BrowserManager {
    private WebDriver driver;
    private static BrowserManager unique = null;

    @Getter
    @Setter
    private boolean isRunning = false;

    @Getter
    @Setter
    private boolean isPaused = false;

    private BrowserManager() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Marek\\Desktop\\chromedriver5.exe");
        ChromeOptions options = new ChromeOptions();
        //  options.addArguments("load-extension=C:\\Users\\Chris\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\Extensions\\cjpalhdlnbpafiamejdnhcphjbkeiagm\\1.16.10_0");
        options.addArguments("--start-maximized");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        driver = new ChromeDriver(capabilities);
    }

    public static BrowserManager instance() {
        if (unique == null)
            unique = new BrowserManager();
        return unique;
    }

    public boolean getUrl(String url) {
        try {
            driver.get(url);
            Utility.sleep(2000);
            if (driver.getCurrentUrl().equals(url)) {
                checkAttack();
                return true;
            }
            Pattern p = Pattern.compile(driver.getCurrentUrl());
            Matcher m = p.matcher("https:\\/\\/s\\d+\\-.+\\.ogame\\.gameforge\\.com");
            if (m.find()) {
                System.out.println("Something wrong with building your URL?");
                return false;
            } else {
                System.out.println("Not signed in? Signing in again.");
                Utility.sleep(60000);

                this.loginLobby(Utility.eMail, Utility.password);
                Utility.sleep(5000);
                this.loginUniverse(Utility.server, Utility.userName);
                Utility.sleep(5000);
            }
            System.out.println("Relogged. Retrying.");
            driver.get(url);
            Utility.sleep(2000);
            if (driver.getCurrentUrl().equals(url)) {
                checkAttack();
                return true;
            }
            System.out.println("Something is very wrong with getting Urls");
            return false;
        } catch (NoSuchWindowException e) {
//            messageMyselfOnDiscord("NoSuchWindow");
            return false;
        }
    }

    public void printCookies() {
        Set<Cookie> allCookies = driver.manage().getCookies();
        System.out.println(driver.getTitle());
        System.out.println(allCookies);
    }


    public void switchTab(String tabName) {
        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
        //System.out.println("Switch Tab origin " + driver.getTitle() + " to " + tabName);
        Utility.sleep(1000);
        try {
            for (int i = 0; i < tabs.size(); i++) {
                driver.switchTo().window(tabs.get(i));
                if (driver.getTitle().equals(tabName)) {
                    //System.out.println("Switch Tab Success");
                    break;
                }
            }
        } catch (NoSuchWindowException e) {
//            messageMyselfOnDiscord("NoSuchWindow");
        }

    }


    public void loginLobby(String user, String pass) {
        driver.get("https://de.ogame.gameforge.com/");
        Utility.sleep(2000);
        WebElement loginButton = driver.findElement(By.id("ui-id-1"));
        clickElement(loginButton);
        Utility.sleep(1000);
        WebElement userName = driver.findElement(By.id("usernameLogin"));
        WebElement password = driver.findElement(By.id("passwordLogin"));
        WebElement loginSubmitButton = driver.findElement(By.id("loginSubmit"));
        userName.sendKeys(user);
        Utility.sleep(1000);
        password.sendKeys(pass);
        Utility.sleep(1000);
        loginSubmitButton.submit();
        Utility.sleep(3000);
    }

    public void loginUniverse(String serverName, String userName) {
        switchTab("OGame Lobby");
        Utility.sleep(4000);
        boolean loopHelper = true;
        int i = 1;
        try {
            while (loopHelper) {
                WebElement serverNameField = driver.findElement(By.xpath("//*[@id=\"accountlist\"]/div/div[1]/div[2]/div[" + i + "]/div/div[4]/div"));
                WebElement userNameField = driver.findElement(By.xpath("//*[@id=\"accountlist\"]/div/div[1]/div[2]/div[" + i + "]/div/div[9]"));
                WebElement accountLoginButton = driver.findElement(By.xpath("//*[@id=\"accountlist\"]/div/div[1]/div[2]/div[" + i + "]/div/div[11]/button"));

                if (serverNameField.getText().trim().equals(serverName) && userNameField.getText().trim().equals(userName)) {
                    loopHelper = false;
                    clickElement(accountLoginButton);
                    Utility.sleep(5000);
                    switchTab(serverName + " OGame");
                    printCookies();
                    Utility.sleep(5000);

                    Utility.serverAdress = Utility.regexString(driver.getCurrentUrl(), "https:\\/\\/s\\d+\\-.+\\.ogame\\.gameforge\\.com");
                    System.out.println("Setting Server URL to " + Utility.serverAdress);

                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Invalid Username or Server");
        }
    }


    public void scanGalaxy(int galaxy, int lower, int upper, String planetId) {

        deleteAllEspionageMessages();
        Utility.sleep(1000);

        for (int i = galaxy; i <= galaxy; i++) {
            for (int j = lower; j <= upper; j++) {
                try {
                    getUrl(Utility.serverAdress + "/game/index.php?page=galaxy&cp=" + planetId + "&galaxy=" + i + "&system=" + j);
                    for (int k = 1; k <= 15; k++) {
                        try {
                            WebElement galaxyPlayerRow = driver.findElement(By.xpath("//*[@id=\"galaxytable\"]/tbody/tr[" + k + "]/td[6]/a/span"));
                            WebElement playerStatus = driver.findElement(By.xpath("//*[@id=\"galaxytable\"]/tbody/tr[" + k + "]/td[6]/span"));
                            if (playerStatus.getText().trim().equals("")) {
                                //System.out.println("Honorable Player detected, reparsing playerStatus");
                                playerStatus = driver.findElement(By.xpath("//*[@id=\"galaxytable\"]/tbody/tr[" + k + "]/td[6]/span[2]"));
                            }
                            if (playerStatus.getText().trim().equals("(I)") || playerStatus.getText().trim().equals("(i)")) {
                                System.out.println("Player on [" + i + ":" + j + ":" + k + "] " + galaxyPlayerRow.getText() + " " + playerStatus.getText());
                                WebElement slotsUsedSpan = driver.findElement(By.xpath("//*[@id=\"slotValue\"]"));
                                String[] slotsUsedSpanHelper = slotsUsedSpan.getText().trim().split("/");
                                boolean fleetSlotsFull = slotsUsedSpanHelper[0].equals(slotsUsedSpanHelper[1]);

                                while (fleetSlotsFull) {
                                    Utility.sleep(15000);
                                    System.out.println("All Fleet Slots are FULL waiting 17-27 seconds");
                                    getUrl(Utility.serverAdress + "/game/index.php?page=galaxy&cp=" + planetId + "&galaxy=" + i + "&system=" + j);
                                    slotsUsedSpan = driver.findElement(By.xpath("//*[@id=\"slotValue\"]"));
                                    slotsUsedSpanHelper = slotsUsedSpan.getText().trim().split("/");
                                    Utility.sleep(100);
                                    System.out.println(slotsUsedSpanHelper[0] + " " + slotsUsedSpanHelper[1]);
                                    fleetSlotsFull = slotsUsedSpanHelper[0].equals(slotsUsedSpanHelper[1]);
                                    Utility.sleep(100);
                                }
                                WebElement planetEspionageButton = driver.findElement(By.xpath("//*[@id=\"galaxytable\"]/tbody/tr[" + k + "]/td[8]/span/a[1]/span"));
                                System.out.println("Spying [" + i + ":" + j + ":" + k + "] " + galaxyPlayerRow.getText() + " " + playerStatus.getText());
                                clickElement(planetEspionageButton);
                                Utility.sleep(2000);
                            }

                        } catch (Exception e) {
                            //System.out.println("No Player on [" + i + ":" + j + ":" + k + "]");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Issue finding Galaxy/Solarsystem Textfields");
                    System.out.println("Waiting 15 to 22 seconds before reattempting");
                    Utility.sleep(10000);
                    getUrl(Utility.serverAdress + "/game/index.php?page=galaxy&cp=" + planetId + "&galaxy=" + i + "&system=" + j);
                    j--;
                    Utility.sleep(3000);
                }
            }
        }
    }

    public void deleteAllEspionageMessages() {
        try {
            Utility.sleep(1000);
            getUrl(Utility.serverAdress + "/game/index.php?page=messages");
            WebElement deleteAllButton = driver.findElement(By.xpath("//*[@id=\"subtabs-nfFleetTrash\"]/div/span[2]/span"));
            clickElement(deleteAllButton);
            Utility.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception @deleteEspionageMessages");
        }
    }

    public void parseEspionageMessages() {
        boolean taskFinished = false;
        SpyReportContainer spyReportContainer = SpyReportContainer.instance();

        while (!taskFinished) {
            try {
                Utility.sleep(1000);
                getUrl(Utility.serverAdress + "/game/index.php?page=messages");

                WebElement amountPages = driver.findElement(By.xpath("//*[@id=\"fleetsgenericpage\"]/ul/ul[1]/li[3]"));
                System.out.println(amountPages.getText());
                String[] helper = amountPages.getText().split("/");
                int pagesCount = Integer.parseInt(helper[1]);
                for (int page = 1; page <= pagesCount; page++) {
                    Utility.sleep(2000);
                    WebElement nextPageButton = driver.findElement(By.xpath("//*[@id=\"fleetsgenericpage\"]/ul/ul[1]/li[4]"));
                    amountPages = driver.findElement(By.xpath("//*[@id=\"fleetsgenericpage\"]/ul/ul[1]/li[3]"));
                    helper = amountPages.getText().split("/");
                    if (Integer.parseInt(helper[0]) != page) {
                        System.out.println("Issue getting the right Messages Page. Sleeping 15-22s");
                        System.out.println("Supposed to be on page " + page + " but instead are on page " + helper[0]);
                        Utility.sleep(10000);
                        clickElement(nextPageButton);
                        Utility.sleep(3000);
                    }
                    try {
                        for (int i = 1; i <= 50; i++) {
                            Utility.sleep(100);
                            WebElement messageTitle = driver.findElement(By.xpath("//*[@id=\"fleetsgenericpage\"]/ul/li[" + i + "]/div[2]/span[1]"));

                            if (messageTitle.getText().trim().contains("Spionagebericht von")) {
                                try {
                                    WebElement messageContent = driver.findElement(By.xpath("//*[@id=\"fleetsgenericpage\"]/ul/li[" + i + "]/span"));
                                    WebElement messageRessources = driver.findElement(By.xpath("//*[@id=\"fleetsgenericpage\"]/ul/li[" + i + "]/span/div[2]/span[2]"));
                                    WebElement messageFleet = driver.findElement(By.xpath("//*[@id=\"fleetsgenericpage\"]/ul/li[" + i + "]/span/div[4]/span[1]"));
                                    WebElement messageDefences = driver.findElement(By.xpath("//*[@id=\"fleetsgenericpage\"]/ul/li[" + i + "]/span/div[4]/span[2]"));
                                    WebElement messageEspionageDefence = driver.findElement(By.xpath("//*[@id=\"fleetsgenericpage\"]/ul/li[" + i + "]/span/div[3]/span[2]"));
                                    String ressources = messageRessources.getText().replace("Rohstoffe:", "").trim();
                                    if (ressources.contains("M")) {
                                        ressources = ressources.replace("M", "").trim();
                                        String[] bufferHelper = ressources.split(",");

                                        StringBuffer outputBuffer = new StringBuffer(6);
                                        outputBuffer.append(bufferHelper[1]);
                                        for (int bufferLength = outputBuffer.length(); bufferLength < 6; bufferLength++) {
                                            outputBuffer.append("0");
                                        }
                                        outputBuffer.insert(0, bufferHelper[0]);
                                        ressources = outputBuffer.toString();
                                    } else {
                                        ressources = ressources.trim().replace(".", "");
                                    }
                                    boolean hasFleetOrDefences = !((messageDefences.getText().trim().equals("Verteidigung: 0")) && (messageFleet.getText().trim().equals("Flotten: 0")));
                                    spyReportContainer.addSpyReport(new SpyReport(new Date(), new Coordinates(Utility.regexString(messageTitle.getText(), "[0-9]+\\:[0-9]+\\:[0-9]+")), Integer.parseInt(ressources), hasFleetOrDefences));
                                    System.out.println("Successfully added Espionage Report: " + messageTitle.getText());
                                } catch (Exception e) {
                                    e.printStackTrace();

                                    System.out.println("Player has Defences or incomplete Espionage Report");
                                }

                            } else if (messageTitle.getText().trim().contains("Spionageaktion auf")) {
                                //WebElement messageContent = driver.findElement(By.xpath("//*[@id=\"fleetsgenericpage\"]/ul/li[" + i + "]/span"));
                                //System.out.println(messageContent.getText());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Page does not contain 50 elements");
                    }
                    clickElement(nextPageButton);
                    Utility.sleep(1000);
                }
                System.out.println("Parsing messages finished.");
                taskFinished = true;
                spyReportContainer.save(Utility.ESPIONAGEFILE);
                System.out.println("Saving Spyreports to Object File");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Issue getting the Messages Count Page");
                System.out.println("Waiting 15 to 22 seconds before reattempting");
                Utility.sleep(10000);
                getUrl(Utility.serverAdress + "/game/index.php?page=messages");
                Utility.sleep(3000);
            } finally {
            }
        }
    }

    public ArrayList<PlayerPlanet> getAccountPlanets() throws NoSuchElementException {
        ArrayList<PlayerPlanet> planets = new ArrayList<PlayerPlanet>();
        Utility.sleep(1000);
        getUrl(Utility.serverAdress + "/game/index.php?page=overview");


        //iterating through all planets+moons and create objects
        List<WebElement> obj = driver.findElements(By.xpath("//*[contains(@id, 'planet-')]/a[1]"));

        for (int i = 0; i < obj.size(); i++) {
            Utility.sleep(3000);
            clickElement(obj.get(i));
            Utility.sleep(1500);
            obj.clear();
            obj = driver.findElements(By.xpath("//*[contains(@id, 'planet-')]/a[1]"));

            System.out.println();
            String url = driver.getCurrentUrl();
            String[] helper = url.split("cp=");
            planets.add(new PlayerPlanet(new Coordinates(Utility.regexString(obj.get(i).getText(), "[0-9]+\\:[0-9]+\\:[0-9]+")), obj.get(i).getText(), helper[1]));
        }
        System.out.println("Planet scanning done");

        obj = driver.findElements(By.className("icon-moon"));

        for (int i = 0; i < obj.size(); i++) {
            Utility.sleep(3000);
            clickElement(obj.get(i));
            Utility.sleep(1500);
            obj.clear();
            obj = driver.findElements(By.className("icon-moon"));

            WebElement coordinatesString = driver.findElement(By.xpath("//*[@id=\"positionContentField\"]/a"));
            WebElement moonName = driver.findElement(By.xpath("//*[@id=\"planetNameHeader\"]"));
            String url = driver.getCurrentUrl();
            String[] planetIdStringHelper = url.split("cp=");
            System.out.println(obj.get(i).getText());
            planets.add(new PlayerPlanet(new Coordinates(Utility.regexString(coordinatesString.getText(), "[0-9]+\\:[0-9]+\\:[0-9]+") + " Moon"), moonName.getTagName().trim(), planetIdStringHelper[1]));
            System.out.println(Utility.regexString(coordinatesString.getText(), "[0-9]+\\:[0-9]+\\:[0-9]+") + " Moon");
        }

        System.out.println("Moon scanning done");
        return planets;

    }

    public void sendFleet(Fleet fleet) {
        System.out.println("Sending Fleet");
        driver.get(Utility.serverAdress + "/game/index.php?page=fleet1&cp=" + fleet.getOrigin());
        Utility.sleep(2000);
        fleet.getShips(driver);
        Utility.sleep(750);
        WebElement fleet1ContinueButton = driver.findElement(By.xpath("//*[@id=\"continue\"]"));
        clickElement(fleet1ContinueButton);

        Utility.sleep(2500);


        WebElement galaxyTextField = driver.findElement(By.xpath("//*[@id=\"galaxy\"]"));
        WebElement systemTextField = driver.findElement(By.xpath("//*[@id=\"system\"]"));
        WebElement planetTextField = driver.findElement(By.xpath("//*[@id=\"position\"]"));
        WebElement speedButton = driver.findElement(By.xpath("//*[@id=\"speedLinks\"]/a[" + fleet.getSpeed() + "]"));

        WebElement fleet2ContinueButton = driver.findElement(By.xpath("//*[@id=\"continue\"]/span"));


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
        if (fleet.getTarget().getPlanetType() == Coordinates.PLANET_TYPE) {
            WebElement selectPlanet = driver.findElement(By.xpath("//*[@id=\"pbutton\"]"));
            selectPlanet.click();
        } else if (fleet.getTarget().getPlanetType() == Coordinates.MOON_TYPE) {
            WebElement selectPlanet = driver.findElement(By.xpath("//*[@id=\"mbutton\"]"));
            selectPlanet.click();
        } else if (fleet.getTarget().getPlanetType() == Coordinates.DEBRIS_TYPE) {
            WebElement selectPlanet = driver.findElement(By.xpath("//*[@id=\"dbutton\"]"));
            selectPlanet.click();
        }

        Utility.sleep(500);


        clickElement(fleet2ContinueButton);

        Utility.sleep(2000);

        WebElement missionButton = driver.findElement(By.xpath("//*[@id=\"missionButton" + fleet.getMission() + "\"]"));
        WebElement sendFleetButton = driver.findElement(By.xpath("//*[@id=\"start\"]/span"));

        if (missionButton.getAttribute("class").equals("off")) {
            return;
        }
        missionButton.click();
        Utility.sleep(900);

        if (fleet.getMetall() != 0) {
            WebElement metalTextField = driver.findElement(By.xpath("//*[@id=\"metal\"]"));
            metalTextField.sendKeys(fleet.getMetall() + "");
            Utility.sleep(500);
        }
        if (fleet.getCrystal() != 0) {
            WebElement crystalTextField = driver.findElement(By.xpath("//*[@id=\"crystal\"]"));
            crystalTextField.sendKeys(fleet.getCrystal() + "");
            Utility.sleep(500);
        }
        if (fleet.getDeuterium() != 0) {
            WebElement deuteriumTextField = driver.findElement(By.xpath("//*[@id=\"deuterium\"]"));
            deuteriumTextField.sendKeys(fleet.getDeuterium() + "");
            Utility.sleep(500);
        }

        sendFleetButton.click();
    }

    public void sendFleetInactiveFast(Fleet fleet) throws Exception {
        Coordinates target = fleet.getTarget();
        getUrl(Utility.serverAdress + "/game/index.php?page=fleet1&galaxy=" + target.getGalaxy() + "&system=" + target.getSystem() + "&position=" + target.getPlanet() + "&type=1&mission=" + fleet.getMission() + "&cp=" + fleet.getOrigin());

        try {
            WebElement playerStatusSpan = driver.findElement(By.xpath("//*[@id=\"fleetStatusBar\"]/ul/li[3]/span[2]"));
            if (playerStatusSpan.getAttribute("class").equals("honorRank rank_starlord3 tooltip") ||
                    playerStatusSpan.getAttribute("class").equals("honorRank rank_starlord2 tooltip") ||
                    playerStatusSpan.getAttribute("class").equals("honorRank rank_bandit3 tooltipHTML")) {
                playerStatusSpan = driver.findElement(By.xpath("//*[@id=\"fleetStatusBar\"]/ul/li[3]/span[3]"));
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


        WebElement slotsUsedSpan = driver.findElement(By.xpath("//*[@id=\"slots\"]/div[1]/span"));
        String[] slotsUsedSpanHelper = slotsUsedSpan.getText().replace("Flotten", "").replace(":", "").trim().split("/");
        int slotsInUse = Integer.parseInt(slotsUsedSpanHelper[0]);
        int maxSlots = Integer.parseInt(slotsUsedSpanHelper[1]);
        boolean fleetSlotsFull = slotsInUse >= (maxSlots);

        while (fleetSlotsFull) {

            System.out.println("All Fleet Slots are FULL waiting 17-27 seconds");
            Utility.sleep(15000);

            getUrl(Utility.serverAdress + "/game/index.php?page=fleet1&galaxy=" + target.getGalaxy() + "&system=" + target.getSystem() + "&position=" + target.getPlanet() + "&type=1&mission=" + fleet.getMission() + "&cp=" + fleet.getOrigin());

            slotsUsedSpan = driver.findElement(By.xpath("//*[@id=\"slots\"]/div[1]/span"));
            slotsUsedSpanHelper = slotsUsedSpan.getText().replace("Flotten", "").replace(":", "").trim().split("/");

            System.out.println("Slots in use: " + slotsUsedSpanHelper[0] + "/ " + slotsUsedSpanHelper[1]);


            slotsInUse = Integer.parseInt(slotsUsedSpanHelper[0]);
            maxSlots = Integer.parseInt(slotsUsedSpanHelper[1]);
            fleetSlotsFull = slotsInUse >= (maxSlots);
        }
        while (fleet.getShips(driver)) {
            System.out.println("No Ships available. Waiting 20-30 seconds");
            Utility.sleep(20000);
            getUrl(Utility.serverAdress + "/game/index.php?page=fleet1&galaxy=" + target.getGalaxy() + "&system=" + target.getSystem() + "&position=" + target.getPlanet() + "&type=1&mission=" + fleet.getMission() + "&cp=" + fleet.getOrigin());
        }


        Utility.sleep(750);
        WebElement fleet1ContinueButton = driver.findElement(By.xpath("//*[@id=\"continue\"]"));

        clickElement(fleet1ContinueButton);

        Utility.sleep(1200);

        if (!driver.getCurrentUrl().equals(Utility.serverAdress + "/game/index.php?page=fleet2")) {
            System.out.println("Not page 2 yet. Clicking again");
            clickElement(fleet1ContinueButton);
        }
        Utility.sleep(2000);

        WebElement speedButton = driver.findElement(By.xpath("//*[@id=\"speedLinks\"]/a[" + fleet.getSpeed() + "]"));
        WebElement fleet2ContinueButton = driver.findElement(By.xpath("//*[@id=\"continue\"]/span"));

        speedButton.click();
        Utility.sleep(250);
        clickElement(fleet2ContinueButton);


        Utility.sleep(2500);
        if (!driver.getCurrentUrl().equals(Utility.serverAdress + "/game/index.php?page=fleet3")) {
            System.out.println("Not page 3 yet. Clicking again");
            clickElement(fleet2ContinueButton);
            Utility.sleep(2500);
        }

        WebElement missionButton = driver.findElement(By.xpath("//*[@id=\"missionButton" + fleet.getMission() + "\"]"));
        WebElement sendFleetButton = driver.findElement(By.xpath("//*[@id=\"start\"]/span"));


        if (missionButton.getAttribute("class").equals("off")) {
            System.out.println("Send Fleet Failure");
            return;
        }

        clickElement(sendFleetButton);

        Utility.sleep(2000);

        if (driver.getCurrentUrl().equals(Utility.serverAdress + "/game/index.php?page=fleet1")) {
            System.out.println("Send Fleet Success");
        } else {
            Utility.sleep(1000);
            System.out.println("Clicking again!");
            clickElement(sendFleetButton);
        }
        Utility.sleep(1000);
    }


    public void espionageFarming(String origin) {
        Fleet fleet;
        SpyReport spyReport;
        SpyReportContainer spyReportContainer = SpyReportContainer.instance();
        ArrayList<SpyReport> spyReports = spyReportContainer.getAllSpyReport(5);
        spyReports.addAll(spyReportContainer.getAllSpyReport(6));
        spyReports.addAll(spyReportContainer.getAllSpyReport(4));
        while (isRunning) {
            if (!isRunning) {
                System.out.println("Turning espionage farming off.");
                break;
            }
            for (int i = 1; i < spyReports.size(); i++) {
                while (isPaused) {
                    Utility.sleep(10000);
                    System.out.println("Paused espionage farming");
                }
                if (!isRunning) {
                    System.out.println("Turning espionage farming off.");
                    break;
                }
                fleet = new Fleet();
                spyReport = spyReports.get(i);
                if (spyReport.hasDefencesOrFleet) {
                    System.out.println("player has defences. Skipping");
                    continue;
                }
                fleet.setOrigin(origin);
                fleet.setSpeed(30);
                fleet.setTarget(spyReport.getCoordinates());
                fleet.setMission(Fleet.ATTACK_MISSION);

                int espionageProbesToSend = ((spyReport.getRessources() / 20) / 10000) * 10000;
                if (espionageProbesToSend < 5000) continue;
                if (espionageProbesToSend < 10000) espionageProbesToSend = 10000;
                if (espionageProbesToSend > 55000) espionageProbesToSend = 60000;

                fleet.setShips(new Pair(Ships.ESPIONAGEPROBE, espionageProbesToSend));

                try {
                    sendFleetInactiveFast(fleet);
                } catch (Exception e) {
                    System.out.println("Something broke. Taking a timeout");
                    e.printStackTrace();
                    Utility.sleep(10000);
                }


                Utility.sleep(1000);
            }
        }
    }

    private String checkAttack() {
        //tooltip eventToggle noAttack for no attack
        //tooltip eventToggle soon for attack
        WebElement attackAlert = driver.findElement(By.xpath("//*[@id=\"attack_alert\"]"));
        if (attackAlert.getAttribute("class").equals("tooltip eventToggle noAttack")) {
            Utility.actionsSinceAttackDetected = 0;
            System.out.println("No Attack detected");
            return null;
        }

        List<WebElement> obj = driver.findElements(By.xpath("//*[contains(@id, 'eventRow-')]"));


        for (int i = 0; i < obj.size(); i++) {
            WebElement tmp = obj.get(i);
            String[] idHelper = tmp.getAttribute("id").trim().split("-");

            String fleetId = idHelper[1];
            WebElement fleetMission = driver.findElement(By.xpath("//*[@id=\"counter-eventlist-" + fleetId + "\"]"));
            if (fleetMission.getAttribute("class").contains("hostile")) {
                System.out.println("Anriff detected");
                WebElement attackTime = driver.findElement(By.xpath("//*[@id=\"eventRow-" + fleetId + "\"]/td[2]"));
                System.out.println(attackTime.getText());
                WebElement time = driver.findElement(By.xpath("//*[@id=\"bar\"]/ul/li[9]"));
                System.out.println(time.getText());

                WebElement tooltip = driver.findElement(By.xpath("//*[@id=\"eventRow-" + fleetId + "\"]/td[7]"));
                System.out.println(tooltip.getText());

                WebElement tooltip2 = driver.findElement(By.xpath("//*[@id=\"eventRow-" + fleetId + "\"]/td[7]/span"));
                System.out.println(tooltip2.getText());

                Utility.actionsSinceAttackDetected++;
                System.out.println("Actions since attack was detected: " + Utility.actionsSinceAttackDetected);
                if (Utility.actionsSinceAttackDetected >= 3 && Utility.actionsSinceAttackDetected <= 6) {

//                    messageMyselfOnDiscord(fleetId);

                }
                if (Utility.actionsSinceAttackDetected == 7) {
                    try {
                        messageAttacker(fleetId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (Utility.actionsSinceAttackDetected > 7) {
                    saveFleet();
                }
                return fleetId;
            }

        }

        //this only happens if its an aks

        attackAlert = driver.findElement(By.xpath("//*[@id=\"attack_alert\"]"));
        if (attackAlert.getAttribute("class").equals("tooltip eventToggle soon")) {
            Utility.actionsSinceAttackDetected++;
            System.out.println("Actions since attack was detected: " + Utility.actionsSinceAttackDetected);
            if (Utility.actionsSinceAttackDetected >= 3 && Utility.actionsSinceAttackDetected <= 7) {

//                messageMyselfOnDiscord(null);

            }
            if (Utility.actionsSinceAttackDetected > 7) {
                saveFleet();
            }
            return null;
        }
        Utility.actionsSinceAttackDetected = 0;
        return null;
    }


    private void messageAttacker(String fleetId) {
        List<String> givenList = Arrays.asList("Hi", "Hi was geht", "Hi bin online.", "Na du?");
        Random rand = new Random();
        String randomElement = givenList.get(rand.nextInt(givenList.size()));
        driver.get(Utility.serverAdress + "/game/index.php?page=overview");
        Utility.sleep(1000);

        try {
            WebElement chatButton = driver.findElement(By.xpath("//*[@id=\"eventRow-" + fleetId + "\"]/td[11]/a/span"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", chatButton);
            Utility.sleep(1000);
            chatButton.click();


            Utility.sleep(5000);

            WebElement chatBox = driver.findElement(By.xpath("//*[@id=\"chatContent\"]/div[2]/div[4]/textarea"));
            chatBox.sendKeys(randomElement);
            Utility.sleep(5000);

            WebElement chatSendButton = driver.findElement(By.xpath("//*[@id=\"chatContent\"]/div[2]/div[4]/a"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", chatSendButton);
            Utility.sleep(1000);
            chatSendButton.click();


            System.out.println("Messaged attacker with random message: " + randomElement);
            Utility.sleep(1000);

        } catch (Exception e) {
            driver.get(Utility.serverAdress + "/game/index.php?page=overview");
            Utility.sleep(2000);
            WebElement chatButton = driver.findElement(By.xpath("//*[@id=\"eventRow-" + fleetId + "\"]/td[11]/a/span"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", chatButton);
            Utility.sleep(1000);
            chatButton.click();
            Utility.sleep(5000);

            WebElement chatBox = driver.findElement(By.xpath("//*[@id=\"chatContent\"]/div[2]/div[3]/textarea"));
            chatBox.sendKeys(randomElement);
            Utility.sleep(5000);

            WebElement chatSendButton = driver.findElement(By.xpath("//*[@id=\"chatContent\"]/div[2]/div[3]/a"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", chatSendButton);
            Utility.sleep(1000);
            chatSendButton.click();


            System.out.println("Messaged attacker with random message: " + randomElement);
            Utility.sleep(1000);
        }
    }


    public void refresh(String planetId) {
        while (true) {
            getUrl(Utility.serverAdress + "/game/index.php?page=overview&cp=" + planetId);
            System.out.println("Sleeping 30-45 seconds");
            Utility.sleep(30000);

        }
    }


    public void saveFleet() {
        WebElement metall = driver.findElement(By.xpath("//*[@id=\"resources_metal\"]"));
        WebElement crystal = driver.findElement(By.xpath("//*[@id=\"resources_crystal\"]"));
        WebElement deuterium = driver.findElement(By.xpath("//*[@id=\"resources_deuterium\"]"));
        Fleet fleet = new Fleet();
        fleet.setOrigin(Utility.activePlanet);
        fleet.setTarget("[5:28:12] Moon");
        fleet.setShips(new Pair(Ships.LIGHTFIGHTER, 999999), new Pair(Ships.HEAVYFIGHTER, 999999), new Pair(Ships.CRUISER, 999999), new Pair(Ships.BATTLESHIP, 999999),
                new Pair(Ships.BATTLECRUISER, 999999), new Pair(Ships.BOMBER, 999999), new Pair(Ships.DESTROYER, 999999), new Pair(Ships.DEATHSTAR, 999999),
                new Pair(Ships.SMALLCARGO, 999999), new Pair(Ships.LARGECARGO, 999999), new Pair(Ships.COLONYSHIP, 999999), new Pair(Ships.RECYCLER, 999999),
                new Pair(Ships.ESPIONAGEPROBE, 999999));
        fleet.setMission(Fleet.DEPLOYMENT_MISSION);
        fleet.setMetall(Integer.parseInt(metall.getText().replace(".", "")));
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
        checkAttack();
    }


    public void clickElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        Utility.sleep(1500);
        element.click();
    }

//    public void messageMyselfOnDiscord(String fleetId) {
//
//        if (fleetId.equals("NoSuchWindow")) {
//            TemmieWebhook temmie = new TemmieWebhook("https://discordapp.com/api/webhooks/468333908564508672/EethBbfriHW0tJj7wpNmXJMujacFTV0LdmYBxjnQL8F6yzX1BsaCab7tTG5eFtdMk8fY");
//            DiscordMessage dm = new DiscordMessage("Ogame Attack Announcement", "<@236917390359789571> Yildun Myspace: NoSuchWindowException.", "http://bdfjade.com/data/out/154/6565174-random-picture.png");
//            temmie.sendMessage(dm);
//            return;
//        }
//
//        if (fleetId == null) {
//            TemmieWebhook temmie = new TemmieWebhook("https://discordapp.com/api/webhooks/468333908564508672/EethBbfriHW0tJj7wpNmXJMujacFTV0LdmYBxjnQL8F6yzX1BsaCab7tTG5eFtdMk8fY");
//            DiscordMessage dm = new DiscordMessage("Ogame Attack Announcement", "<@236917390359789571> Yildun Myspace: Angriff ist ein AKS.", "http://bdfjade.com/data/out/154/6565174-random-picture.png");
//            temmie.sendMessage(dm);
//            return;
//        }
//        driver.get(Utility.serverAdress + "/game/index.php?page=overview");
//        Utility.sleep(1000);
//
//        WebElement attackerKoords = driver.findElement(By.xpath("//*[@id=\"eventRow-" + fleetId + "\"]/td[5]"));
//        WebElement arrivalTime = driver.findElement(By.xpath("//*[@id=\"eventRow-" + fleetId + "\"]/td[2]"));
//        WebElement defenderKoords = driver.findElement(By.xpath("//*[@id=\"eventRow-" + fleetId + "\"]/td[9]"));
//        WebElement shipAmount = driver.findElement(By.xpath("//*[@id=\"eventRow-" + fleetId + "\"]/td[6]"));
//
//        TemmieWebhook temmie = new TemmieWebhook("https://discordapp.com/api/webhooks/468333908564508672/EethBbfriHW0tJj7wpNmXJMujacFTV0LdmYBxjnQL8F6yzX1BsaCab7tTG5eFtdMk8fY");
//        DiscordMessage dm = new DiscordMessage("Ogame Attack Announcement", "<@236917390359789571> Yildun Myspace: Angriff kommt von: " + attackerKoords.getText() + " und schlaegt um "
//                + arrivalTime.getText() + " auf " + defenderKoords.getText() + " mit " + shipAmount.getText() + " Schiffen ein.", "http://bdfjade.com/data/out/154/6565174-random-picture.png");
//        temmie.sendMessage(dm);
//    }
}
