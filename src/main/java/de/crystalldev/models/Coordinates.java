package de.crystalldev.models;

import java.io.Serializable;

public class Coordinates implements Cloneable, Serializable {

    public static final int PLANET_TYPE = 1;
    public static final int DEBRIS_TYPE = 2;
    public static final int MOON_TYPE = 3;
    private static final long serialVersionUID = 9208573032729082194L;

    private int galaxy, system, planet, planetType;

    public int getGalaxy() {
        return this.galaxy;
    }

    public int getSystem() {
        return this.system;
    }

    public int getPlanet() {
        return this.planet;
    }

    public int getPlanetType() {
        return this.planetType;
    }

    public Coordinates(String coord) {
        String helper = coord;
        this.planetType = PLANET_TYPE;
        try {
            if (helper.contains("Moon")) {
                helper = helper.replace("Moon", "").trim();
                this.planetType = MOON_TYPE;
            } else if (helper.contains("Debris")) {
                helper = helper.replace("Debris", "").trim();
                this.planetType = DEBRIS_TYPE;
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


    public Coordinates(int galaxy, int system, int planet, int planetType) {
        this.galaxy = galaxy;
        this.system = system;
        this.planet = planet;
        this.planetType = planetType;
    }

    public String toString() {
        StringBuilder helper = new StringBuilder("[" + this.galaxy + ":" + this.system + ":" + this.planet + "]");
        if (planetType == PLANET_TYPE) {
            return helper.toString();
        } else if (planetType == DEBRIS_TYPE) {
            helper.append(" Debris");
            return helper.toString();
        } else if (planetType == MOON_TYPE) {
            helper.append(" Moon");
            return helper.toString();
        }
        return null;
    }

    public boolean isMoon() {
        return this.planetType == MOON_TYPE;
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
}