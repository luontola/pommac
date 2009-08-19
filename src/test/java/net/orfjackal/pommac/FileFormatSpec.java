/*
 * Copyright © 2008-2009  Esko Luontola, www.orfjackal.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.orfjackal.pommac;

import jdave.Specification;
import jdave.junit4.JDaveRunner;
import org.junit.runner.RunWith;
import org.jvyaml.YAML;

import java.util.*;

/**
 * @author Esko Luontola
 * @since 28.2.2008
 */
@RunWith(JDaveRunner.class)
public class FileFormatSpec extends Specification<Object> {

    public class ReadingSgsExample {

        private ParseResults results;

        public void create() {
            String fileText = "" +
                    "default.version: sgs-src-*-r*.zip!/sgs-src-*-r* | sgs-src-([\\d\\.]+)-r(\\d+) >> %1$s\n" +
                    "\n" +
                    "com.sun.sgs:\n" +
                    "    sgs:\n" +
                    "        jar:     sgs-${default.version}-*.zip!/sgs-*/lib/sgs.jar\n" +
                    "        javadoc: sgs-${default.version}-*.zip!/sgs-*/doc/sgs-api\n" +
                    "        sources:\n" +
                    "            - sgs-src-${default.version}-*.zip!/sgs-src-*/src/server/j2se\n" +
                    "            - sgs-src-${default.version}-*.zip!/sgs-src-*/src/shared/j2se\n" +
                    "        depends:\n" +
                    "            - berkeleydb:berkeleydb\n" +
                    "            - org.apache.mina:mina-core\n" +
                    "            - org.slf4j:slf4j-jdk14\n" +
                    "\n" +
                    "    sgs-client:\n" +
                    "        jar:     sgs-client-${default.version}-*.zip!/sgs-client-*/lib/sgs-client.jar\n" +
                    "        javadoc: sgs-client-${default.version}-*.zip!/sgs-client-*/doc/sgs-client-api\n" +
                    "        sources:\n" +
                    "            - sgs-client-src-${default.version}-*.zip!/sgs-client-src-*/src/client/j2se\n" +
                    "            - sgs-client-src-${default.version}-*.zip!/sgs-client-src-*/src/shared/j2se\n" +
                    "        depends:\n" +
                    "            - org.apache.mina:mina-core\n" +
                    "            - org.slf4j:slf4j-jdk14\n" +
                    "\n" +
                    "berkeleydb:\n" +
                    "    berkeleydb:\n" +
                    "        jar:     sgs-${default.version}-*.zip!/bdb-*/db.jar\n" +
                    "        version: sgs-${default.version}-*.zip!/bdb-* | bdb-([\\d\\.]+) >> %1$s\n" +
                    "\n" +
                    "org.apache.mina:\n" +
                    "    mina-core:\n" +
                    "        mvn:     sgs-${default.version}-*.zip!/mina-*/mina-core-*.jar\n" +
                    "\n" +
                    "org.slf4j:\n" +
                    "    slf4j-jdk14:\n" +
                    "        mvn:     sgs-${default.version}-*.zip!/slf4j-*/slf4j-jdk14-*.jar\n" +
                    "";
            Object data = YAML.load(fileText);
            results = PommacParser.parse(data);
        }

        public void willReadDefaultVersion() {
            specify(results.defaultVersion, does.equal("sgs-src-*-r*.zip!/sgs-src-*-r* | sgs-src-([\\d\\.]+)-r(\\d+) >> %1$s"));
        }

        public void willReadGroupId() {
            Set<String> groupIds = new HashSet<String>();
            for (Artifact artifact : results.artifacts) {
                groupIds.add(artifact.groupId);
            }
            specify(groupIds, does.containExactly("com.sun.sgs", "berkeleydb", "org.apache.mina", "org.slf4j"));
        }

        public void willReadArtifactId() {
            Set<String> artifactIds = new HashSet<String>();
            for (Artifact artifact : results.artifacts) {
                artifactIds.add(artifact.artifactId);
            }
            specify(artifactIds, does.containExactly("sgs", "sgs-client", "berkeleydb", "mina-core", "slf4j-jdk14"));
        }

