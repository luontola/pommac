package net.orfjackal.pommac;

import java.io.File;
import java.util.Arrays;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
public class UnambiguousQueryException extends RuntimeException {

    public UnambiguousQueryException(String query, File[] found) {
        super("Unambiguous query: " + query + "\n" +
                "More than one match: " + Arrays.asList(found));
    }
}
