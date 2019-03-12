public class PlayerPlanet {
    Coordinates coordinates;
    String name, id;

    public PlayerPlanet(Coordinates coordinates, String name, String id) {
        this.coordinates = coordinates;
        this.name = name;
        this.id = id;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
