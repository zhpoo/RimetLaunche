package top.zhpoo.rimetlauncher;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import top.zhpoo.rimetlauncher.util.Utils;

public abstract class AgileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && autoHideKeyboardOnTouchEditTextOutside(ev)) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                int[] focusLocation = new int[2];
                v.getLocationInWindow(focusLocation);
                if (ev.getX() < focusLocation[0] || ev.getY() < focusLocation[1]
                        || ev.getX() > focusLocation[0] + v.getWidth()
                        || ev.getY() > focusLocation[1] + v.getHeight()) {
                    // Clicked the outside of EditText.
                    Utils.hideKeyboard(this, v);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 点击时是否自动隐藏输入法键盘，默认不关闭。
     *
     * @return true 自动在点击EditText之外区域时隐藏输入法，false 不自动关闭。
     */
    @SuppressWarnings("unused")
    protected boolean autoHideKeyboardOnTouchEditTextOutside(MotionEvent ev) {
        return false;
    }
}
