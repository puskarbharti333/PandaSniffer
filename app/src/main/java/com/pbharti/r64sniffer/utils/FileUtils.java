package com.pbharti.r64sniffer.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileUtils {

    public static final StringBuffer readSmallFileText(String filePath) {
        StringBuffer sb = new StringBuffer();
        File file = new File(filePath);
        BufferedReader reader = null;
        FileReader fr = null;
        try {
            fr = new FileReader(file);
            reader = new BufferedReader(fr);
            String buffer = null;
            while ((buffer = reader.readLine()) != null) {
                sb.append(buffer).append("\n\r");
            }
        } catch (IOException e) {
            ExceptionHandler.handleException(e);
        } finally {
            IOUtils.safeClose(fr);
            IOUtils.safeClose(reader);
        }

        return sb;
    }

}
