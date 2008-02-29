package net.orfjackal.pommac;

import jdave.Specification;
import jdave.junit4.JDaveRunner;
import org.junit.runner.RunWith;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
@RunWith(JDaveRunner.class)
public class ExpressionInterpreterSpec extends Specification<Object> {

    public class ProcessingReplacementTags {

        private ParseResults results;
        private Artifact sgs;
        private Artifact berkeleydb;

        public Object create() {
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
            results.defaultVersion = "0.9.5";
            results.artifacts.add(sgs);
            results.artifacts.add(berkeleydb);
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
}
