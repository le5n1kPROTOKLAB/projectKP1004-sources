package com.spentec.server_client.util;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class ThreadUtil {
    private static final ExecutorService threadExecutor = Executors.newSingleThreadExecutor();

    public static void execute(Runnable r) {
        threadExecutor.execute(r);
    }

    private static final ExecutorService moreThreadExecutor = Executors.newScheduledThreadPool(8 * 2);

    public static void executeMore(Runnable r) {
        moreThreadExecutor.execute(r);
    }

    private static int getNumCores() {
        // Private Class to display only CPU devices in the directory listing
        try {
            // Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            // Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            // Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            // Default to return 1 core
            return 1;
        }
    }

    public static void say() {
        System.out.println("====================== num: " + getNumCores());
    }

    static class CpuFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            // Check if filename is "cpu", followed by a single digit number
            if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                return true;
            }
            return false;
        }
    }
}
