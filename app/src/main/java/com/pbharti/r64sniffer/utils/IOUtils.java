package com.pbharti.r64sniffer.utils;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {

    public static final void safeClose(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
                ExceptionHandler.handleException(e);
            }
        }
    }

}
