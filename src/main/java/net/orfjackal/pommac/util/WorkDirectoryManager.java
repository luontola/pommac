package net.orfjackal.pommac.util;

import java.io.File;
import java.util.*;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
public class WorkDirectoryManager {

    private static int counter = 0;

    private final List<File> createdDirs = new ArrayList<File>();
    private final String prefix = "temp_" + System.currentTimeMillis() + "_";
    private final File parentDir;

    public WorkDirectoryManager() {
        this(new File("").getAbsoluteFile());
    }

    public WorkDirectoryManager(File parentDir) {
        assert parentDir.isDirectory();
        this.parentDir = parentDir;
    }

    public File newDirectory() {
        File dir = new File(parentDir, nextName());
        if (dir.mkdir()) {
            createdDirs.add(dir);
            return dir;
        }
        throw new RuntimeException("Unable to create directory: " + dir);
    }

    private String nextName() {
        counter++;
        return prefix + counter;
    }

    public void dispose() {
        for (File dir : createdDirs) {
            delete(dir);
        }
        createdDirs.clear();
    }

    private static void delete(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                delete(f);
            }
        }
        file.delete();
    }
}
