package co.booker.models.builder;

import co.booker.models.Location;
import co.booker.utils.Builder;

public class LocationBuilder implements Builder<Location> {
    private String street;
    private String city;
    private String state;
    private String country;
    private String timezone;

    public LocationBuilder(){
        this.street = "47A";
        this.city = "Manizales";
        this.state = "Caldas";
        this.country = "Colombia";
        this.timezone = "UTC-5";
    }

    public static Location byDefault(){
        return new LocationBuilder().build();
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getTimezone() {
        return timezone;
    }

    @Override
    public Location build() {
        return new Location(this);
    }
}