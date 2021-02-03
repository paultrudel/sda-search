package com.search.sdasearch.utility;

import java.io.File;

/**************************************/
/*                                    */
/*  Program: SDA-Search               */
/*  Author: Paul Trudel               */
/*                                    */
/*  Sets up the necessary directories */
/*                                    */
/**************************************/

public class SDASetup {
    public static final String DIRECTORY = System.getProperty("user.home");
    public static final String CRAWL = DIRECTORY + "\\Crawler";
    public static final String STORAGE = CRAWL + "\\Storage";
    public static final String GRAPH = CRAWL + "\\Graph";
    public static final String INDEX = CRAWL + "\\Index";

    public static void setup() {
        createDir(CRAWL);
        createDir(STORAGE);
        createDir(GRAPH);
        createDir(INDEX);
    }

    private static void createDir(String dir) {
        File d = new File(dir);
        if(!d.exists()) {
            if(d.mkdir())
                System.out.println("Created new directory: " + dir);
            else
                System.out.println("Failed to create directory " + dir);
        }
    }
}
