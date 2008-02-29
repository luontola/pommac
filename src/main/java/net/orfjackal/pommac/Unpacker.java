package net.orfjackal.pommac;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Esko Luontola
 * @since 29.2.2008
 */
public final class Unpacker {

    private Unpacker() {
    }

    public static void unpack(File archive, File toDir) {
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
            close(zip);
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
        copy(in, out);
    }

    private static void copy(InputStream from, OutputStream to) throws IOException {
        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = from.read(buffer)) >= 0) {
                to.write(buffer, 0, len);
            }
        } finally {
            close(from);
            close(to);
        }
    }

    private static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void close(ZipFile zip) {
        try {
            if (zip != null) {
                zip.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
