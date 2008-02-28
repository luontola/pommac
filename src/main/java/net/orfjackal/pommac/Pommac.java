package net.orfjackal.pommac;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
@SuppressWarnings({"unchecked"})
public class Pommac {

    public static List<Artifact> parse(Object data) {
        List<Artifact> artifacts = new ArrayList<Artifact>();
        Map<String, Object> groupMap = (Map<String, Object>) data;
        for (Map.Entry<String, Object> groupEntry : groupMap.entrySet()) {
            String key = groupEntry.getKey();
            Object value = groupEntry.getValue();
            if (key.equals("version") && value instanceof String) {

            } else {
                parseArtifact(artifacts, key, value);
            }
        }
        return artifacts;
    }

    private static void parseArtifact(List<Artifact> results, String groupId, Object data) {
        Map<String, Object> artifactMap = (Map<String, Object>) data;
        for (Map.Entry<String, Object> artifactEntry : artifactMap.entrySet()) {
            String key = artifactEntry.getKey();
            Map<String, Object> value = (Map<String, Object>) artifactEntry.getValue();

            Artifact artifact = new Artifact();
            artifact.groupId = groupId;
            artifact.artifactId = key;
            artifact.jar = (String) value.get("jar");
            artifact.javadoc = (String) value.get("javadoc");
            results.add(artifact);
        }
    }
}
