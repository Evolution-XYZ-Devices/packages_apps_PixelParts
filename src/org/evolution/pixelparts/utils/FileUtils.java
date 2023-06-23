/*
 * Copyright (C) 2016 The CyanogenMod Project
 *               2018-2022 crDroid Android Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.pixelparts.utils;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

import org.evolution.pixelparts.R;

import android.util.Log;

public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    /**
     * Reads the first line of text from the given file.
     * Reference {@link BufferedReader#readLine()} for clarification on what a line is
     *
     * @return the read line contents, or null on failure
     */
    public static String readOneLine(String fileName) {
        String line = null;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(fileName), 512);
            line = reader.readLine();
        } catch (FileNotFoundException e) {
            Log.w(TAG, "No such file " + fileName + " for reading", e);
        } catch (IOException e) {
            Log.e(TAG, "Could not read from file " + fileName, e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // Ignored, not much we can do anyway
            }
        }

        return line;
    }

    /**
     * Write a string value to the specified file.
     * @param filename      The filename
     * @param value         The value
     */
    public static void writeValue(String filename, String value) {
        if (filename == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(new File(filename));
            fos.write(value.getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the contents of the file with the given filename, or the specified default value if the file cannot be read.
     *
     * @param filename the name of the file to read
     * @param defValue the default value to return if the file cannot be read
     * @return the contents of the file as a String, or the default value if the file cannot be read
     */
    public static String getFileValue(String filename, String defValue) {
        String fileValue = readOneLine(filename);
        if (fileValue != null) {
            return fileValue;
        }
        return defValue;
    }

    /**
     * Checks whether the given file exists
     *
     * @return true if exists, false if not
     */
    public static boolean fileExists(String fileName) {
        final File file = new File(fileName);
        return file.exists();
    }

    /**
     * Checks whether the given file is readable
     *
     * @return true if readable, false if not
     */
    public static boolean isFileReadable(String fileName) {
        final File file = new File(fileName);
        return file.exists() && file.canRead();
    }

    /**
     * Checks whether the given file is writable
     *
     * @return true if writable, false if not
     */
    public static boolean isFileWritable(String fileName) {
        final File file = new File(fileName);
        return file.exists() && file.canWrite();
    }
}
