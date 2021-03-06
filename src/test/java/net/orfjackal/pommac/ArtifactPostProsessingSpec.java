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
import net.orfjackal.pommac.util.FileUtil;
import org.junit.runner.RunWith;

import java.io.*;
import java.util.zip.*;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
@SuppressWarnings({"FieldCanBeLocal"})
@RunWith(JDaveRunner.class)
public class ArtifactPostProsessingSpec extends Specification<Object> {

    private File workDir;
    private FileLocator locator;

    private ParseResults results;
    private Artifact sgs;
    private Artifact berkeleydb;

    public void create() {
        workDir = TestUtil.createWorkDir();
        locator = new FileLocator(workDir);
        initTestData();
    }

    public void destroy() {
        locator.dispose();
        TestUtil.deleteWorkDir();
    }

    private void initTestData() {
        sgs = new Artifact();
        sgs.groupId = "com.sun.sgs";
        sgs.artifactId = "sgs";
        sgs.jar = "sgs-${default.version}-*.zip!/sgs-*/lib/sgs.jar";
        sgs.sources = new String[]{"sgs-src-${default.version}-*.zip!/sgs-src-*/src/server/j2se"};
        sgs.resources = new String[]{"sgs-src-${default.version}-*.zip!/sgs-src-*/resources"};
        sgs.javadoc = "sgs-${default.version}-*.zip!/sgs-*/doc/sgs-api";

        berkeleydb = new Artifact();
        berkeleydb.groupId = "berkeleydb";
        berkeleydb.artifactId = "berkeleydb";
        berkeleydb.jar = "sgs-${default.version}-*.zip!/bdb-*/db.jar";
        berkeleydb.version = "sgs-${default.version}-*.zip!/bdb-* | bdb-([\\d\\.]+) >> %1$s";

        results = new ParseResults();
        results.defaultVersion = "sgs-src-*-r*.zip!/sgs-src-*-r* | sgs-src-([\\d\\.]+)-r(\\d+) >> %1$s";
        results.artifacts.add(sgs);
        results.artifacts.add(berkeleydb);
    }


    public class FilteringReplacementTags {

        public void create() {
            results.defaultVersion = "0.9.5";
        }

        public void willFilterVersionFieldForDefaultVersion() {
            results.processTags();
            specify(sgs.version, does.equal("0.9.5"));
            specify(berkeleydb.version, does.equal("sgs-0.9.5-*.zip!/bdb-* | bdb-([\\d\\.]+) >> %1$s"));
        }

        public void willFilterJarFieldForDefaultVersion() {
            results.processTags();
            specify(sgs.jar, does.equal("sgs-0.9.5-*.zip!/sgs-*/lib/sgs.jar"));
            specify(berkeleydb.jar, does.equal("sgs-0.9.5-*.zip!/bdb-*/db.jar"));
        }

        public void willFilterSourcesFieldForDefaultVersion() {
            results.processTags();
            specify(sgs.sources, does.containExactly("sgs-src-0.9.5-*.zip!/sgs-src-*/src/server/j2se"));
        }

        public void willFilterResourcesFieldForDefaultVersion() {
            results.processTags();
            specify(sgs.resources, does.containExactly("sgs-src-0.9.5-*.zip!/sgs-src-*/resources"));
        }

        public void willFilterJavadocFieldForDefaultVersion() {
            results.processTags();
            specify(sgs.javadoc, does.equal("sgs-0.9.5-*.zip!/sgs-*/doc/sgs-api"));
        }

        public void willDoNothingIfDefaultVersionIsNotSet() {
            results.defaultVersion = null;
            results.processTags();
            specify(sgs.jar, does.equal("sgs-${default.version}-*.zip!/sgs-*/lib/sgs.jar"));
        }
    }

    public class CalculatingVersionNumbers {

        public void create() throws IOException {
            ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(
                    new File(workDir, "sgs-src-0.9.5.1-r3730.zip")));
            zip.putNextEntry(new ZipEntry("sgs-src-0.9.5.1-r3730/build"));
            zip.write("build=153".getBytes());
            zip.close();
        }

        public void canBeReadFromFileNameWithRegex() {
            sgs.version = "sgs-src-*-r*.zip!/sgs-src-*-r* | sgs-src-([\\d\\.]+)-r(\\d+) >> %1$s";
            sgs.calculateVersion(workDir);
            specify(sgs.version, does.equal("0.9.5.1"));
        }

        public void canBeReadFromFileContentsWithRegex() {
            sgs.version = "sgs-src-*-r*.zip!/sgs-src-*-r*/build >> build=(\\d+) >> b%1$s";
            sgs.calculateVersion(workDir);
            specify(sgs.version, does.equal("b153"));
        }

        public void canBeReadFromAConstant() {
            sgs.version = "0.9.6";
            sgs.calculateVersion(workDir);
            specify(sgs.version, does.equal("0.9.6"));
        }
    }

    public class LocatingFilePaths {

        public void create() throws IOException {
            sgs.jar = "sgs-src-0.9.5.1-*.zip!/sgs-*/lib/sgs.jar";
            sgs.sources = new String[]{
                    "sgs-src-0.9.5.1-*.zip!/sgs-src-*/src/server/j2se",
                    "sgs-src-0.9.5.1-*.zip!/sgs-src-*/src/shared/j2se"
            };
            sgs.resources = new String[]{
                    "sgs-src-0.9.5.1-*.zip!/sgs-src-*/resources"
            };
            sgs.javadoc = "sgs-src-0.9.5.1-*.zip!/sgs-*/doc/sgs-api";

            ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(
                    new File(workDir, "sgs-src-0.9.5.1-r3730.zip")));
            zip.putNextEntry(new ZipEntry("sgs-src-0.9.5.1-r3730/lib/sgs.jar"));
            zip.write("JAR_FILE".getBytes());
            zip.putNextEntry(new ZipEntry("sgs-src-0.9.5.1-r3730/src/server/j2se/"));
            zip.putNextEntry(new ZipEntry("sgs-src-0.9.5.1-r3730/src/shared/j2se/"));
            zip.putNextEntry(new ZipEntry("sgs-src-0.9.5.1-r3730/resources/"));
            zip.putNextEntry(new ZipEntry("sgs-src-0.9.5.1-r3730/doc/sgs-api/"));
            zip.close();

            sgs.locateFiles(locator);
        }

        public void willLocateJar() {
            specify(FileUtil.contentsOf(new File(sgs.jar)), does.equal("JAR_FILE"));
        }

        public void willLocateSources() {
            specify(new File(sgs.sources[0]).isDirectory());
            specify(new File(sgs.sources[0]).toURI().toString().endsWith("/src/server/j2se/"));
            specify(new File(sgs.sources[1]).isDirectory());
            specify(new File(sgs.sources[1]).toURI().toString().endsWith("/src/shared/j2se/"));
        }

        public void willLocateResources() {
            specify(new File(sgs.resources[0]).isDirectory());
            specify(new File(sgs.resources[0]).toURI().toString().endsWith("/resources/"));
        }

        public void willLocateJavadoc() {
            specify(new File(sgs.javadoc).isDirectory());
            specify(new File(sgs.javadoc).toURI().toString().endsWith("/doc/sgs-api/"));
        }
    }

    // TODO: zip sources, resources, javadoc
    // TODO: link dependencies
    // TODO: create pom
    // TODO: commands for importing to repository
    // TODO: intergration test
}
