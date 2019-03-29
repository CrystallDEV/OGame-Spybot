package de.crystalldev;

import de.crystalldev.utils.Utility;
import de.crystalldev.fleet.Fleet;
import de.crystalldev.fleet.FleetManager;
import de.crystalldev.fleet.Ships;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import de.crystalldev.models.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BrowserManager {
    @Getter
    private WebDriver driver;
    @Getter @Setter
    private boolean isRunning = false;
    @Getter @Setter
    private boolean isPaused = false;

    private static BrowserManager unique = null;

    public static BrowserManager getInstance() {
        if (unique == null) {
            unique = new BrowserManager();
        }
        return unique;
    }

    private BrowserManager() {
        System.setProperty("webdriver.chrome.driver", new File(getClass().getClassLoader().getResource("chromedriver5.exe").getFile()).getAbsolutePath());
        System.out.println(new File(getClass().getClassLoader().getResource("chromedriver5.exe").getFile()).getAbsolutePath());
        ChromeOptions options = new ChromeOptions();
        //  options.addArguments("load-extension=C:\\Users\\Chris\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\Extensions\\cjpalhdlnbpafiamejdnhcphjbkeiagm\\1.16.10_0");
        options.addArguments("--start-maximized");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        driver = new ChromeDriver(capabilities);
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

                this.loginLobby();
                Utility.sleep(3000);
                this.loginUniverse();
                Utility.sleep(3000);
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
            e.printStackTrace();
//          messageMyselfOnDiscord("NoSuchWindow");
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

    /**
     * Logs the player into the lobby
     */
    public void loginLobby() {
        try {
            driver.get("https://de.ogame.gameforge.com/");
            Utility.sleep(2000);
            WebElement loginButton = driver.findElement(By.id("ui-id-1"));
            clickElement(loginButton);
            Utility.sleep(1000);
            WebElement userName = driver.findElement(By.id("usernameLogin"));
            WebElement password = driver.findElement(By.id("passwordLogin"));
            WebElement loginSubmitButton = driver.findElement(By.id("loginSubmit"));
            userName.sendKeys(SettingsManager.getInstance().getEMail());
            Utility.sleep(1000);
            password.sendKeys(SettingsManager.getInstance().getPassword());
            Utility.sleep(1000);
            loginSubmitButton.submit();
            Utility.sleep(3000);
        } catch (Exception e) {
            //TODO handle not being able to login
        }
    }

    public void loginUniverse() {
        switchTab("OGame Lobby");
        Utility.sleep(2000);
        boolean loopHelper = true;
        int i = 1;
        try {
            while (loopHelper) {
                WebElement serverNameField = driver.findElement(By.xpath("//*[@id=\"accountlist\"]/div/div[1]/div[2]/div[" + i + "]/div/div[4]/div"));
                WebElement userNameField = driver.findElement(By.xpath("//*[@id=\"accountlist\"]/div/div[1]/div[2]/div[" + i + "]/div/div[9]"));
                WebElement accountLoginButton = driver.findElement(By.xpath("//*[@id=\"accountlist\"]/div/div[1]/div[2]/div[" + i + "]/div/div[11]/button"));

                if (serverNameField.getText().trim().equals(SettingsManager.getInstance().getServer()) && userNameField.getText().trim().equals(SettingsManager.getInstance().getUserName())) {
                    loopHelper = false;
                    clickElement(accountLoginButton);
                    Utility.sleep(3000);
                    switchTab(SettingsManager.getInstance().getServer() + " OGame");
                    printCookies();
                    Utility.sleep(3000);

                    SettingsManager.getInstance().setServerAddress(Utility.regexString(driver.getCurrentUrl(), "https:\\/\\/s\\d+\\-.+\\.ogame\\.gameforge\\.com"));
                    System.out.println("Setting Server URL to " + SettingsManager.getInstance().getServerAddress());

                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Invalid Username or Server");
        }
    }

    /**
     * @param galaxy   the galaxy scanning in
     * @param lower    the starting sunsystem
     * @param upper    the ending sunsystem
     * @param planetId
     */
    public void scanGalaxy(int galaxy, int lower, int upper, String planetId) {
        deleteAllEspionageMessages();
        Utility.sleep(1000);

        for (int i = galaxy; i <= galaxy; i++) {
            for (int j = lower; j <= upper; j++) {
                try {
                    getUrl(SettingsManager.getInstance().getServerAddress() + "/game/index.php?page=galaxy&cp=" + planetId + "&galaxy=" + i + "&system=" + j);

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
                                    System.out.println("All Fleet Slots are FULL waiting 17-27 seconds");
                                    Utility.sleep(15000);
                                    getUrl(SettingsManager.getInstance().getServerAddress() + "/game/index.php?page=galaxy&cp=" + planetId + "&galaxy=" + i + "&system=" + j);
                                    slotsUsedSpan = driver.findElement(By.xpath("//*[@id=\"slotValue\"]"));
                                    slotsUsedSpanHelper = slotsUsedSpan.getText().trim().split("/");
                                    Utility.sleep(100);
                                    System.out.println(slotsUsedSpanHelper[0] + " " + slotsUsedSpanHelper[1]);
                                    fleetSlotsFull = slotsUsedSpanHelper[0].equals(slotsUsedSpanHelper[1]);
                                    Utility.sleep(100);
                                }

                                WebElement spyCountElement = driver.findElement(By.xpath("//*[@id=\"probeValue\"]"));
                                if (spyCountElement != null) {
                                    int spyCount = Integer.parseInt(spyCountElement.getText());
                                    while (spyCount <= SettingsManager.getInstance().getProbesPerSpy()) {
                                        System.out.println("There are no free probes. Waiting 17-27 seconds");
                                        Utility.sleep(15000);
                                        getUrl(SettingsManager.getInstance().getServerAddress() + "/game/index.php?page=galaxy&cp=" + planetId + "&galaxy=" + i + "&system=" + j);
                                        spyCountElement = driver.findElement(By.xpath("//*[@id=\"slotValue\"]"));
                                        Utility.sleep(100);
                                        spyCount = Integer.parseInt(spyCountElement.getText());
                                        Utility.sleep(100);
                                    }
                                }

                                WebElement planetEspionageButton = driver.findElement(By.xpath("//*[@id=\"galaxytable\"]/tbody/tr[" + k + "]/td[8]/span/a[1]/span"));
                                System.out.println("Spying [" + i + ":" + j + ":" + k + "] " + galaxyPlayerRow.getText() + " " + playerStatus.getText());
                                clickElement(planetEspionageButton);
                                Utility.sleep(2000);
                            }

                        } catch (Exception e) {
//                            System.out.println("No Player on [" + i + ":" + j + ":" + k + "]");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Issue finding Galaxy/Solarsystem Textfields");
                    System.out.println("Waiting 15 to 22 seconds before reattempting");
                    Utility.sleep(10000);
                    getUrl(SettingsManager.getInstance().getServerAddress() + "/game/index.php?page=galaxy&cp=" + planetId + "&galaxy=" + i + "&system=" + j);
                    j--;
                    Utility.sleep(3000);
                }
            }
        }
    }

    public void deleteAllEspionageMessages() {
        try {
            Utility.sleep(1000);
            getUrl(SettingsManager.getInstance().getServerAddress() + "/game/index.php?page=messages");
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
                getUrl(SettingsManager.getInstance().getServerAddress() + "/game/index.php?page=messages");

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
                spyReportContainer.save(SettingsManager.getInstance().getEspionageFile());
                System.out.println("Saving Spyreports to Object File");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Issue getting the Messages Count Page");
                System.out.println("Waiting 15 to 22 seconds before reattempting");
                Utility.sleep(10000);
                getUrl(SettingsManager.getInstance().getServerAddress() + "/game/index.php?page=messages");
                Utility.sleep(3000);
            }
        }
    }

    public ArrayList<PlayerPlanet> getAccountPlanets() throws NoSuchElementException {
        ArrayList<PlayerPlanet> planets = new ArrayList<>();
        Utility.sleep(1000);
        getUrl(SettingsManager.getInstance().getServerAddress() + "/game/index.php?page=overview");

        //iterating through all planets+moons and create objects
        List<WebElement> obj = driver.findElements(By.xpath("//*[contains(@id, 'planet-')]/a[1]"));

        for (int i = 0; i < obj.size(); i++) {
            Utility.sleep(200);
            clickElement(obj.get(i));
            Utility.sleep(1000);
            obj.clear();
            obj = driver.findElements(By.xpath("//*[contains(@id, 'planet-')]/a[1]"));
            String url = driver.getCurrentUrl();
            String[] helper = url.split("cp=");
            PlayerPlanet planet = new PlayerPlanet(
                    new Coordinates(Utility.regexString(obj.get(i).getText(), "[0-9]+\\:[0-9]+\\:[0-9]+")),
                    obj.get(i).getText(), helper[1]
            );
            planets.add(planet);
            System.out.println(planet.getName());
        }
        System.out.println("Planet scanning done");
        obj = driver.findElements(By.className("icon-moon"));

        for (int i = 0; i < obj.size(); i++) {
            Utility.sleep(200);
            clickElement(obj.get(i));
            Utility.sleep(1500);
            obj.clear();
            obj = driver.findElements(By.className("icon-moon"));
            WebElement coordinatesString = driver.findElement(By.xpath("//*[@id=\"positionContentField\"]/a"));
            WebElement moonName = driver.findElement(By.xpath("//*[@id=\"planetNameHeader\"]"));
            String url = driver.getCurrentUrl();
            String[] planetIdStringHelper = url.split("cp=");
            PlayerPlanet planet = new PlayerPlanet(
                    new Coordinates(Utility.regexString(coordinatesString.getText(), "[0-9]+\\:[0-9]+\\:[0-9]+") + " Moon"),
                    moonName.getTagName().trim(),
                    planetIdStringHelper[1]
            );
            planets.add(planet);
            System.out.println(planet.getCoordinates());
        }

        System.out.println("Moon scanning done");
        return planets;
    }

    public void espionageFarming(String origin) {
        Fleet fleet;
        SpyReport spyReport;
        SpyReportContainer spyReportContainer = SpyReportContainer.instance();
        ArrayList<SpyReport> spyReports = spyReportContainer.getAllSpyReport(5);
        spyReports.addAll(spyReportContainer.getAllSpyReport(6));
        spyReports.addAll(spyReportContainer.getAllSpyReport(4));
        while (isRunning) {
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
                if (spyReport.isHasDefencesOrFleet()) {
                    System.out.println("player has defences. Skipping");
                    continue;
                }
                fleet.setOrigin(origin);
                fleet.setSpeed(30);
                fleet.setTarget(spyReport.getCoordinates());
                fleet.setMission(Fleet.ATTACK_MISSION);

                int espionageProbesToSend = ((spyReport.getResources() / 20) / 10000) * 10000;
                if (espionageProbesToSend < 5000) {
                    continue;
                }
                if (espionageProbesToSend < 10000) {
                    espionageProbesToSend = 10000;
                }
                if (espionageProbesToSend > 55000) {
                    espionageProbesToSend = 60000;
                }

                fleet.setShips(new Pair(Ships.ESPIONAGEPROBE, espionageProbesToSend));

                try {
                    FleetManager.getInstance().sendFleetInactiveFast(fleet);
                } catch (Exception e) {
                    System.out.println("Something broke. Taking a timeout");
                    e.printStackTrace();
                    Utility.sleep(10000);
                }

                Utility.sleep(1000);
            }
        }
    }

    /**
     * Checks for existing attacks on any planet
     * @return
     */
    public String checkAttack() {
        //tooltip eventToggle noAttack for no attack
        //tooltip eventToggle soon for attack
        WebElement attackAlert = driver.findElement(By.xpath("//*[@id=\"attack_alert\"]"));
        if (attackAlert.getAttribute("class").equals("tooltip eventToggle noAttack")) {
            SettingsManager.getInstance().setActionsSinceAttackDetected(0);
            System.out.println("No Attack detected");
            return null;
        }

        List<WebElement> obj = driver.findElements(By.xpath("//*[contains(@id, 'eventRow-')]"));

        for (WebElement tmp : obj) {
            String[] idHelper = tmp.getAttribute("id").trim().split("-");

            String fleetId = idHelper[1];
            WebElement fleetMission = driver.findElement(By.xpath("//*[@id=\"counter-eventlist-" + fleetId + "\"]"));
            if (fleetMission.getAttribute("class").contains("hostile")) {
                System.out.println("Attack detected");
                WebElement attackTime = driver.findElement(By.xpath("//*[@id=\"eventRow-" + fleetId + "\"]/td[2]"));
                System.out.println(attackTime.getText());
                WebElement time = driver.findElement(By.xpath("//*[@id=\"bar\"]/ul/li[9]"));
                System.out.println(time.getText());

                WebElement tooltip = driver.findElement(By.xpath("//*[@id=\"eventRow-" + fleetId + "\"]/td[7]"));
                System.out.println(tooltip.getText());

                WebElement tooltip2 = driver.findElement(By.xpath("//*[@id=\"eventRow-" + fleetId + "\"]/td[7]/span"));
                System.out.println(tooltip2.getText());

                SettingsManager.getInstance().setActionsSinceAttackDetected(SettingsManager.getInstance().getActionsSinceAttackDetected() + 1);
                System.out.println("Actions since attack was detected: " + SettingsManager.getInstance().getActionsSinceAttackDetected());
                if (SettingsManager.getInstance().getActionsSinceAttackDetected() >= 3 && SettingsManager.getInstance().getActionsSinceAttackDetected() <= 6) {
//                    messageMyselfOnDiscord(fleetId);
                }
                if (SettingsManager.getInstance().getActionsSinceAttackDetected() == 7) {
                    try {
                        messageAttacker(fleetId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (SettingsManager.getInstance().getActionsSinceAttackDetected() > 7) {
                    FleetManager.getInstance().saveFleet();
                }
                return fleetId;
            }
        }

        //this only happens if its an aks
        attackAlert = driver.findElement(By.xpath("//*[@id=\"attack_alert\"]"));
        if (attackAlert.getAttribute("class").equals("tooltip eventToggle soon")) {
            SettingsManager.getInstance().setActionsSinceAttackDetected(SettingsManager.getInstance().getActionsSinceAttackDetected() + 1);
            System.out.println("Actions since attack was detected: " + SettingsManager.getInstance().getActionsSinceAttackDetected());
            if (SettingsManager.getInstance().getActionsSinceAttackDetected() >= 3 && SettingsManager.getInstance().getActionsSinceAttackDetected() <= 7) {
//                messageMyselfOnDiscord(null);
            }
            if (SettingsManager.getInstance().getActionsSinceAttackDetected() > 7) {
                FleetManager.getInstance().saveFleet();
            }
            return null;
        }
        SettingsManager.getInstance().setActionsSinceAttackDetected(0);
        return null;
    }

    /**
     * Sends a random mesage to the attacker
     * @param fleetId
     */
    private void messageAttacker(String fleetId) {
        List<String> givenList = Arrays.asList("Hi", "Hi was geht", "Hi bin online.", "Na du?");
        Random rand = new Random();
        String randomElement = givenList.get(rand.nextInt(givenList.size()));
        driver.get(SettingsManager.getInstance().getServerAddress() + "/game/index.php?page=overview");
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
            driver.get(SettingsManager.getInstance().getServerAddress() + "/game/index.php?page=overview");
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
            getUrl(SettingsManager.getInstance().getServerAddress() + "/game/index.php?page=overview&cp=" + planetId);
            System.out.println("Sleeping 30-45 seconds");
            Utility.sleep(30000);

        }
    }

    /**
     * Clicks an element on the screen
     * @param element
     */
    public void clickElement(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            Utility.sleep(1500);
            element.click();
        } catch (Exception e) {
            System.out.println("Could not find element: " + element.getText());
        }
    }

}
