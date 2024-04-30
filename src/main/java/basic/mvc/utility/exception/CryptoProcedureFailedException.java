package basic.mvc.utility.exception;

public class CryptoProcedureFailedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CryptoProcedureFailedException(String s) {
        super(s);
    }

}
