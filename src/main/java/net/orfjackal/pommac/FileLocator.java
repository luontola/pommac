package net.orfjackal.pommac;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
public class FileLocator {

    public static File findFile(File dir, String query) {
        String[] parts = query.split("/");
        assert parts.length > 0;
        File current = dir;
        for (String part : parts) {
            current = findFile(current, part, query);
        }
        return current;
    }

    private static File findFile(File dir, String nameQuery, String fullQuery) {
        final Pattern pattern = Pattern.compile(toRegex(nameQuery));
        File[] found = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return pattern.matcher(file.getName()).matches();
            }
        });
        if (found.length == 0) {
            throw new MatchingFileNotFoundException(fullQuery);
        } else if (found.length > 1) {
            throw new UnambiguousQueryException(fullQuery, found);
        }
        return found[0];
    }

    private static String toRegex(String query) {
        StringBuilder regex = new StringBuilder();
        for (char c : query.toCharArray()) {
            if (c == '*') {
                regex.append(".*");
            } else if (c == '?') {
                regex.append(".");
            } else {
                regex.append(Pattern.quote(Character.toString(c)));
            }
        }
        return regex.toString();
    }
}
