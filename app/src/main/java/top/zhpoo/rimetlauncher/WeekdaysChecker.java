package top.zhpoo.rimetlauncher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class WeekdaysChecker extends LinearLayout {

    private int mCheckedState;
    private OnCheckedChangeListener mOnCheckedChangedListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int index = indexOfChild(buttonView);
            if (index < 0) {
                return;
            }
            if (isChecked) {
                mCheckedState |= 1 << index;
            } else {
                mCheckedState &= ~(1 << index);
            }
        }
    };

    public WeekdaysChecker(Context context) {
        this(context, null);
    }

    public WeekdaysChecker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekdaysChecker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.HORIZONTAL);
        addWeekdaysView();
    }

    public void setCheckedState(int checkedState) {
        mCheckedState = checkedState;
        updateCheckedState();
    }

    private void updateCheckedState() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ((CheckBox) getChildAt(i)).setChecked(isChecked(i));
        }
    }

    private boolean isChecked(int index) {
        return ((mCheckedState >> index) & 1) == 1;
    }

    public int getCheckedState() {
        return mCheckedState;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).setEnabled(enabled);
        }
    }

    private void addWeekdaysView() {
        removeAllViews();
        String[] weekdays = getContext().getResources().getStringArray(R.array.weekdays);
        for (int i = 0; i < weekdays.length; i++) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(weekdays[i]);
            checkBox.setChecked(isChecked(i));
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            checkBox.setLayoutParams(params);
            checkBox.setEnabled(isEnabled());
            checkBox.setOnCheckedChangeListener(mOnCheckedChangedListener);
            addView(checkBox);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() == 7) {
            throw new UnsupportedOperationException("not support add more view");
        }
        super.addView(child, index, params);
    }
}
