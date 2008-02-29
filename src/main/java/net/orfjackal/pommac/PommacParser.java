package net.orfjackal.pommac;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
@SuppressWarnings({"unchecked"})
public final class PommacParser {

    private PommacParser() {
    }

    public static ParseResults parse(Object data) {
        ParseResults results = new ParseResults();
        Map<String, Object> groups = (Map<String, Object>) data;

        if (groups.containsKey("default.version")) {
            results.defaultVersion = (String) groups.get("default.version");
        }

        for (Map.Entry<String, Object> groupEntry : groups.entrySet()) {
            String key = groupEntry.getKey();
            Object value = groupEntry.getValue();

            if (!key.equals("default.version")) {
                parseArtifact(results, key, value);
            }
        }
        return results;
    }

    private static void parseArtifact(ParseResults results, String groupId, Object data) {
        Map<String, Object> artifacts = (Map<String, Object>) data;
        for (Map.Entry<String, Object> artifactEntry : artifacts.entrySet()) {
            String artifactId = artifactEntry.getKey();
            Map<String, Object> value = (Map<String, Object>) artifactEntry.getValue();

            Artifact artifact = new Artifact();
            artifact.groupId = groupId;
            artifact.artifactId = artifactId;
            artifact.version = getVersion(value);
            artifact.jar = getJar(value);
            artifact.sources = getSources(value);
            artifact.resources = getResources(value);
            artifact.javadoc = getJavadoc(value);
            artifact.depends = getDepends(value);
            results.artifacts.add(artifact);
        }
    }

    private static String getVersion(Map<String, Object> artifactMap) {
        return (String) artifactMap.get("version");
    }

    private static String getJar(Map<String, Object> artifactMap) {
        return (String) artifactMap.get("jar");
    }

    private static String[] getSources(Map<String, Object> artifactMap) {
        return asArray("sources", artifactMap);
    }

    private static String[] getResources(Map<String, Object> artifactMap) {
        return asArray("resources", artifactMap);
    }

    private static String getJavadoc(Map<String, Object> artifactMap) {
        return (String) artifactMap.get("javadoc");
    }

    private static String[] getDepends(Map<String, Object> artifactMap) {
        return asArray("depends", artifactMap);
    }

    private static String[] asArray(String key, Map<String, Object> map) {
        List<String> list = Collections.EMPTY_LIST;
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value instanceof List) {
                list = (List<String>) value;
            } else {
                list = Arrays.asList((String) value);
            }
        }
        return list.toArray(new String[list.size()]);
    }
}
