package com.zeyad.usecases.integration;

import java.io.File;
import java.net.URL;
import java.util.Scanner;

import io.appflate.restmock.RESTMockFileParser;

/**
 * @author by ZIaDo on 6/17/17.
 */
public class JVMFileParser implements RESTMockFileParser {

    @Override
    public String readJsonFile(String jsonFilePath) throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource(jsonFilePath);
        File file = new File(resource.getPath());
        StringBuilder fileContents = new StringBuilder((int) file.length());
        Scanner scanner = new Scanner(file, "UTF-8");
        String lineSeparator = System.getProperty("line.separator");
        try {
            while (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine()).append(lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }
}
