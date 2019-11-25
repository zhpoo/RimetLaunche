package top.zhpoo.rimetlauncher.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.PrettyFormatStrategy;

import top.zhpoo.rimetlauncher.BuildConfig;

public class Logger {

    public static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String DEFAULT_TAG = "Logger";

    static {
        init();
    }

    private static void init() {
        if (DEBUG) {
            FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
//                    .showThreadInfo(false)
                    .methodCount(1)
                    .methodOffset(1)
                    .tag(DEFAULT_TAG)
                    .build();
            com.orhanobut.logger.Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        }
    }

    /**
     * General log function that accepts all configurations as parameter
     */
    public static void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable throwable) {
        if (DEBUG) {
            com.orhanobut.logger.Logger.log(priority, tag, message, throwable);
        }
    }

    public static void d(@NonNull String message, @Nullable Object... args) {
        if (DEBUG) {
            com.orhanobut.logger.Logger.d(message, args);
        }
    }

    public static void d(@Nullable Object object) {
        if (DEBUG) {
            com.orhanobut.logger.Logger.d(object);
        }
    }

    public static void e(@NonNull String message, @Nullable Object... args) {
        if (DEBUG) {
            com.orhanobut.logger.Logger.e(null, message, args);
        }
    }

    public static void e(@Nullable Throwable throwable) {
        if (DEBUG) {
            com.orhanobut.logger.Logger.e(throwable, "");
        }
    }

    public static void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
        if (DEBUG) {
            com.orhanobut.logger.Logger.e(throwable, message, args);
        }
    }

    public static void i(@NonNull String message, @Nullable Object... args) {
        if (DEBUG) {
            com.orhanobut.logger.Logger.i(message, args);
        }
    }

    public static void v(@NonNull String message, @Nullable Object... args) {
        if (DEBUG) {
            com.orhanobut.logger.Logger.v(message, args);
        }
    }

    public static void w(@NonNull String message, @Nullable Object... args) {
        if (DEBUG) {
            com.orhanobut.logger.Logger.w(message, args);
        }
    }

    /**
     * Tip: Use this for exceptional situations to log
     * ie: Unexpected errors etc
     */
    public static void wtf(@NonNull String message, @Nullable Object... args) {
        if (DEBUG) {
            com.orhanobut.logger.Logger.wtf(message, args);
        }
    }

    /**
     * Formats the given json content and print it
     */
    public static void json(@Nullable String json) {
        if (DEBUG) {
            com.orhanobut.logger.Logger.json(json);
        }
    }

    /**
     * Formats the given xml content and print it
     */
    public static void xml(@Nullable String xml) {
        if (DEBUG) {
            com.orhanobut.logger.Logger.xml(xml);
        }
    }

}
