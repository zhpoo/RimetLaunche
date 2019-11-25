package top.zhpoo.rimetlauncher;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    private static Application app;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        app = this;
    }

    public static Context getContext() {
        return app;
    }
}
