package net.orfjackal.pommac;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
public class MatchingFileNotFoundException extends RuntimeException {
    
    public MatchingFileNotFoundException(String query) {
        super("No file matching: " + query);
    }
}
