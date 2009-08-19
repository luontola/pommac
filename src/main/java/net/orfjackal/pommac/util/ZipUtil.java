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

import java.io.*;
import java.util.Enumeration;
import java.util.zip.*;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
public final class ZipUtil {

    private ZipUtil() {
    }

    public static void unzip(File archive, File toDir) {
        ZipFile zip = null;
        try {
            zip = new ZipFile(archive);
            for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();) {
                ZipEntry zipEntry = entries.nextElement();
                if (zipEntry.isDirectory()) {
                    writeDir(toDir, zipEntry);
                } else {
                    writeFile(toDir, zip, zipEntry);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to unpack: " + archive, e);
        } finally {
            FileUtil.close(zip);
        }
    }

    private static void writeDir(File toDir, ZipEntry entry) {
        File dir = new File(toDir, entry.getName());
        dir.mkdirs();
    }

    private static void writeFile(File toDir, ZipFile zip, ZipEntry zipEntry) throws IOException {
        File file = new File(toDir, zipEntry.getName());
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        InputStream in = zip.getInputStream(zipEntry);
        OutputStream out = new FileOutputStream(file);
        FileUtil.copy(in, out);
    }
}
