package basic.mvc.utility.exception;

public class SessionTokenExpiredException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SessionTokenExpiredException(String s) {
        super(s);
    }

}
