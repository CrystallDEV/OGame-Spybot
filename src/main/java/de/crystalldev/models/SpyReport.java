package de.crystalldev.models;

import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

public class SpyReport implements Serializable {
    @Getter private Date scanDate;
    @Getter private Coordinates coordinates;
    @Getter private int resources;
    @Getter private boolean hasDefencesOrFleet;
    private static final long serialVersionUID = 9208573032759089194L;

    public SpyReport(Date scanDate, Coordinates coordinates, int resources, boolean hasDefencesOrFleet) {
        this.scanDate = scanDate;
        this.coordinates = coordinates;
        this.resources = resources;
        this.hasDefencesOrFleet = hasDefencesOrFleet;
    }
}