        public void willReadVersion() {
            Map<String, String> expected = new HashMap<String, String>();
            expected.put("sgs", null);
            expected.put("sgs-client", null);
            expected.put("berkeleydb", "sgs-${default.version}-*.zip!/bdb-* | bdb-([\\d\\.]+) >> %1$s");
            for (Artifact artifact : results.artifacts) {
                specify(artifact.version, does.equal(expected.get(artifact.artifactId)));
            }
        }

        public void willReadJar() {
            Map<String, String> expected = new HashMap<String, String>();
            expected.put("sgs", "sgs-${default.version}-*.zip!/sgs-*/lib/sgs.jar");
            expected.put("sgs-client", "sgs-client-${default.version}-*.zip!/sgs-client-*/lib/sgs-client.jar");
            expected.put("berkeleydb", "sgs-${default.version}-*.zip!/bdb-*/db.jar");
            for (Artifact artifact : results.artifacts) {
                specify(artifact.jar, does.equal(expected.get(artifact.artifactId)));
            }
        }

        public void willReadSources() {
            Map<String, String[]> expected = new HashMap<String, String[]>();
            expected.put("sgs", new String[]{
                    "sgs-src-${default.version}-*.zip!/sgs-src-*/src/server/j2se",
                    "sgs-src-${default.version}-*.zip!/sgs-src-*/src/shared/j2se"});
            expected.put("sgs-client", new String[]{
                    "sgs-client-src-${default.version}-*.zip!/sgs-client-src-*/src/client/j2se",
                    "sgs-client-src-${default.version}-*.zip!/sgs-client-src-*/src/shared/j2se"});
            expected.put("berkeleydb", new String[0]);
            expected.put("mina-core", new String[0]);
            expected.put("slf4j-jdk14", new String[0]);
            for (Artifact artifact : results.artifacts) {
                specify(artifact.sources, should.containExactly((Object[]) expected.get(artifact.artifactId)));
            }
        }

        public void willReadJavadoc() {
            Map<String, String> expected = new HashMap<String, String>();
            expected.put("sgs", "sgs-${default.version}-*.zip!/sgs-*/doc/sgs-api");
            expected.put("sgs-client", "sgs-client-${default.version}-*.zip!/sgs-client-*/doc/sgs-client-api");
            expected.put("berkeleydb", null);
            for (Artifact artifact : results.artifacts) {
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
            for (Artifact artifact : results.artifacts) {
                specify(artifact.depends, should.containExactly((Object[]) expected.get(artifact.artifactId)));
            }
        }
    }

    public class ReadingSlickExample {

        private ParseResults results;

        public void create() {
            String fileText = "" +
                    "default.version: slick.zip!/lib/slick.jar!/version >> build=(\\d+) >> b%1$d\n" +
                    "\n" +
                    "slick:\n" +
                    "    slick:\n" +
                    "        jar:     slick.zip!/lib/slick.jar\n" +
                    "        sources: slick.zip!/src\n" +
                    "        javadoc: slick.zip!/javadoc\n" +
                    "        depends:\n" +
                    "            - slick.deps:lwjgl\n" +
                    "            - slick.deps:lwjgl-util-applet, optional\n" +
                    "            - slick.deps:ibxm\n" +
                    "            - slick.deps:jnlp\n" +
                    "            - slick.deps:jogg\n" +
                    "            - slick.deps:jorbis\n" +
                    "            - slick.deps:tinylinepp\n" +
                    "\n" +
                    "    # Native Libraries\n" +
                    "\n" +
                    "    slick-natives-linux:\n" +
                    "        jar:     slick.zip!/lib/natives-linux.jar\n" +
                    "        depends: slick:slick\n" +
                    "\n" +
                    "    slick-natives-mac:\n" +
                    "        jar:     slick.zip!/lib/natives-mac.jar\n" +
                    "        depends: slick:slick\n" +
                    "\n" +
                    "    slick-natives-win32:\n" +
                    "        jar:     slick.zip!/lib/natives-win32.jar\n" +
                    "        depends: slick:slick\n" +
                    "\n" +
                    "    # Examples\n" +
                    "\n" +
                    "    slick-examples:\n" +
                    "        jar:     slick.zip!/lib/slick-examples.jar\n" +
                    "        depends: slick:slick-testdata\n" +
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
                    "        version: slick.zip!/lib/jogg-*.jar | jogg-([\\d\\.]+)\\.jar >> %1$s\n" +
                    "\n" +
                    "    jorbis:\n" +
                    "        jar:     slick.zip!/lib/jorbis-*.jar\n" +
                    "        version: slick.zip!/lib/jorbis-*.jar | jorbis-([\\d\\.]+)\\.jar >> %1$s\n" +
                    "\n" +
                    "    tinylinepp:\n" +
                    "        jar:     slick.zip!/lib/tinylinepp.jar\n" +
                    "\n" +
                    "    # Not used\n" +
                    "\n" +
                    "    jinput:\n" +
                    "        jar:     slick.zip!/lib/jinput.jar\n" +
                    "";
            Object data = YAML.load(fileText);
            results = PommacParser.parse(data);
        }

