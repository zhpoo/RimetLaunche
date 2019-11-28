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
import android.widget.Toast;

import java.util.Calendar;
import java.util.Random;

import top.zhpoo.rimetlauncher.Constants.PackageName;
import top.zhpoo.rimetlauncher.util.Logger;

public class MainActivity extends AgileActivity implements OnClickListener {

    private static final int DEFAULT_BACK_DELAY_MILLIS = 30 * 1000;
    private static final long ONE_DAY = 24 * 60 * 60 * 1000;

    private final Handler mHandler = new Handler();

    private final Runnable mBackRunner = () -> {
        Intent me = new Intent(App.getContext(), MainActivity.class);
        me.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getContext().startActivity(me);
        Logger.d("back .");
        start();
    };
    private int backDelayMillis = DEFAULT_BACK_DELAY_MILLIS;

    private final Runnable mRunnable = () -> {
        final Context context = App.getContext();
        final PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(PackageName.DING_DING);
        if (intent == null) {
            Toast.makeText(context, "package not installed (or has no launcher activity): " + PackageName.DING_DING, Toast.LENGTH_SHORT).show();
            return;
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Logger.d("ding .");
        mHandler.postDelayed(mBackRunner, backDelayMillis);
    };
    private Button mButton;
    private CountDownTimer mTimer;

    private Button mBtnWorkTime;
    private Button mBtnOffTime;
    private EditText mEtStayInDingdingTime;
    private EditText mEtFloatingTime;

    private Integer mHour1;
    private Integer mMinute1;

    private Integer mHour2;
    private Integer mMinute2;


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
        mEtStayInDingdingTime = findViewById(R.id.et_stay_in_dingding_time);
        mEtFloatingTime = findViewById(R.id.et_floating_time);

        mBtnWorkTime.setOnClickListener(this);
        mBtnOffTime.setOnClickListener(this);
        updateSettingText();
    }

    private void updateSettingText() {
        String time1 = mHour1 == null ? "" : "\n" + twoDigits(mHour1) + ":" + twoDigits(mMinute1);
        String time2 = mHour2 == null ? "" : "\n" + twoDigits(mHour2) + ":" + twoDigits(mMinute2);

        mBtnWorkTime.setText(getString(R.string.setupTime1, time1));
        mBtnOffTime.setText(getString(R.string.setupTime2, time2));
    }

    private String twoDigits(long value) {
        return value < 10 ? "0" + value : "" + value;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
    }

    private void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                if (isRunning()) {
                    stop();
                    mButton.setText(R.string.start);
                    mEtFloatingTime.setEnabled(true);
                    mEtStayInDingdingTime.setEnabled(true);
                } else if (start()) {
                    mEtFloatingTime.setEnabled(false);
                    mEtStayInDingdingTime.setEnabled(false);
                }
                break;
            case R.id.btn_work_time:
                if (isRunning()) {
                    toast(R.string.stopTips);
                } else {
                    TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                        mHour1 = hourOfDay;
                        mMinute1 = minute;
                        updateSettingText();
                    }, mHour1 == null ? 0 : mHour1, mMinute1 == null ? 0 : mMinute1, true);
                    dialog.show();
                }
                break;
            case R.id.btn_off_time:
                if (isRunning()) {
                    toast(R.string.stopTips);
                } else {
                    TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                        mHour2 = hourOfDay;
                        mMinute2 = minute;
                        updateSettingText();
                    }, mHour2 == null ? 0 : mHour2, mMinute2 == null ? 0 : mMinute2, true);
                    dialog.show();
                }
                break;
        }
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
            toast(R.string.setupTimeTips1);
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
            next = Calendar.getInstance();
            next.setTimeInMillis(before.getTimeInMillis() + ONE_DAY);
        }

        long delay = next.getTimeInMillis() - current;

        String floatingTime = mEtFloatingTime.getText().toString().trim();
        if (!TextUtils.isEmpty(floatingTime)) {
            try {
                int i = Math.abs(Integer.parseInt(floatingTime));
                int randomInt = new Random().nextInt(i * 2) - i;
                delay += randomInt * 60 * 1000;
            } catch (NumberFormatException e) {
                toast(R.string.invalidNumber);
                mEtFloatingTime.setText("");
            }
        }

        backDelayMillis = DEFAULT_BACK_DELAY_MILLIS;
        String backTimeStr = mEtStayInDingdingTime.getText().toString().trim();
        if (!TextUtils.isEmpty(backTimeStr)) {
            try {
                int backTime = Integer.parseInt(backTimeStr);
                if (backTime <= 0) {
                    mEtStayInDingdingTime.setText("");
                } else {
                    backDelayMillis = backTime * 1000;
                }
            } catch (NumberFormatException e) {
                toast(R.string.invalidNumber);
                mEtStayInDingdingTime.setText("");
            }
        }

        start(delay);
        startTimer(delay);
        return true;
    }

    private void toast(int resId) {
        Toast.makeText(App.getContext(), resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.d("onNewIntent: %s", intent.toString());
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
                mButton.setText(getString(R.string.exchangeTime, leftTime, backDelayMillis / 1000));
            }

            @Override
            public void onFinish() {

            }
        };
        mTimer.start();
    }

    private boolean isRunning() {
        return mHandler.hasCallbacks(mRunnable);
    }

    private void start(long delayTime) {
        mHandler.postDelayed(mRunnable, delayTime);
    }

    private void stop() {
        mHandler.removeCallbacksAndMessages(null);
        cancelTimer();
    }
}
