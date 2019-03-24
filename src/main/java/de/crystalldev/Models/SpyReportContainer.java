package de.crystalldev.Models;

import de.crystalldev.Util.Settings;

import java.io.*;
import java.util.ArrayList;

public class SpyReportContainer implements Serializable {
    private static SpyReportContainer unique = null;
    private ArrayList<SpyReport> allSpyReport;

    private SpyReportContainer() {
        unique = load(Settings.ESPIONAGEFILE);
        if (this.allSpyReport == null) {
            allSpyReport = new ArrayList<>();
        }
    }

    public static SpyReportContainer instance() {
        if (unique == null)
            unique = new SpyReportContainer();
        return unique;
    }

    public void addSpyReport(SpyReport spyReport) {
        if (this.getSpyReport(spyReport.getCoordinates()) != null) {
            System.out.println("Duplicate Spyreport detected. Replacing old Spyreport with current one.");
            this.removeSpyReport(spyReport.getCoordinates());
        }
        allSpyReport.add(spyReport);
    }

    public ArrayList<SpyReport> getAllSpyReport(int galaxy) {
        ArrayList<SpyReport> tmp = new ArrayList<>();
        for (SpyReport spyReport : allSpyReport) {
            if (spyReport.getCoordinates().getGalaxy() == galaxy) {
                tmp.add(spyReport);
            }
        }
        return tmp;
    }

    private ArrayList<SpyReport> getAllSpyReport() {
        return allSpyReport;
    }

    private SpyReport getSpyReport(Coordinates coordinates) {
        for (SpyReport spyReport : allSpyReport) {
            //System.out.println("Found Spy Report with coordinates" + spyReport.getCoordinates().toString());
            if (spyReport.getCoordinates().equals(coordinates)) {
                return spyReport;
            }
        }
        return null;
    }

    public void clear(int galaxy) {
        allSpyReport.removeIf(spyReport -> spyReport.getCoordinates().getGalaxy() == galaxy);
    }

    public void clearAll() {
        allSpyReport.clear();
    }

    public void removeSpyReport(Coordinates coordinates) {
        allSpyReport.removeIf(spyReport -> spyReport.getCoordinates().equals(coordinates));
    }

    public SpyReportContainer load(String fileName) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
            unique = (SpyReportContainer) ois.readObject();
            ois.close();
            System.out.println("Loading file " + fileName + " success!");
            this.allSpyReport = unique.getAllSpyReport();
            return unique;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldn't find file " + fileName);
        }
        return null;
    }

    public void save(String fileName) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
            oos.writeObject(unique);
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
