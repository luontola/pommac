package net.orfjackal.pommac;

import java.io.File;
import java.io.IOException;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
public class TestUtil {

    private static final File WORK_DIR = new File("workdir_" + System.currentTimeMillis() + ".tmp");

    public static File createWorkDir() {
        if (WORK_DIR.exists()) {
            throw new RuntimeException("Already exists: " + WORK_DIR);
        }
        WORK_DIR.mkdir();
        if (!WORK_DIR.isDirectory()) {
            throw new RuntimeException("Unable to create: " + WORK_DIR);
        }
        return WORK_DIR;
    }

    public static void deleteWorkDir() {
        try {
            delete(WORK_DIR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void delete(File file) throws IOException {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                delete(f);
            }
        }
        if (!file.delete()) {
            throw new IOException("Unable to delete: " + file);
        }
    }
}
