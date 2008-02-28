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

    public class ReadingSgsExample {

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

    public class ReadingSlickExample {

        private List<Artifact> artifacts;

        public Object create() {
            String fileText = "" +
                    "version: slick.zip!/lib/slick.jar!/version >> build=(\\d+) >> b%1$d\n" +
                    "\n" +
                    "slick:\n" +
                    "    slick:\n" +
                    "        jar:     slick.zip!/lib/slick.jar\n" +
                    "        sources: slick.zip!/src\n" +
                    "        javadoc: slick.zip!/javadoc\n" +
                    "        depends:\n" +
                    "            - \"slick.deps:lwjgl\"\n" +
                    "            - \"slick.deps:lwjgl-util-applet, optional\"\n" +
                    "            - \"slick.deps:ibxm\"\n" +
                    "            - \"slick.deps:jnlp\"\n" +
                    "            - \"slick.deps:jogg\"\n" +
                    "            - \"slick.deps:jorbis\"\n" +
                    "            - \"slick.deps:tinylinepp\"\n" +
                    "\n" +
                    "    # Native Libraries\n" +
                    "\n" +
                    "    slick-natives-linux:\n" +
                    "        jar:     slick.zip!/lib/natives-linux.jar\n" +
                    "        depends: \"slick:slick\"\n" +
                    "\n" +
                    "    slick-natives-mac:\n" +
                    "        jar:     slick.zip!/lib/natives-mac.jar\n" +
                    "        depends: \"slick:slick\"\n" +
                    "\n" +
                    "    slick-natives-win32:\n" +
                    "        jar:     slick.zip!/lib/natives-win32.jar\n" +
                    "        depends: \"slick:slick\"\n" +
                    "\n" +
                    "    # Examples\n" +
                    "\n" +
                    "    slick-examples:\n" +
                    "        jar:     slick.zip!/lib/slick-examples.jar\n" +
                    "        sources: slick.zip!/tools\n" +
                    "        depends: \"slick:slick-testdata\"\n" +
                    "\n" +
                    "    slick-testdata:\n" +
                    "        resources: slick.zip!/ | testdata/**\n" +
                    "\n" +
                    "\n" +
                    "# Dependencies\n" +
                    "\n" +
                    "slick.deps:\n" +
                    "    lwjgl:\n" +
                    "        jar:     slick.zip!/lib/lwjgl.jar\n" +
                    "\n" +
                    "    lwjgl-util-applet:\n" +
                    "        jar:     slick.zip!/applet/lwjgl_util_applet.jar\n" +
                    "\n" +
                    "    ibxm:\n" +
                    "        jar:     slick.zip!/lib/ibxm.jar\n" +
                    "\n" +
                    "    jnlp:\n" +
                    "        jar:     slick.zip!/lib/jnlp.jar\n" +
                    "\n" +
                    "    jogg:\n" +
                    "        jar:     slick.zip!/lib/jogg-*.jar\n" +
                    "        version: slick.zip!/lib/jogg-*.jar | jogg-(.+)\\.jar >> %1$s\n" +
                    "\n" +
                    "    jorbis:\n" +
                    "        jar:     slick.zip!/lib/jorbis-*.jar\n" +
                    "        version: slick.zip!/lib/jorbis-*.jar | jorbis-(.+)\\.jar >> %1$s\n" +
                    "\n" +
                    "    tinylinepp:\n" +
                    "        jar:     slick.zip!/lib/tinylinepp.jar\n" +
                    "\n" +
                    "    # Not used\n" +
                    "\n" +
                    "    jinput:\n" +
                    "        jar:     slick.zip!/lib/jinput.jar";
            Object data = Yaml.load(fileText);
            artifacts = Pommac.parse(data);
            return null;
        }
    }
}
