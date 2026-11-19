package co.booker.models;

import co.booker.models.builder.LocationBuilder;

/** Ubicación de un {@link User}. Heredada de la plantilla original; no se usa en las pruebas. */
public class Location {
    private String street;
    private String city;
    private String state;
    private String country;
    private String timezone;

    /**
     * Crea la ubicación a partir de un builder.
     *
     * @param builder builder con calle, ciudad, estado, país y zona horaria
     */
    public Location(LocationBuilder builder) {
        this.street = builder.getStreet();
        this.city = builder.getCity();
        this.state = builder.getState();
        this.country = builder.getCountry();
        this.timezone = builder.getTimezone();
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
}