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

package net.orfjackal.pommac;

import java.io.*;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
public final class TestUtil {

    private static final File WORK_DIR = new File("workdir_" + System.currentTimeMillis() + ".tmp");

    private TestUtil() {
    }

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
