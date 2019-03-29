package de.crystalldev.models;

import lombok.Getter;

import java.io.Serializable;

public class Coordinates implements Cloneable, Serializable {

    private static final long serialVersionUID = 9208573032729082194L;

    @Getter
    private int galaxy, system, planet;
    @Getter
    private PlanetType planetType;

    public Coordinates(String coord) {
        String helper = coord;
        this.planetType = PlanetType.PLANET;
        try {
            if (helper.contains("Moon")) {
                helper = helper.replace("Moon", "").trim();
                this.planetType = PlanetType.MOON;
            } else if (helper.contains("Debris")) {
                helper = helper.replace("Debris", "").trim();
                this.planetType = PlanetType.DEBRIS;
            }
            helper = helper.replace("[", "").replace("]", "").trim();
            String[] helper2 = helper.split(":");
            this.galaxy = Integer.parseInt(helper2[0]);
            this.system = Integer.parseInt(helper2[1]);
            this.planet = Integer.parseInt(helper2[2]);
        } catch (NumberFormatException e) {
            System.out.println("Error Parsing Coordinates @public Coordinates(String coord)");
            System.out.println(e.getMessage());
        }

    }


    public Coordinates(int galaxy, int system, int planet, PlanetType planetType) {
        this.galaxy = galaxy;
        this.system = system;
        this.planet = planet;
        this.planetType = planetType;
    }

    public String toString() {
        StringBuilder helper = new StringBuilder("[" + this.galaxy + ":" + this.system + ":" + this.planet + "]");
        if (planetType == PlanetType.PLANET) {
            helper.append(" Planet");
            return helper.toString();
        } else if (planetType == PlanetType.DEBRIS) {
            helper.append(" Debris");
            return helper.toString();
        } else if (planetType == PlanetType.MOON) {
            helper.append(" Moon");
            return helper.toString();
        }
        return null;
    }

    public boolean isMoon() {
        return this.planetType == PlanetType.MOON;
    }

    public static boolean isCoordinates(String temp) {
        if (temp.matches("[0-9]+\\:[0-9]+\\:[0-9]") || temp.matches("[[0-9]+\\:[0-9]+\\:[0-9]]") || temp.matches("[[0-9]+\\:[0-9]+\\:[0-9]] Moon"))
            return true;
        return false;
    }

    public int compareTo(final Coordinates o) {
        return Integer.compare(this.galaxy, o.galaxy);
    }

    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Coordinates)) return false;
        Coordinates otherCoordinates = (Coordinates) o;
        return this.getGalaxy() == otherCoordinates.getGalaxy() && this.getSystem() == otherCoordinates.getSystem()
                && this.getPlanet() == otherCoordinates.getPlanet() && this.getPlanetType() == otherCoordinates.getPlanetType();
    }

    public enum PlanetType {
        PLANET, DEBRIS, MOON
    }
}