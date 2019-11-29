package top.zhpoo.rimetlauncher.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Set;

public class SpManager {

    public static final String DEFAULT_PREFERENCE_NAME = "frame";
    private static final HashMap<String, SpManager> instanceMap = new HashMap<>();

    private static Application sContext;

    private SharedPreferences mSp;
    private boolean mCommitMode = false;

    private SpManager(String name) {
        if (DEFAULT_PREFERENCE_NAME.equals(name)) {
            mSp = PreferenceManager.getDefaultSharedPreferences(sContext);
        } else {
            mSp = sContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        }
    }

    public static void init(Application context) {
        sContext = context;
    }

    /**
     * get the default SharedPreference manager with {@link #DEFAULT_PREFERENCE_NAME}
     *
     * @param commitMode save mode, only the first element is useful.
     *                   true to {@link Editor#commit()}, false to {@link Editor#apply()}.
     * @return the manager instance.
     */
    public static SpManager getInstance(boolean... commitMode) {
        return getInstance(DEFAULT_PREFERENCE_NAME, commitMode);
    }

    /**
     * get the default SharedPreference manager with {@link #DEFAULT_PREFERENCE_NAME}
     *
     * @param name       the SharedPreference name you want to manage.
     * @param commitMode save mode, only the first element is useful.
     *                   true to {@link Editor#commit()}, false to {@link Editor#apply()}.
     * @return the manager instance.
     */
    public static SpManager getInstance(String name, boolean... commitMode) {
        if (instanceMap.get(name) == null) {
            synchronized (instanceMap) {
                if (instanceMap.get(name) == null) {
                    instanceMap.put(name, new SpManager(name));
                }
            }
        }
        SpManager manager = instanceMap.get(name);
        manager.mCommitMode = commitMode != null && commitMode.length > 0 && commitMode[0];
        return manager;
    }

    /**
     * provide the SharedPreference instance to manage what you want to.
     *
     * @return the SharedPreference instance.
     */
    public SharedPreferences getSp() {
        return mSp;
    }

    /**
     * provide the SharedPreference editor to manage what you want to.
     *
     * @return the editor.
     */
    public Editor edit() {
        return mSp.edit();
    }

    /**
     * save a boolean value.
     *
     * @param key   key
     * @param value boolean value.
     */
    public void putBoolean(String key, boolean value) {
        save(edit().putBoolean(key, value));
    }

    /**
     * get the boolean value with given key.
     *
     * @param key      key
     * @param defValue default value if the key not saved. only the first element is useful. default false.
     * @return the boolean value.
     */
    public boolean getBoolean(String key, boolean... defValue) {
        return mSp.getBoolean(key, defValue != null && defValue.length > 0 && defValue[0]);
    }

    /**
     * save a float value.
     *
     * @param key   key
     * @param value float value.
     */
    public void putFloat(String key, float value) {
        save(edit().putFloat(key, value));
    }

    /**
     * get the float value with given key.
     *
     * @param key      key
     * @param defValue default value if the key not saved. only the first element is useful. default 0.
     * @return the float value.
     */
    public float getFloat(String key, float... defValue) {
        return mSp.getFloat(key, defValue != null && defValue.length > 0 ? defValue[0] : 0);
    }

    /**
     * save a long value.
     *
     * @param key   key
     * @param value long value.
     */
    public void putLong(String key, long value) {
        save(edit().putLong(key, value));
    }

    /**
     * get the long value with given key.
     *
     * @param key      key
     * @param defValue default value if the key not saved. only the first element is useful. default 0.
     * @return the long value.
     */
    public long getLong(String key, long... defValue) {
        return mSp.getLong(key, defValue != null && defValue.length > 0 ? defValue[0] : 0);
    }

    /**
     * save a int value.
     *
     * @param key   key
     * @param value int value.
     */
    public void putInt(String key, int value) {
        save(edit().putInt(key, value));
    }

    /**
     * get the int value with given key.
     *
     * @param key      key
     * @param defValue default value if the key not saved. only the first element is useful. default 0.
     * @return the int value.
     */
    public int getInt(String key, int... defValue) {
        return mSp.getInt(key, defValue != null && defValue.length > 0 ? defValue[0] : 0);
    }

    /**
     * save a String value.
     *
     * @param key   key
     * @param value String value.
     */
    public void putString(String key, String value) {
        save(edit().putString(key, value));
    }

    /**
     * get the String value with given key.
     *
     * @param key      key
     * @param defValue default value if the key not saved. only the first element is useful. default null.
     * @return the String value.
     */
    public String getString(String key, String... defValue) {
        return mSp.getString(key, defValue != null && defValue.length > 0 ? defValue[0] : null);
    }

    /**
     * save a StringSet value.
     *
     * @param key   key
     * @param value StringSet value.
     */
    public void putStringSet(String key, Set<String> value) {
        save(edit().putStringSet(key, value));
    }

    /**
     * get the StringSet value with given key.
     *
     * @param key      key
     * @param defValue default value if the key not saved. only the first element is useful. default null.
     * @return the StringSet value.
     */
    public Set<String> getStringSet(String key, Set<String>... defValue) {
        return mSp.getStringSet(key, defValue != null && defValue.length > 0 ? defValue[0] : null);
    }

    /**
     * delete a value with given key.
     *
     * @param key the key
     */
    public void remove(String key) {
        save(edit().remove(key));
    }

    /**
     * clear all preferences value
     */
    public void clear() {
        save(edit().clear());
    }

    private void save(Editor editor) {
        if (mCommitMode) {
            editor.commit();
        } else {
            editor.apply();
        }
    }
}
