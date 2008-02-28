package net.orfjackal.pommac;

import jdave.Specification;
import jdave.junit4.JDaveRunner;
import org.ho.yaml.Yaml;
import org.junit.runner.RunWith;

import java.util.*;

/**
 * @author Esko Luontola
 * @since 28.2.2008
 */
@RunWith(JDaveRunner.class)
public class FileFormatSpec extends Specification<Object> {

    public class ReadingSgsExampleFile {

        private List<Artifact> artifacts;

        public Object create() {
            String fileText = "" +
                    "version: sgs-src-*-r*.zip!/sgs-src-*-r* | sgs-src-(.+)-r(\\d+) >> %1$s\n" +
                    "\n" +
                    "com.sun.sgs:\n" +
                    "    sgs:\n" +
                    "        jar:     sgs-$VERSION-*.zip!/sgs-*/lib/sgs.jar\n" +
                    "        javadoc: sgs-$VERSION-*.zip!/sgs-*/doc/sgs-api\n" +
                    "        sources:\n" +
                    "            - sgs-src-$VERSION-*.zip!/sgs-src-*/src/server/j2se\n" +
                    "            - sgs-src-$VERSION-*.zip!/sgs-src-*/src/shared/j2se\n" +
                    "        depends:\n" +
                    "            - \"berkeleydb:berkeleydb\"\n" +
                    "            - \"org.apache.mina:mina-core\"\n" +
                    "            - \"org.slf4j:slf4j-jdk14\"\n" +
                    "\n" +
                    "    sgs-client:\n" +
                    "        jar:     sgs-client-$VERSION-*.zip!/sgs-client-*/lib/sgs-client.jar\n" +
                    "        javadoc: sgs-client-$VERSION-*.zip!/sgs-client-*/doc/sgs-client-api\n" +
                    "        sources:\n" +
                    "            - sgs-client-src-$VERSION-*.zip!/sgs-client-src-*/src/client/j2se\n" +
                    "            - sgs-client-src-$VERSION-*.zip!/sgs-client-src-*/src/shared/j2se\n" +
                    "        depends:\n" +
                    "            - \"org.apache.mina:mina-core\"\n" +
                    "            - \"org.slf4j:slf4j-jdk14\"\n" +
                    "\n" +
                    "berkeleydb:\n" +
                    "    berkeleydb:\n" +
                    "        jar:     sgs-$VERSION-*.zip!/bdb-*/db.jar\n" +
                    "        version: sgs-$VERSION-*.zip!/bdb-* | bdb-(.+) >> %1$s\n" +
                    "\n" +
                    "org.apache.mina:\n" +
                    "    mina-core:\n" +
                    "        mvn:     sgs-$VERSION-*.zip!/mina-*/mina-core-*.jar\n" +
                    "\n" +
                    "org.slf4j:\n" +
                    "    slf4j-jdk14:\n" +
                    "        mvn:     sgs-$VERSION-*.zip!/slf4j-*/slf4j-jdk14-*.jar\n" +
                    "";
            Object data = Yaml.load(fileText);
            artifacts = Pommac.parse(data);
            return null;
        }

        public void willReadGroupId() {
            Set<String> groupIds = new HashSet<String>();
            for (Artifact artifact : artifacts) {
                groupIds.add(artifact.groupId);
            }
            specify(groupIds, does.containExactly("com.sun.sgs", "berkeleydb", "org.apache.mina", "org.slf4j"));
        }

        public void willReadArtifactId() {
            Set<String> artifactIds = new HashSet<String>();
            for (Artifact artifact : artifacts) {
                artifactIds.add(artifact.artifactId);
            }
            specify(artifactIds, does.containExactly("sgs", "sgs-client", "berkeleydb", "mina-core", "slf4j-jdk14"));
        }

        public void willReadVersion() {
            Map<String, String> expected = new HashMap<String, String>();
            expected.put("sgs", "sgs-src-*-r*.zip!/sgs-src-*-r* | sgs-src-(.+)-r(\\d+) >> %1$s");
            expected.put("sgs-client", "sgs-src-*-r*.zip!/sgs-src-*-r* | sgs-src-(.+)-r(\\d+) >> %1$s");
            expected.put("berkeleydb", "sgs-$VERSION-*.zip!/bdb-* | bdb-(.+) >> %1$s");
            for (Artifact artifact : artifacts) {
                specify(artifact.version, does.equal(expected.get(artifact.artifactId)));
            }
        }

        public void willReadJar() {
            Map<String, String> expected = new HashMap<String, String>();
            expected.put("sgs", "sgs-$VERSION-*.zip!/sgs-*/lib/sgs.jar");
            expected.put("sgs-client", "sgs-client-$VERSION-*.zip!/sgs-client-*/lib/sgs-client.jar");
            expected.put("berkeleydb", "sgs-$VERSION-*.zip!/bdb-*/db.jar");
            for (Artifact artifact : artifacts) {
                specify(artifact.jar, does.equal(expected.get(artifact.artifactId)));
            }
        }

        public void willReadSources() {
            Map<String, String[]> expected = new HashMap<String, String[]>();
            expected.put("sgs", new String[]{
                    "sgs-src-$VERSION-*.zip!/sgs-src-*/src/server/j2se",
                    "sgs-src-$VERSION-*.zip!/sgs-src-*/src/shared/j2se"});
            expected.put("sgs-client", new String[]{
                    "sgs-client-src-$VERSION-*.zip!/sgs-client-src-*/src/client/j2se",
                    "sgs-client-src-$VERSION-*.zip!/sgs-client-src-*/src/shared/j2se"});
            expected.put("berkeleydb", new String[0]);
            expected.put("mina-core", new String[0]);
            expected.put("slf4j-jdk14", new String[0]);
            for (Artifact artifact : artifacts) {
                specify(artifact.sources, should.containExactly(expected.get(artifact.artifactId)));
            }
        }

        public void willReadJavadoc() {
            Map<String, String> expected = new HashMap<String, String>();
            expected.put("sgs", "sgs-$VERSION-*.zip!/sgs-*/doc/sgs-api");
            expected.put("sgs-client", "sgs-client-$VERSION-*.zip!/sgs-client-*/doc/sgs-client-api");
            expected.put("berkeleydb", null);
            for (Artifact artifact : artifacts) {
                specify(artifact.javadoc, does.equal(expected.get(artifact.artifactId)));
            }
        }

        public void willReadDependencies() {
            Map<String, String[]> expected = new HashMap<String, String[]>();
            expected.put("sgs", new String[]{
                    "berkeleydb:berkeleydb",
                    "org.apache.mina:mina-core",
                    "org.slf4j:slf4j-jdk14"});
            expected.put("sgs-client", new String[]{
                    "org.apache.mina:mina-core",
                    "org.slf4j:slf4j-jdk14"});
            expected.put("berkeleydb", new String[0]);
            expected.put("mina-core", new String[0]);
            expected.put("slf4j-jdk14", new String[0]);
            for (Artifact artifact : artifacts) {
                specify(artifact.depends, should.containExactly(expected.get(artifact.artifactId)));
            }
        }
    }
}
