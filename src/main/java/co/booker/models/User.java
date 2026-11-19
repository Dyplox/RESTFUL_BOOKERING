package co.booker.models;

import co.booker.models.builder.UserBuilder;

/**
 * Modelo de usuario heredado de la plantilla original del proyecto (DummyApi).
 *
 * <p>No se usa en las pruebas de Restful Booker.</p>
 */
public class User {
    private String id;
    private String title;
    private String firstName;
    private String lastName;
    private String gender;
    private String email;
    private String dateOfBirth;
    private String registerDate;
    private String phone;
    private String picture;
    private Location location;

    public User() {
    }

    /**
     * Crea un usuario a partir de un builder.
     *
     * @param builder builder con nombre, apellido, email y ubicación
     */
    public User(UserBuilder builder) {
        this.firstName = builder.getFirstName();
        this.lastName = builder.getLastName();
        this.email = builder.getEmail();
        this.location = builder.getLocation();
    }

    /**
     * Crea un usuario solo con identificador.
     *
     * @param id identificador del usuario
     */
    public User(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public String getPhone() {
        return phone;
    }

    public String getPicture() {
        return picture;
    }

    public Location getLocation() {
        return location;
    }


    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", location=" + location +
                '}';
    }
}