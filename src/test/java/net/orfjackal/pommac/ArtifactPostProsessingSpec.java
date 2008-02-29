package net.orfjackal.pommac;

import jdave.Specification;
import jdave.junit4.JDaveRunner;
import net.orfjackal.pommac.util.FileUtil;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
@SuppressWarnings({"FieldCanBeLocal"})
@RunWith(JDaveRunner.class)
public class ArtifactPostProsessingSpec extends Specification<Object> {

    private ParseResults results;
    private Artifact sgs;
    private Artifact berkeleydb;

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

        public Object create() {
            initTestData();
            results.defaultVersion = "0.9.5";
            return null;
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

        private File workDir;

        public Object create() throws IOException {
            initTestData();
            workDir = TestUtil.createWorkDir();
            ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(
                    new File(workDir, "sgs-src-0.9.5.1-r3730.zip")));
            zip.putNextEntry(new ZipEntry("sgs-src-0.9.5.1-r3730/build"));
            zip.write("build=153".getBytes());
            zip.close();
            return null;
        }

        public void destroy() {
            TestUtil.deleteWorkDir();
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

        private File workDir;
        private FileLocator locator;

        public Object create() throws IOException {
            workDir = TestUtil.createWorkDir();
            locator = new FileLocator(workDir);

            initTestData();
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
            return null;
        }

        public void destroy() {
            locator.dispose();
            TestUtil.deleteWorkDir();
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
}
