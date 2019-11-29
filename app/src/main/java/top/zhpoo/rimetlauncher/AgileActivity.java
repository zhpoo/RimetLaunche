package top.zhpoo.rimetlauncher;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import top.zhpoo.rimetlauncher.util.Utils;

public abstract class AgileActivity extends AppCompatActivity {

    private static final int[] mTempLocation = new int[2];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && autoHideKeyboardOnTouchEditTextOutside(ev)) {
            View v = getCurrentFocus();
            if (v instanceof EditText && !isTouchInEditText(ev)) {
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

    protected boolean isTouchInEditText(MotionEvent ev) {
        View decorView = getWindow().getDecorView();
        return isTouchInEditText(((ViewGroup) decorView), ev);
    }

    public static boolean isTouchInEditText(ViewGroup parent, MotionEvent ev) {
        if (parent == null) {
            return false;
        }
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            child.getLocationInWindow(mTempLocation);
            float x = ev.getX();
            float y = ev.getY();
            if (x > mTempLocation[0] && y > mTempLocation[1]
                    && x < mTempLocation[0] + child.getWidth()
                    && y < mTempLocation[1] + child.getHeight()) {
                if (child instanceof ViewGroup) {
                    return isTouchInEditText((ViewGroup) child, ev);
                } else if (child instanceof EditText) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
}
