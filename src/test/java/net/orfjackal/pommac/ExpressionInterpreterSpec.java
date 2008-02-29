package net.orfjackal.pommac;

import jdave.Specification;
import jdave.junit4.JDaveRunner;
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
@RunWith(JDaveRunner.class)
public class ExpressionInterpreterSpec extends Specification<Object> {

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

    public class ProcessingReplacementTags {

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

    public class ReadingVersionNumber {

        private File workDir;

        public Object create() throws IOException {
            initTestData();
            workDir = TestUtil.createWorkDir();
            ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(
                    new File(workDir, "sgs-src-0.9.5.1-r3730.zip")));
            zip.putNextEntry(new ZipEntry("sgs-src-0.9.5.1-r3730/build"));
            zip.write("build=3730".getBytes());
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
    }
}
