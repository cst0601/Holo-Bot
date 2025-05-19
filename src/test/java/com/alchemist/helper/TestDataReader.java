package com.alchemist.helper;

import java.util.Scanner;

/**
 * Test helper functions for reading files.
 */
public class TestDataReader {
  private static TestDataReader instance = null;
  
  /** Get object instance of the reader. */
  public static TestDataReader getReader() {
    if (instance == null) {
      synchronized (TestDataReader.class) {
        if (instance == null) {
          instance = new TestDataReader();
        }
      }
    }

    return instance;
  }

  /** Reads file from specified file path. */
  public String readTestData(String filePath) {
    String testData = null;

    ClassLoader classLoader = getClass().getClassLoader();
    Scanner scanner = new Scanner(classLoader.getResourceAsStream(filePath));
    scanner.useDelimiter("\\Z");
    testData = scanner.next();
    scanner.close();

    return testData;
  }
}
