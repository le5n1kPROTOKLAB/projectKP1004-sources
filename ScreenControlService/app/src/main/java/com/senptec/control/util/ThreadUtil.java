package com.senptec.control.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtil {
    private static final ExecutorService threadExecutor = Executors.newFixedThreadPool(10);

    public static void execute(Runnable r){
        threadExecutor.execute(r);
    }
}