        public void willReadDefaultVersion() {
            specify(results.defaultVersion, does.equal("slick.zip!/lib/slick.jar!/version >> build=(\\d+) >> b%1$d"));
        }

        public void willReadGroupId() {
            Set<String> groupIds = new HashSet<String>();
            for (Artifact artifact : results.artifacts) {
                groupIds.add(artifact.groupId);
            }
            specify(groupIds, does.containExactly("slick", "slick.deps"));
        }

        public void willReadArtifactId() {
            Set<String> artifactIds = new HashSet<String>();
            for (Artifact artifact : results.artifacts) {
                artifactIds.add(artifact.artifactId);
            }
            specify(artifactIds, does.containExactly(
                    "slick", "slick-natives-linux", "slick-natives-mac", "slick-natives-win32",
                    "slick-examples", "slick-testdata", "lwjgl", "lwjgl-util-applet", "ibxm",
                    "jnlp", "jogg", "jorbis", "tinylinepp", "jinput"));
        }

        public void willReadVersion() {
            Map<String, String> expected = new HashMap<String, String>();
            expected.put("slick", null);
            expected.put("slick-natives-linux", null);
            expected.put("slick-natives-mac", null);
            expected.put("slick-natives-win32", null);
            expected.put("slick-examples", null);
            expected.put("slick-testdata", null);
            expected.put("lwjgl", null);
            expected.put("lwjgl-util-applet", null);
            expected.put("ibxm", null);
            expected.put("jnlp", null);
            expected.put("jogg", "slick.zip!/lib/jogg-*.jar | jogg-([\\d\\.]+)\\.jar >> %1$s");
            expected.put("jorbis", "slick.zip!/lib/jorbis-*.jar | jorbis-([\\d\\.]+)\\.jar >> %1$s");
            expected.put("tinylinepp", null);
            expected.put("jinput", null);
            for (Artifact artifact : results.artifacts) {
                specify(artifact.version, does.equal(expected.get(artifact.artifactId)));
            }
        }

        public void willReadJar() {
            Map<String, String> expected = new HashMap<String, String>();
            expected.put("slick", "slick.zip!/lib/slick.jar");
            expected.put("slick-natives-linux", "slick.zip!/lib/natives-linux.jar");
            expected.put("slick-natives-mac", "slick.zip!/lib/natives-mac.jar");
            expected.put("slick-natives-win32", "slick.zip!/lib/natives-win32.jar");
            expected.put("slick-examples", "slick.zip!/lib/slick-examples.jar");
            expected.put("slick-testdata", null);
            expected.put("lwjgl", "slick.zip!/lib/lwjgl.jar");
            expected.put("lwjgl-util-applet", "slick.zip!/applet/lwjgl_util_applet.jar");
            expected.put("ibxm", "slick.zip!/lib/ibxm.jar");
            expected.put("jnlp", "slick.zip!/lib/jnlp.jar");
            expected.put("jogg", "slick.zip!/lib/jogg-*.jar");
            expected.put("jorbis", "slick.zip!/lib/jorbis-*.jar");
            expected.put("tinylinepp", "slick.zip!/lib/tinylinepp.jar");
            expected.put("jinput", "slick.zip!/lib/jinput.jar");
            for (Artifact artifact : results.artifacts) {
                specify(artifact.jar, does.equal(expected.get(artifact.artifactId)));
            }
        }

