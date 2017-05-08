//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.domob.android.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {
    public IOUtils() {
    }

    public static void copyContent(InputStream inputStream, OutputStream outputStream) throws IOException {
        if(inputStream != null && outputStream != null) {
            byte[] buffer = new byte[16384];

            int length;
            while((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

        } else {
            throw new IOException("Unable to copy from or to a null stream.");
        }
    }

    public static void copyContent(InputStream inputStream, OutputStream outputStream, long maxBytes) throws IOException {
        if(inputStream != null && outputStream != null) {
            byte[] buffer = new byte[16384];
            long totalRead = 0L;

            int length;
            while((length = inputStream.read(buffer)) != -1) {
                totalRead += (long)length;
                if(totalRead >= maxBytes) {
                    throw new IOException("Error copying content: attempted to copy " + totalRead + " bytes, with " + maxBytes + " maximum.");
                }

                outputStream.write(buffer, 0, length);
            }

        } else {
            throw new IOException("Unable to copy from or to a null stream.");
        }
    }

    public static void readStream(InputStream inputStream, byte[] buffer) throws IOException {
        int offset = 0;
        boolean bytesRead = false;
        int maxBytes = buffer.length;

        do {
            int bytesRead1;
            if((bytesRead1 = inputStream.read(buffer, offset, maxBytes)) == -1) {
                return;
            }

            offset += bytesRead1;
            maxBytes -= bytesRead1;
        } while(maxBytes > 0);

    }

    public static void closeStream(Closeable stream) {
        if(stream != null) {
            try {
                stream.close();
            } catch (IOException var2) {
                ;
            }

        }
    }
}
