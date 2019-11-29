package top.zhpoo.rimetlauncher;

import android.app.Application;
import android.content.Context;

import top.zhpoo.rimetlauncher.util.SpManager;
import top.zhpoo.rimetlauncher.util.Toasts;

public class App extends Application {
    private static Application app;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        app = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SpManager.init(this);
        Toasts.init(this);
    }

    public static Context getContext() {
        return app;
    }
}
