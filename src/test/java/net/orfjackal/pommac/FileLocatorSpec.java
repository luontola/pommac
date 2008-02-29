package net.orfjackal.pommac;

import jdave.Block;
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
@SuppressWarnings({"FieldCanBeLocal"})
@RunWith(JDaveRunner.class)
public class FileLocatorSpec extends Specification<FileLocator> {

    public class WhenQueryingForPlainFiles {

        private FileLocator locator;
        private File workDir;
        private File aabbcc;
        private File aabccd;

        public FileLocator create() throws IOException {
            workDir = TestUtil.createWorkDir();
            aabbcc = new File(workDir, "aabbcc.txt");
            aabccd = new File(workDir, "aabccd.txt");
            specify(aabbcc.createNewFile());
            specify(aabccd.createNewFile());
            locator = new FileLocator();
            return locator;
        }

        public void destroy() {
            locator.dispose();
            TestUtil.deleteWorkDir();
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

        private FileLocator locator;
        private File workDir;
        private File subDir;
        private File aabbcc;

        public FileLocator create() throws IOException {
            workDir = TestUtil.createWorkDir();
            subDir = new File(workDir, "subdir");
            aabbcc = new File(subDir, "aabbcc.txt");
            specify(subDir.mkdir());
            specify(aabbcc.createNewFile());
            locator = new FileLocator();
            return locator;
        }

        public void destroy() {
            locator.dispose();
            TestUtil.deleteWorkDir();
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

        private FileLocator locator;
        private File workDir;
        private File archive;

        public FileLocator create() throws IOException {
            workDir = TestUtil.createWorkDir();
            archive = new File(workDir, "archive.zip");
            ZipOutputStream zipper = new ZipOutputStream(new FileOutputStream(archive));
            zipper.putNextEntry(new ZipEntry("aabbcc.txt"));
            zipper.close();
            locator = new FileLocator();
            return locator;
        }

        public void destroy() {
            locator.dispose();
            TestUtil.deleteWorkDir();
        }

        public void findsAFileFromAZipArchive() {
            File found = locator.findFile(workDir, "archive.zip!/aabbcc.txt");
            specify(found.getName(), does.equal("aabbcc.txt"));
        }
    }
}
