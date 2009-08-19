/*
 * Copyright © 2008-2009  Esko Luontola, www.orfjackal.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
