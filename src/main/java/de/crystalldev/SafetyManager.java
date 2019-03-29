package de.crystalldev;

/**
 * Created by Crystall on 03/23/2019
 */
public class SafetyManager {
    //TODO add all stuff that has to do with Safety (saving fleet, resources etc.)

    //TODO add discord in config, check for existing url and try to send messages
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
//        driver.get(de.crystalldev.utils.Utility.serverAddress + "/game/index.php?page=overview");
//        de.crystalldev.utils.Utility.sleep(1000);
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
