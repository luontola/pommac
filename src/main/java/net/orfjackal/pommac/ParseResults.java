package net.orfjackal.pommac;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
public class ParseResults {

    public String defaultVersion;
    public List<Artifact> artifacts = new ArrayList<Artifact>();

    public void processTags() {
        if (defaultVersion != null) {
            if (defaultVersion.contains("${default.version}")) {
                throw new IllegalArgumentException("default version may not contain ${default.version}");
            }
            String tag = Pattern.quote("${default.version}");
            for (Artifact artifact : artifacts) {
                if (artifact.version == null) {
                    artifact.version = defaultVersion;
                }
                artifact.replaceAll(tag, defaultVersion);
            }
        }
    }
}
