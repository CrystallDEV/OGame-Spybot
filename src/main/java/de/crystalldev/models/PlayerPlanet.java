package de.crystalldev.models;

import lombok.Getter;

public class PlayerPlanet {
    @Getter Coordinates coordinates;
    @Getter String name, id;

    public PlayerPlanet(Coordinates coordinates, String name, String id) {
        this.coordinates = coordinates;
        this.name = name;
        this.id = id;
    }
}
