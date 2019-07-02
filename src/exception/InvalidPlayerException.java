package exception;

/**
 * Custom exception used in PlayerHolder and FreePlayres. The exception is used
 * when a player choose opponent by id(number) and the number is invalid or
 * non-existing.
 */
public class InvalidPlayerException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public InvalidPlayerException() {
    }

    public InvalidPlayerException(String arg0) {
        super(arg0);
    }

    public InvalidPlayerException(Throwable arg0) {
        super(arg0);
    }

    public InvalidPlayerException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public InvalidPlayerException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }

}
