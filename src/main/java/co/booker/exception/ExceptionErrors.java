package co.booker.exception;

public class ExceptionErrors extends AssertionError{
    public static final String APP_ID_NOT_EXIST = "app-id header is found but the value is not valid";

    public static final String APP_ID_MISSING = "app-id header is not set correctly";

    public static final String PARAMS_NOT_VALID = "param format is invalid, param is not found";
    public static final String BODY_NOT_VALID = "Boyd format is invalid";
    public static final String RESOURCE_NOT_FOUND = "Works correctly if you try to create a post for user that not exist or deleted.";
    public static final String PATH_NOT_FOUND = "Request path is not valid";
    public static final String SERVER_ERROR = "Something is wrong with server, try again later";


    public static final String TIME_GREATER = "Tiempo es mayor a un segundo (1sg)";

    public ExceptionErrors(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
