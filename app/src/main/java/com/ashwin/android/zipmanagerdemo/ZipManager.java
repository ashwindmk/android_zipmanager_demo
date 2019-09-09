package com.ashwin.android.zipmanagerdemo;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipManager {
    public static final String TAG = "ZipManager";

    private static int BUFFER_SIZE = 6 * 1024;

    public static void zip(String inputDirPath, String outputDirPath) throws IOException {
        File[] files = new File(inputDirPath).listFiles();
        String[] filePaths = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            filePaths[i] = files[i].getAbsolutePath();
        }

        zip(filePaths, outputDirPath);
    }

    public static void zip(String[] filePaths, String outputFilePath) throws IOException {
        String[] files = filePaths;
        String zipFile = outputFilePath;

        BufferedInputStream origin = null;
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
        try {
            byte data[] = new byte[BUFFER_SIZE];

            for (int i = 0; i < files.length; i++) {
                String filePath = files[i];
                File file = new File(filePath);
                if (file.isFile()) {
                    FileInputStream fi = new FileInputStream(filePath);
                    origin = new BufferedInputStream(fi, BUFFER_SIZE);
                    try {
                        ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
                        out.putNextEntry(entry);
                        int count;
                        while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                            out.write(data, 0, count);
                        }
                    } catch (Exception e) {
                        Log.e(ZipManager.TAG, "Exception while zipping file: " + filePath, e);
                    } finally {
                        origin.close();
                    }
                } else {
                    Log.e(ZipManager.TAG, "Path is not a file");
                }
            }
        } catch (Exception e) {
            Log.e(ZipManager.TAG, "Exception while zipping", e);
        } finally {
            out.close();
        }
    }

    public static void unzip(String zipFile, String location) throws IOException {
       File f = new File(location);
        if (!f.isDirectory()) {
            f.mkdirs();
        }

        ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
        try {
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                String path = location + File.separator + ze.getName();

                if (ze.isDirectory()) {
                    File unzipFile = new File(path);
                    if (!unzipFile.isDirectory()) {
                        unzipFile.mkdirs();
                    }
                } else {
                    FileOutputStream fout = new FileOutputStream(path, false);
                    try {
                        for (int c = zin.read(); c != -1; c = zin.read()) {
                            fout.write(c);
                        }
                        zin.closeEntry();
                    } finally {
                        fout.close();
                    }
                }
            }
        } finally {
            zin.close();
        }
    }
}
