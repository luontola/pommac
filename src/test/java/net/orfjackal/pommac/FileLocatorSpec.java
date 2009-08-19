package net.orfjackal.pommac;

import jdave.*;
import jdave.junit4.JDaveRunner;
import org.junit.runner.RunWith;

import java.io.*;
import java.util.zip.*;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
@SuppressWarnings({"FieldCanBeLocal"})
@RunWith(JDaveRunner.class)
public class FileLocatorSpec extends Specification<FileLocator> {

    private FileLocator locator;
    private File workDir;

    public void create() {
        workDir = TestUtil.createWorkDir();
        locator = new FileLocator();
    }

    public void destroy() {
        locator.dispose();
        TestUtil.deleteWorkDir();
    }


    public class WhenQueryingForPlainFiles {

        private File aabbcc;
        private File aabccd;

        public void create() throws IOException {
            aabbcc = new File(workDir, "aabbcc.txt");
            aabccd = new File(workDir, "aabccd.txt");
            specify(aabbcc.createNewFile());
            specify(aabccd.createNewFile());
        }

        public void findsWithAnExactName() {
            File found = locator.findFile(workDir, "aabbcc.txt");
            specify(found, does.equal(aabbcc));
        }

        public void findsWithTheStarWildcard() {
            File found = locator.findFile(workDir, "aabb*.txt");
            specify(found, does.equal(aabbcc));
        }

        public void findsWithTheQuestionMarkWildcard() {
            File found = locator.findFile(workDir, "aabb?c.txt");
            specify(found, does.equal(aabbcc));
        }

        public void escapesPossibleRegexCharactersFromTheQueryString() {
            specify(new Block() {
                public void run() throws Throwable {
                    locator.findFile(workDir, "aabb...txt");
                }
            }, does.raise(MatchingFileNotFoundException.class, "No file matching: aabb...txt"));
        }

        public void failsIfFileDoesDoNotExist() {
            specify(new Block() {
                public void run() throws Throwable {
                    locator.findFile(workDir, "abc.txt");
                }
            }, does.raise(MatchingFileNotFoundException.class, "No file matching: abc.txt"));
        }

        public void failsIfThereIsMoreThanOneMatch() {
            specify(new Block() {
                public void run() throws Throwable {
                    locator.findFile(workDir, "aa*.txt");
                }
            }, does.raise(UnambiguousQueryException.class, "Unambiguous query: aa*.txt\n" +
                    "More than one match: [" + aabbcc + ", " + aabccd + "]"));
        }

        public void failsIfQueryIsEmpty() {
            specify(new Block() {
                public void run() throws Throwable {
                    locator.findFile(workDir, "");
                }
            }, does.raise(MatchingFileNotFoundException.class));
        }
    }

    public class WhenQueryingInsideDirectories {

        private File subDir;
        private File aabbcc;

        public void create() throws IOException {
            subDir = new File(workDir, "subdir");
            aabbcc = new File(subDir, "aabbcc.txt");
            specify(subDir.mkdir());
            specify(aabbcc.createNewFile());
        }

        public void findsADirectory() {
            File found = locator.findFile(workDir, "subdir");
            specify(found, does.equal(subDir));
        }

        public void findsAFileFromADirectory() {
            File found = locator.findFile(workDir, "subdir/aabbcc.txt");
            specify(found, does.equal(aabbcc));
        }
    }

    public class WhenQueryingInsideZipArchives {

        private File archive;

        public void create() throws IOException {
            archive = new File(workDir, "archive.zip");
            ZipOutputStream zipper = new ZipOutputStream(new FileOutputStream(archive));
            zipper.putNextEntry(new ZipEntry("aabbcc.txt"));
            zipper.close();
        }

        public void findsAFileFromAZipArchive() {
            File found = locator.findFile(workDir, "archive.zip!/aabbcc.txt");
            specify(found.getName(), does.equal("aabbcc.txt"));
        }
    }
}
