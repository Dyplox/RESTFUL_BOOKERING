package co.booker.utils.endpoints;

public enum uris {

    AUTH("auth"),
    BOOKING("booking/"),
    PING("ping");

    private final String endpoint;

    uris(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String toString() {
        return endpoint;
    }
}