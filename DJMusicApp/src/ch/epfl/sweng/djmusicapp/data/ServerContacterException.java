package ch.epfl.sweng.djmusicapp.data;

/**
 * Thrown to indicate a problem encountered when contacting the server through a
 * ServerContacter.
 * 
 * @author csbenz
 * 
 */
public class ServerContacterException extends Exception {

    private static final long serialVersionUID = 1L;

    public ServerContacterException() {
        super();
    }

    public ServerContacterException(String message) {
        super(message);
    }

    public ServerContacterException(Throwable throwable) {
        super(throwable);
    }
}
