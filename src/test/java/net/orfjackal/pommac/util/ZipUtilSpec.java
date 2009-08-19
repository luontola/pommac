package net.orfjackal.pommac.util;

import jdave.Specification;
import jdave.junit4.JDaveRunner;
import net.orfjackal.pommac.TestUtil;
import org.junit.runner.RunWith;

import java.io.*;
import java.util.zip.*;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
@SuppressWarnings({"FieldCanBeLocal"})
@RunWith(JDaveRunner.class)
public class ZipUtilSpec extends Specification<Object> {

    private File workDir;

    public void create() {
        workDir = TestUtil.createWorkDir();
    }

    public void destroy() {
        TestUtil.deleteWorkDir();
    }


    public class ExtractingFilesFromAZipArchive {

        private File outputDir;
        private File archive;

        private File unpackedEmptyDir;
        private File unpackedFileA;
        private File unpackedDir;
        private File unpackedFileB;

        public void create() throws IOException {
            archive = new File(workDir, "archive.zip");

            outputDir = new File(workDir, "output");
            outputDir.mkdir();
            unpackedEmptyDir = new File(outputDir, "emptyDir");
            unpackedFileA = new File(outputDir, "fileA.txt");
            unpackedDir = new File(outputDir, "dir");
            unpackedFileB = new File(unpackedDir, "fileB.txt");

            ZipEntry emptyDir = new ZipEntry("emptyDir/foo/");
            ZipEntry fileA = new ZipEntry("fileA.txt");
            ZipEntry fileB = new ZipEntry("dir/fileB.txt");
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(archive));
            out.putNextEntry(emptyDir);
            out.putNextEntry(fileA);
            out.write("alpha".getBytes());
            out.putNextEntry(fileB);
            out.write("beta".getBytes());
            out.close();

            specify(archive.exists());
            specify(outputDir.listFiles(), does.containExactly());
            ZipUtil.unzip(archive, outputDir);
            specify(archive.exists());
        }

        public void willUnpackDirectories() {
            specify(unpackedEmptyDir.exists());
            specify(unpackedEmptyDir.isDirectory());
        }

        public void willUnpackFiles() {
            specify(unpackedFileA.exists());
            specify(unpackedFileA.isFile());
            specify(FileUtil.contentsOf(unpackedFileA), does.equal("alpha"));
        }

        public void willUnpackFilesInDirectories() {
            specify(unpackedDir.exists());
            specify(unpackedDir.isDirectory());
            specify(unpackedFileB.exists());
            specify(unpackedFileB.isFile());
            specify(FileUtil.contentsOf(unpackedFileB), does.equal("beta"));
        }
    }
}
