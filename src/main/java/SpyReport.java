import java.io.Serializable;
import java.util.Date;

public class SpyReport implements Serializable {

    Date scanDate;
    Coordinates coordinates;
    int ressources;
    boolean hasDefencesOrFleet;
    private static final long serialVersionUID = 9208573032759089194L;

    public SpyReport(Date scanDate, Coordinates coordinates, int ressources, boolean hasDefencesOrFleet) {
        this.scanDate = scanDate;
        this.coordinates = coordinates;
        this.ressources = ressources;
        this.hasDefencesOrFleet = hasDefencesOrFleet;
    }

    public Date getScanDate() {
        return scanDate;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public int getRessources() {
        return ressources;
    }

    public boolean isHasDefencesOrFleet() {
        return hasDefencesOrFleet;
    }
}