        public void willReadResources() {
            Map<String, String[]> expected = new HashMap<String, String[]>();
            expected.put("slick", new String[0]);
            expected.put("slick-natives-linux", new String[0]);
            expected.put("slick-natives-mac", new String[0]);
            expected.put("slick-natives-win32", new String[0]);
            expected.put("slick-examples", new String[0]);
            expected.put("slick-testdata", new String[]{"slick.zip!/ | testdata/**"});
            expected.put("lwjgl", new String[0]);
            expected.put("lwjgl-util-applet", new String[0]);
            expected.put("ibxm", new String[0]);
            expected.put("jnlp", new String[0]);
            expected.put("jogg", new String[0]);
            expected.put("jorbis", new String[0]);
            expected.put("tinylinepp", new String[0]);
            expected.put("jinput", new String[0]);
            for (Artifact artifact : results.artifacts) {
                specify(artifact.resources, does.containExactly((Object[]) expected.get(artifact.artifactId)));
            }
        }

        public void willReadSources() {
            Map<String, String[]> expected = new HashMap<String, String[]>();
            expected.put("slick", new String[]{"slick.zip!/src"});
            expected.put("slick-natives-linux", new String[0]);
            expected.put("slick-natives-mac", new String[0]);
            expected.put("slick-natives-win32", new String[0]);
            expected.put("slick-examples", new String[0]);
            expected.put("slick-testdata", new String[0]);
            expected.put("lwjgl", new String[0]);
            expected.put("lwjgl-util-applet", new String[0]);
            expected.put("ibxm", new String[0]);
            expected.put("jnlp", new String[0]);
            expected.put("jogg", new String[0]);
            expected.put("jorbis", new String[0]);
            expected.put("tinylinepp", new String[0]);
            expected.put("jinput", new String[0]);
            for (Artifact artifact : results.artifacts) {
                specify(artifact.sources, should.containExactly((Object[]) expected.get(artifact.artifactId)));
            }
        }

        public void willReadJavadoc() {
            Map<String, String> expected = new HashMap<String, String>();
            expected.put("slick", "slick.zip!/javadoc");
            expected.put("slick-natives-linux", null);
            expected.put("slick-natives-mac", null);
            expected.put("slick-natives-win32", null);
            expected.put("slick-examples", null);
            expected.put("slick-testdata", null);
            expected.put("lwjgl", null);
            expected.put("lwjgl-util-applet", null);
            expected.put("ibxm", null);
            expected.put("jnlp", null);
            expected.put("jogg", null);
            expected.put("jorbis", null);
            expected.put("tinylinepp", null);
            expected.put("jinput", null);
            for (Artifact artifact : results.artifacts) {
                specify(artifact.javadoc, does.equal(expected.get(artifact.artifactId)));
            }
        }

        public void willReadDependencies() {
            Map<String, String[]> expected = new HashMap<String, String[]>();
            expected.put("slick", new String[]{
                    "slick.deps:lwjgl",
                    "slick.deps:lwjgl-util-applet, optional",
                    "slick.deps:ibxm",
                    "slick.deps:jnlp",
                    "slick.deps:jogg",
                    "slick.deps:jorbis",
                    "slick.deps:tinylinepp"});
            expected.put("slick-natives-linux", new String[]{"slick:slick"});
            expected.put("slick-natives-mac", new String[]{"slick:slick"});
            expected.put("slick-natives-win32", new String[]{"slick:slick"});
            expected.put("slick-examples", new String[]{"slick:slick-testdata"});
            expected.put("slick-testdata", new String[0]);
            expected.put("lwjgl", new String[0]);
            expected.put("lwjgl-util-applet", new String[0]);
            expected.put("ibxm", new String[0]);
            expected.put("jnlp", new String[0]);
            expected.put("jogg", new String[0]);
            expected.put("jorbis", new String[0]);
            expected.put("tinylinepp", new String[0]);
            expected.put("jinput", new String[0]);
            for (Artifact artifact : results.artifacts) {
                specify(artifact.depends, should.containExactly((Object[]) expected.get(artifact.artifactId)));
            }
        }
    }
}
