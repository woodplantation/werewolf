package com.woodplantation.werwolf.graphics;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.EditText;

import com.woodplantation.werwolf.R;

/**
 * Created by Sebu on 01.11.2016.
 */

public class MyEditText extends EditText {
    public MyEditText(Context context) {
        super(context);
        init();
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setTextColor(getResources().getColor(R.color.text_color, null));
        } else {
            setTextColor(getResources().getColor(R.color.text_color));
        }
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.activity_start_text_size));
        setGravity(Gravity.CENTER_HORIZONTAL);
        setSingleLine(true);
    }
}
