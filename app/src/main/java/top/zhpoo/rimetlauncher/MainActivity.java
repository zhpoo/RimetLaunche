package top.zhpoo.rimetlauncher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import top.zhpoo.rimetlauncher.Constants.PackageName;
import top.zhpoo.rimetlauncher.util.Logger;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    public static final int BACK_DELAY_MILLIS = 6 * 1000;
    public static final int DING_DELAY_MILLIS = 60 * 1000;

    private static int sCount = 0;

    private static final Handler mHandler = new Handler();

    private static final Runnable mDingDingStarter = () -> {
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
    };

    private static final Runnable mBackRunner = () -> {
        sCount++;
        Intent me = new Intent(App.getContext(), MainActivity.class);
        me.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getContext().startActivity(me);
        Logger.d("back .");
        start(DING_DELAY_MILLIS);
    };

    private static final Runnable mRunnable = () -> {
        mDingDingStarter.run();
        mHandler.postDelayed(mBackRunner, BACK_DELAY_MILLIS);
    };
    private Button mButton;
    private TextView mTvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvInfo = findViewById(R.id.tv_info);
        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(this);
        sCount++;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTvInfo.setText(getString(R.string.exchangedCount, sCount));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            if (mHandler.hasCallbacks(mRunnable)) {
                stop();
            } else {
                start(0);
            }
            setButtonText();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.d("onNewIntent: %s", intent.toString());
    }

    private void setButtonText() {
        mButton.setText(mHandler.hasCallbacks(mRunnable) ? R.string.stop : R.string.start);
    }

    private static void start(long delayTime) {
        mHandler.postDelayed(mRunnable, delayTime);
    }

    private static void stop() {
        mHandler.removeCallbacksAndMessages(null);
    }
}
