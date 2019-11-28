package top.zhpoo.rimetlauncher.util;

import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Utils {
    public static void hideKeyboard(Context context, View view) {
        if (view == null) {
            return;
        }
        IBinder token = view.getWindowToken();
        if (token == null) {
            return;
        }
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (im == null) {
            return;
        }
        im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
