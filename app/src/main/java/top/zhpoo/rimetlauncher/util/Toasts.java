package top.zhpoo.rimetlauncher.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.widget.Toast;

import androidx.annotation.StringRes;

public class Toasts {

    private static Application sApp;
    private static Toast sToast;

    @SuppressLint("ShowToast")
    public static void init(Application app) {
        sApp = app;
    }

    public static void show(final CharSequence text) {
        if (sApp == null) {
            Logger.w("should init with Toasts.init(Application) first");
            return;
        }
        recreate();
        sToast.setText(text);
        sToast.show();
    }

    public static void show(@StringRes final int resId) {
        if (sApp == null) {
            Logger.w("should init with Toasts.init(Application) first");
            return;
        }
        recreate();
        sToast.setText(resId);
        sToast.show();
    }

    @SuppressLint("ShowToast")
    private static void recreate() {
        if (sToast != null) {
            sToast.cancel();
        }
        sToast = Toast.makeText(sApp, "", Toast.LENGTH_SHORT);
    }
}
