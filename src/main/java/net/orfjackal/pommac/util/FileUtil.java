package net.orfjackal.pommac.util;

import java.io.*;
import java.util.zip.ZipFile;

/**
 * @author Esko Luontola
 * @since 1.3.2008
 */
public final class FileUtil {

    private FileUtil() {
    }

    public static String contentsOf(File file) {
        try {
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            copy(in, out);
            return out.toString();

        } catch (IOException e) {
            throw new RuntimeException("Unable to read: " + file, e);
        }
    }

    public static void copy(InputStream from, OutputStream to) throws IOException {
        try {
            byte[] buf = new byte[1024];
            int len;
            while ((len = from.read(buf)) >= 0) {
                to.write(buf, 0, len);
            }
        } finally {
            close(from);
            close(to);
        }
    }

    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close(ZipFile zip) {
        try {
            if (zip != null) {
                zip.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
