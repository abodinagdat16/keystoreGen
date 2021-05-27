package com.example.myapp;

import android.app.Application;
import android.content.Intent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import android.content.Context;

public class MyApp extends Application {

    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    private static MyApp app;

    public static MyApp getApp() {
        return app;
    }

    @Override
    public void onCreate() {
        
        this.app = this;
        this.uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    Intent intent = new Intent(getApplicationContext(), DebugActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("error", getStackTrace(ex));
                    app.startActivity(intent);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                    uncaughtExceptionHandler.uncaughtException(thread, ex);
                }
            });
        super.onCreate();
    }

    private String getStackTrace(Throwable th) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        Throwable cause = th;
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        final String stacktraceAsString = result.toString();
        printWriter.close();
        return stacktraceAsString;
    }
}

