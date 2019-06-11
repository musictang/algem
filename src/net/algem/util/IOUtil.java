package net.algem.util;

import java.io.*;

public class IOUtil {
    public static String slurp(InputStream in) throws IOException {
        StringBuilder out = new StringBuilder();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1; ) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }

    public static void closeQuietly(Closeable in) {
        if (in != null) {
            try {
                in.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static String readFile(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        try {
            return slurp(stream);
        } finally {
            closeQuietly(stream);
        }
    }

    /**
     * Class version of readFile, for easy mocking in service
     */
    public static class FileReaderHelper {
        public String readFile(File file) throws IOException {
            return IOUtil.readFile(file);
        }
    }
}
