package net.orfjackal.pommac;

import jdave.Specification;
import jdave.junit4.JDaveRunner;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
@RunWith(JDaveRunner.class)
public class UnpackerSpec extends Specification<Object> {

    public class UnpackingAZipArchive {

        private File workDir;
        private File outputDir;
        private File archive;

        private File unpackedEmptyDir;
        private File unpackedFileA;
        private File unpackedDir;
        private File unpackedFileB;

        public Object create() throws IOException {
            workDir = TestUtil.createWorkDir();
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
            Unpacker.unpack(archive, outputDir);
            specify(archive.exists());
            return null;
        }

        public void destroy() {
            TestUtil.deleteWorkDir();
        }

        public void willUnpackDirectories() {
            specify(unpackedEmptyDir.exists());
            specify(unpackedEmptyDir.isDirectory());
        }

        public void willUnpackFiles() throws FileNotFoundException {
            specify(unpackedFileA.exists());
            specify(unpackedFileA.isFile());
            specify(contentsOf(unpackedFileA), does.equal("alpha"));
        }

        public void willUnpackFilesInDirectories() throws FileNotFoundException {
            specify(unpackedDir.exists());
            specify(unpackedDir.isDirectory());
            specify(unpackedFileB.exists());
            specify(unpackedFileB.isFile());
            specify(contentsOf(unpackedFileB), does.equal("beta"));
        }
    }

    private static String contentsOf(File file) throws FileNotFoundException {
        Scanner in = new Scanner(file);
        try {
            if (in.hasNextLine()) {
                return in.nextLine();
            } else {
                return "";
            }
        } finally {
            in.close();
        }
    }
}
