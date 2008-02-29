package net.orfjackal.pommac;

import jdave.Block;
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
public class FileLocatorSpec extends Specification<Object> {

    public class WhenQueryingForPlainFiles {

        private File workDir;
        private File aabbcc;
        private File aabccd;

        public Object create() throws IOException {
            workDir = TestUtil.createWorkDir();
            aabbcc = new File(workDir, "aabbcc.txt");
            aabccd = new File(workDir, "aabccd.txt");
            specify(aabbcc.createNewFile());
            specify(aabccd.createNewFile());
            return null;
        }

        public void destroy() {
            TestUtil.deleteWorkDir();
        }

        public void findsWithAnExactName() {
            File found = FileLocator.findFile(workDir, "aabbcc.txt");
            specify(found, does.equal(aabbcc));
        }

        public void findsWithTheStarWildcard() {
            File found = FileLocator.findFile(workDir, "aabb*.txt");
            specify(found, does.equal(aabbcc));
        }

        public void findsWithTheQuestionMarkWildcard() {
            File found = FileLocator.findFile(workDir, "aabb?c.txt");
            specify(found, does.equal(aabbcc));
        }

        public void escapesPossibleRegexCharactersFromTheQueryString() {
            specify(new Block() {
                public void run() throws Throwable {
                    FileLocator.findFile(workDir, "aabb...txt");
                }
            }, does.raise(MatchingFileNotFoundException.class, "No file matching: aabb...txt"));
        }

        public void failsIfFileDoesDoNotExist() {
            specify(new Block() {
                public void run() throws Throwable {
                    FileLocator.findFile(workDir, "abc.txt");
                }
            }, does.raise(MatchingFileNotFoundException.class, "No file matching: abc.txt"));
        }

        public void failsIfThereIsMoreThanOneMatch() {
            specify(new Block() {
                public void run() throws Throwable {
                    FileLocator.findFile(workDir, "aa*.txt");
                }
            }, does.raise(UnambiguousQueryException.class, "Unambiguous query: aa*.txt\n" +
                    "More than one match: [" + aabbcc + ", " + aabccd + "]"));
        }
    }
}
