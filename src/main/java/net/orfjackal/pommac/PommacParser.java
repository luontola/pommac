package net.orfjackal.pommac;

import java.util.*;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
@SuppressWarnings({"unchecked"})
public class PommacParser {

    private String defaultVersion;

    public List<Artifact> parse(Object data) {
        List<Artifact> artifacts = new ArrayList<Artifact>();
        Map<String, Object> groups = (Map<String, Object>) data;

        if (groups.containsKey("version")) {
            defaultVersion = (String) groups.get("version");
        }

        for (Map.Entry<String, Object> groupEntry : groups.entrySet()) {
            String key = groupEntry.getKey();
            Object value = groupEntry.getValue();

            if (!key.equals("version")) {
                parseArtifact(artifacts, key, value);
            }
        }
        return artifacts;
    }

    private void parseArtifact(List<Artifact> results, String groupId, Object data) {
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
            results.add(artifact);
        }
    }

    private String getVersion(Map<String, Object> artifactMap) {
        String version = (String) artifactMap.get("version");
        if (version == null && !artifactMap.containsKey("mvn")) {
            version = defaultVersion;
        }
        return version;
    }

    private String getJar(Map<String, Object> artifactMap) {
        return (String) artifactMap.get("jar");
    }

    private String[] getSources(Map<String, Object> artifactMap) {
        return asArray("sources", artifactMap);
    }

    private String[] getResources(Map<String, Object> artifactMap) {
        return asArray("resources", artifactMap);
    }

    private String getJavadoc(Map<String, Object> artifactMap) {
        return (String) artifactMap.get("javadoc");
    }

    private String[] getDepends(Map<String, Object> artifactMap) {
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
