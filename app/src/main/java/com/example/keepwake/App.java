package com.example.keepwake;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

public class App extends Application {
    private static final String appName = "keep-wake";
    final Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler =
            Thread.getDefaultUncaughtExceptionHandler();

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler((paramThread, paramThrowable) -> {
            Logger.e(paramThrowable, "----------UncaughtException throw---------");
            if (defaultUncaughtExceptionHandler != null) {
                defaultUncaughtExceptionHandler.uncaughtException(paramThread, paramThrowable);
            }
        });

        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)
                .tag(appName)
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        int pid = android.os.Process.myPid();
        Logger.i("~~~~~~~%s app startup,pid:%s~~~~~~~~", appName, pid);
    }
}
