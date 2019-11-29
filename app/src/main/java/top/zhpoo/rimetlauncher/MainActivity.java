package top.zhpoo.rimetlauncher;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Random;

import top.zhpoo.rimetlauncher.Constants.PackageName;
import top.zhpoo.rimetlauncher.util.Logger;
import top.zhpoo.rimetlauncher.util.SpKey;
import top.zhpoo.rimetlauncher.util.SpManager;
import top.zhpoo.rimetlauncher.util.Toasts;

public class MainActivity extends AgileActivity implements OnClickListener {

    private static final int DEFAULT_BACK_DELAY_SECONDS = 30;
    private static final int DEFAULT_WEEKDAYS_STATE = 0x000000ff >> 1;

    private final Handler mHandler = new Handler();

    private final Runnable mBackRunner = () -> {
        Intent me = new Intent(App.getContext(), MainActivity.class);
        me.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getContext().startActivity(me);
        Logger.d("back .");
        start();
    };
    private Integer mBackDelaySeconds;
    private Integer mFloatingTimeMinutes;

    private final Runnable mRunnable = () -> {
        final Context context = App.getContext();
        final PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(PackageName.DING_DING);
        if (intent == null) {
            Toasts.show("package not installed (or has no launcher activity): " + PackageName.DING_DING);
            return;
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Logger.d("ding .");
        mHandler.postDelayed(mBackRunner, mBackDelaySeconds * 1000);
    };
    private Button mButton;
    private CountDownTimer mTimer;

    private Button mBtnWorkTime;
    private Button mBtnOffTime;
    private EditText mEtBackDelaySeconds;
    private EditText mEtFloatingTimeMinutes;
    private WeekdaysChecker mWeekdaysChecker;

    private Integer mHour1;
    private Integer mMinute1;

    private Integer mHour2;
    private Integer mMinute2;

    private Integer mWeekdaysCheckedStates;

    @Override
    protected boolean autoHideKeyboardOnTouchEditTextOutside(MotionEvent ev) {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(this);

        mBtnWorkTime = findViewById(R.id.btn_work_time);
        mBtnOffTime = findViewById(R.id.btn_off_time);
        mWeekdaysChecker = findViewById(R.id.weekdays_checker);
        mEtBackDelaySeconds = findViewById(R.id.et_stay_in_dingding_time);
        mEtFloatingTimeMinutes = findViewById(R.id.et_floating_time);

        mBtnWorkTime.setOnClickListener(this);
        mBtnOffTime.setOnClickListener(this);

        readSavedSettings();

        updateTime1Text();
        updateTime2Text();

        mEtBackDelaySeconds.setText(mBackDelaySeconds == null ? null : mBackDelaySeconds.toString());
        mEtBackDelaySeconds.setSelection(mEtBackDelaySeconds.getText().length());

        mEtFloatingTimeMinutes.setText(mFloatingTimeMinutes == null ? null : mFloatingTimeMinutes.toString());
        mEtFloatingTimeMinutes.setSelection(mEtFloatingTimeMinutes.getText().length());

        mWeekdaysChecker.setCheckedState(mWeekdaysCheckedStates == null ? DEFAULT_WEEKDAYS_STATE : mWeekdaysCheckedStates);
    }

    private void readSavedSettings() {
        SpManager sp = SpManager.getInstance();
        int spInt = sp.getInt(SpKey.TIME_HOUR1, -1);
        mHour1 = spInt == -1 ? null : spInt;

        spInt = sp.getInt(SpKey.TIME_MINUTE1, -1);
        mMinute1 = spInt == -1 ? null : spInt;

        spInt = sp.getInt(SpKey.TIME_HOUR2, -1);
        mHour2 = spInt == -1 ? null : spInt;

        spInt = sp.getInt(SpKey.TIME_MINUTE2, -1);
        mMinute2 = spInt == -1 ? null : spInt;

        spInt = sp.getInt(SpKey.WEEKDAYS_STATE, -1);
        mWeekdaysCheckedStates = spInt == -1 ? null : spInt;

        spInt = sp.getInt(SpKey.TIME_FLOATING_MINUTES, -1);
        mFloatingTimeMinutes = spInt == -1 ? null : spInt;

        spInt = sp.getInt(SpKey.BACK_DELAY_SECONDS, -1);
        mBackDelaySeconds = spInt == -1 ? null : spInt;
    }

    private void updateTime1Text() {
        String time1 = mHour1 == null ? "" : "\n" + twoDigits(mHour1) + ":" + twoDigits(mMinute1);
        mBtnWorkTime.setText(getString(R.string.setupTime1, time1));
    }

    private void updateTime2Text() {
        String time2 = mHour2 == null ? "" : "\n" + twoDigits(mHour2) + ":" + twoDigits(mMinute2);
        mBtnOffTime.setText(getString(R.string.setupTime2, time2));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                if (isRunning()) {
                    stop();
                    mButton.setText(R.string.start);
                    mEtFloatingTimeMinutes.setEnabled(true);
                    mEtBackDelaySeconds.setEnabled(true);
                    mWeekdaysChecker.setEnabled(true);
                } else if (prepareStart() && start()) {
                    mEtFloatingTimeMinutes.setEnabled(false);
                    mEtBackDelaySeconds.setEnabled(false);
                    mWeekdaysChecker.setEnabled(false);
                }
                break;
            case R.id.btn_work_time:
                if (isRunning()) {
                    Toasts.show(R.string.stopTips);
                } else {
                    TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                        mHour1 = hourOfDay;
                        mMinute1 = minute;
                        updateTime1Text();
                        SpManager sp = SpManager.getInstance();
                        sp.edit().putInt(SpKey.TIME_HOUR1, mHour1).putInt(SpKey.TIME_MINUTE1, mMinute1).apply();
                    }, mHour1 == null ? 0 : mHour1, mMinute1 == null ? 0 : mMinute1, true);
                    dialog.show();
                }
                break;
            case R.id.btn_off_time:
                if (isRunning()) {
                    Toasts.show(R.string.stopTips);
                } else {
                    TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                        mHour2 = hourOfDay;
                        mMinute2 = minute;
                        updateTime2Text();
                        SpManager sp = SpManager.getInstance();
                        sp.edit().putInt(SpKey.TIME_HOUR2, mHour2).putInt(SpKey.TIME_MINUTE2, mMinute2).apply();
                    }, mHour2 == null ? 0 : mHour2, mMinute2 == null ? 0 : mMinute2, true);
                    dialog.show();
                }
                break;
        }
    }

    private boolean prepareStart() {
        if (mHour1 == null && mHour2 == null) {
            Toasts.show(R.string.setupTimeTips1);
            return false;
        }

        int checkedState = mWeekdaysChecker.getCheckedState();
        if (checkedState == 0) {
            Toasts.show(R.string.weekdaysTips);
            return false;
        }
        mWeekdaysCheckedStates = checkedState;
        SpManager.getInstance().putInt(SpKey.WEEKDAYS_STATE, mWeekdaysCheckedStates);

        mFloatingTimeMinutes = 0;
        String floatingTime = mEtFloatingTimeMinutes.getText().toString().trim();
        if (!TextUtils.isEmpty(floatingTime)) {
            try {
                mFloatingTimeMinutes = Math.abs(Integer.parseInt(floatingTime));
                SpManager.getInstance().putInt(SpKey.TIME_FLOATING_MINUTES, mFloatingTimeMinutes);
            } catch (NumberFormatException e) {
                Toasts.show(R.string.invalidNumber);
                mEtFloatingTimeMinutes.setText(null);
                SpManager.getInstance().remove(SpKey.TIME_FLOATING_MINUTES);
            }
        } else {
            SpManager.getInstance().remove(SpKey.TIME_FLOATING_MINUTES);
        }

        mBackDelaySeconds = DEFAULT_BACK_DELAY_SECONDS;
        String backTimeStr = mEtBackDelaySeconds.getText().toString().trim();
        if (!TextUtils.isEmpty(backTimeStr)) {
            try {
                int backTimeSeconds = Integer.parseInt(backTimeStr);
                if (backTimeSeconds <= 0) {
                    Toasts.show(R.string.invalidNumber);
                    mEtBackDelaySeconds.setText(null);
                    SpManager.getInstance().remove(SpKey.BACK_DELAY_SECONDS);
                } else {
                    mBackDelaySeconds = backTimeSeconds;
                    SpManager.getInstance().putInt(SpKey.BACK_DELAY_SECONDS, backTimeSeconds);
                }
            } catch (NumberFormatException e) {
                Toasts.show(R.string.invalidNumber);
                mEtBackDelaySeconds.setText(null);
                SpManager.getInstance().remove(SpKey.BACK_DELAY_SECONDS);
            }
        } else {
            SpManager.getInstance().remove(SpKey.BACK_DELAY_SECONDS);
        }
        return true;
    }

    private boolean start() {
        stop();

        final long current = System.currentTimeMillis();

        Calendar before = null;
        Calendar after = null;

        if (mHour1 != null) {
            before = Calendar.getInstance();
            before.setTimeInMillis(current);
            before.set(Calendar.HOUR_OF_DAY, mHour1);
            before.set(Calendar.MINUTE, mMinute1);
            before.set(Calendar.SECOND, 0);
            before.set(Calendar.MILLISECOND, 0);
        }
        if (mHour2 != null) {
            after = Calendar.getInstance();
            after.setTimeInMillis(current);
            after.set(Calendar.HOUR_OF_DAY, mHour2);
            after.set(Calendar.MINUTE, mMinute2);
            after.set(Calendar.SECOND, 0);
            after.set(Calendar.MILLISECOND, 0);
        }
        if (before == null && after == null) {
            Toasts.show(R.string.setupTimeTips1);
            return false;
        }
        if (before == null) {
            before = Calendar.getInstance();
            before.setTimeInMillis(after.getTimeInMillis());
        } else if (after == null) {
            after = Calendar.getInstance();
            after.setTimeInMillis(before.getTimeInMillis());
        }
        if (before.after(after)) {
            Calendar temp = after;
            after = before;
            before = temp;
        }

        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(current);

        Calendar next;

        if (now.before(before)) {
            next = before;
        } else if (now.before(after)) {
            next = after;
        } else {
            before.add(Calendar.DAY_OF_MONTH, 1);
            after.add(Calendar.DAY_OF_MONTH, 1);
            next = before;
        }

        while (!isWeekdayChecked(next)) {
            before.add(Calendar.DAY_OF_MONTH, 1);
            after.add(Calendar.DAY_OF_MONTH, 1);
            next = before;
        }

        long delay = next.getTimeInMillis() - current;

        if (mFloatingTimeMinutes > 0) {
            int floatingTimeMs = mFloatingTimeMinutes * 60 * 1000;
            int randomInt = new Random().nextInt(floatingTimeMs * 2) - floatingTimeMs;
            delay += randomInt;
        }

        mHandler.postDelayed(mRunnable, delay);
        startTimer(delay);
        return true;
    }

    private boolean isWeekdayChecked(Calendar time) {
        int weekdayIndex = time.get(Calendar.DAY_OF_WEEK) - 1;
        return ((mWeekdaysCheckedStates >> weekdayIndex) & 1) == 1;
    }

    private void stop() {
        mHandler.removeCallbacksAndMessages(null);
        cancelTimer();
    }

    private boolean isRunning() {
        return mHandler.hasCallbacks(mRunnable);
    }

    private void startTimer(long millisInFuture) {
        cancelTimer();
        mTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hour = millisUntilFinished / 1000 / 60 / 60;
                long minute = millisUntilFinished / 1000 / 60 % 60;
                long second = millisUntilFinished / 1000 % 60;
                String leftTime = twoDigits(hour) + ":" + twoDigits(minute) + ":" + twoDigits(second);
                mButton.setText(getString(R.string.exchangeTime, leftTime, mBackDelaySeconds));
            }

            @Override
            public void onFinish() {
            }
        };
        mTimer.start();
    }

    private void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private String twoDigits(long value) {
        return value < 10 ? "0" + value : "" + value;
    }
}
