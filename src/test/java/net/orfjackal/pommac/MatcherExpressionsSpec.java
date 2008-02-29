package net.orfjackal.pommac;

import jdave.Specification;
import jdave.junit4.JDaveRunner;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
@RunWith(JDaveRunner.class)
public class MatcherExpressionsSpec extends Specification<Object> {

    public class LocatingAFile {

        private File workDir;
        private File fileAabbcc;
        private File fileAabccd;

        public Object create() throws IOException {
            workDir = TestUtil.createWorkDir();
            fileAabbcc = new File(workDir, "aabbcc");
            fileAabccd = new File(workDir, "aabccd");
            specify(fileAabbcc.createNewFile());
            specify(fileAabccd.createNewFile());
            return null;
        }

        public void destroy() {
            TestUtil.deleteWorkDir();
        }

        public void todo() {
            // TODO
        }
    }
}
