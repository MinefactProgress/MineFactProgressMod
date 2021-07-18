package de.jannik0308.minefactprogressmod.utils.scanmap;

public class Coordinate {

    private final String latitude, longitude;

    public Coordinate(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "{\"lat\":\"" + latitude + "\",\"long\":\"" + longitude + "\"}";
    }
}
