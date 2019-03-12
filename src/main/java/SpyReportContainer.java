import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class SpyReportContainer implements Serializable {
    private static final long serialVersionUID = 7219632990740128143L;
    private static SpyReportContainer unique = null;
    private ArrayList<SpyReport> allSpyReport;

    private SpyReportContainer() {
        unique = load(Utility.ESPIONAGEFILE);
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
        ArrayList<SpyReport> tmp = new ArrayList<SpyReport>();
        for (Iterator<SpyReport> iterator = allSpyReport.iterator(); iterator.hasNext(); ) {
            SpyReport spyReport = iterator.next();
            if (spyReport.getCoordinates().getGalaxy() == galaxy) {
                tmp.add(spyReport);
            }
        }
        return tmp;
    }

    public ArrayList<SpyReport> getAllSpyReport() {
        return allSpyReport;
    }

    public SpyReport getSpyReport(Coordinates coordinates) {
        for (Iterator<SpyReport> iterator = allSpyReport.iterator(); iterator.hasNext(); ) {
            SpyReport spyReport = iterator.next();
            //System.out.println("Found Spy Report with coordinates" + spyReport.getCoordinates().toString());
            if (spyReport.getCoordinates().equals(coordinates)) {
                return spyReport;
            }
        }
        return null;
    }

    public void clear(int galaxy) {
        for (Iterator<SpyReport> iterator = allSpyReport.iterator(); iterator.hasNext(); ) {
            SpyReport spyReport = iterator.next();
            if (spyReport.getCoordinates().getGalaxy() == galaxy) {
                iterator.remove();
            }
        }
    }

    public void clearAll() {
        allSpyReport.clear();
    }

    public void removeSpyReport(Coordinates coordinates) {
        for (Iterator<SpyReport> iterator = allSpyReport.iterator(); iterator.hasNext(); ) {
            SpyReport spyReport = iterator.next();
            if (spyReport.getCoordinates().equals(coordinates)) {
                iterator.remove();
            }
        }
    }

    public SpyReportContainer load(String fileName) {
        try {
            ObjectInputStream ois = null;
            ois = new ObjectInputStream(new FileInputStream(fileName));
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
            ObjectOutputStream oos = null;
            oos = new ObjectOutputStream(new FileOutputStream(fileName));
            oos.writeObject(unique);
            oos.close();
        } catch (Exception e) {
        }
    }
}
