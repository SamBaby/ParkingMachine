package util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Context context;
    private Class<?> restartActivity;

    public CustomExceptionHandler(Context context, Class<?> restartActivity) {
        this.context = context;
        this.restartActivity = restartActivity;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Log.e("CustomExceptionHandler", "App crashed", throwable);

        // Show a Toast message or dialog to notify the user
        new Handler(Looper.getMainLooper()).post(() -> {
            Util.showRestartDialog(context);
        });

        // Wait for some time to show the message
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Restart the application
        Intent intent = new Intent(context, restartActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        // Kill the current process
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(2);
    }
}
