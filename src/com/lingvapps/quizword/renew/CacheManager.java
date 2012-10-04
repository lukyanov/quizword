package com.lingvapps.quizword.renew;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

public class CacheManager {

    private static final long MAX_SIZE = 1048576L; // 1MB

    @TargetApi(9)
    public static void cacheData(Context context, byte[] data, String name)
            throws IOException {

        File cacheDir = context.getCacheDir();
        long size = getDirSize(cacheDir);
        long newSize = data.length + size;

        if (newSize > MAX_SIZE) {
            cleanDir(cacheDir, newSize - MAX_SIZE);
        }

        File file = new File(cacheDir, name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            file.setReadable(true, false);
        }

        FileOutputStream os = new FileOutputStream(file);
        try {
            os.write(data);
        } finally {
            os.flush();
            os.close();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
                chmod(file, 0644);
            }
        }
    }

    public static Boolean cacheExists(Context context, String name) {
        File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, name);
        return file.exists();
    }

    public static byte[] retrieveData(Context context, String name)
            throws IOException {

        File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, name);

        if (!file.exists()) {
            // Data doesn't exist
            return null;
        }

        byte[] data = new byte[(int) file.length()];
        FileInputStream is = new FileInputStream(file);
        try {
            is.read(data);
        } finally {
            is.close();
        }

        return data;
    }

    public static void clearCache(Context context) {
        File[] files = context.getCacheDir().listFiles();
        for (File file : files) {
            file.delete();
        }
    }

    private static void cleanDir(File dir, long bytes) {

        long bytesDeleted = 0;
        File[] files = dir.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });

        for (File file : files) {
            bytesDeleted += file.length();
            file.delete();

            if (bytesDeleted >= bytes) {
                break;
            }
        }
    }

    private static long getDirSize(File dir) {

        long size = 0;
        File[] files = dir.listFiles();

        for (File file : files) {
            if (file.isFile()) {
                size += file.length();
            }
        }

        return size;
    }

    public static int chmod(File path, int mode) {
        try {
            Class<?> fileUtils = Class.forName("android.os.FileUtils");
            Method setPermissions = fileUtils.getMethod("setPermissions",
                    String.class, int.class, int.class, int.class);
            return (Integer) setPermissions.invoke(null,
                    path.getAbsolutePath(), mode, -1, -1);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
