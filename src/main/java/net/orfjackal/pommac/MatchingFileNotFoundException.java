package net.orfjackal.pommac;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
public class MatchingFileNotFoundException extends RuntimeException {

    public MatchingFileNotFoundException(String query) {
        super(toMessage(query));
    }

    public MatchingFileNotFoundException(String query, Throwable cause) {
        super(toMessage(query), cause);
    }

    private static String toMessage(String query) {
        return "No file matching: " + query;
    }
}
