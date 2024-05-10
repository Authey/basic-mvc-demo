package basic.mvc.utility.exception;

public class CryptoProcessFailedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CryptoProcessFailedException(String s, Throwable e) {
        super(s, e);
    }

}
