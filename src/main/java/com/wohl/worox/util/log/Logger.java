package com.wohl.worox.util.log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private Object target;
    public Logger(Object target) {
        this.target = target;
    }
    public void info(String log) {
        String stdout = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("MMM/dd/YYYY HH:mm:ss.SSS")) +
                " free_memo=" +
                String.format("%.3f",(Runtime.getRuntime().freeMemory() / 1024.0 / 1024.0)) + "MB" +
                " [INFO] -- " +
                "[" + Thread.currentThread().getName().toUpperCase() + "] " +
                target.getClass().toString().split(" ")[1] +
                " : " + log;
        System.out.println(stdout);
    }
    public void warn(String warn) {
        String stdout = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("MMM/dd/YYYY HH:mm:ss.SSS")) +
                " Free_memo=" +
                String.format("%.3f",(Runtime.getRuntime().freeMemory() / 1024.0 / 1024.0)) + "MB" +
                " [WARN] -- " +
                "[" + Thread.currentThread().getName().toUpperCase() + "] " +
                target.getClass().toString().split(" ")[1] +
                " : " + warn;
        System.out.println(stdout);
    }
    public void err(Throwable e) {
        String stdout = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("MMM/dd/YYYY HH:mm:ss.SSS")) +
                " Free_memo=" +
                String.format("%.3f",(Runtime.getRuntime().freeMemory() / 1024.0 / 1024.0)) + "MB" +
                " [ERROR] -- " +
                "[" + Thread.currentThread().getName().toUpperCase() + "] " +
                target.getClass().toString().split(" ")[1] +
                " : " + e.getMessage();
        System.out.println(stdout);
    }
}
