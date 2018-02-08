package net.chaosworship.topuslib.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.SeekBar;

import net.chaosworship.topuslib.R;


public class FloatSeekBar extends android.support.v7.widget.AppCompatSeekBar {

    private static final int NOTCHES = 200;
    private final float mCenterSnapMargin;

    public interface OnSeekBarChangeListener {
        void onProgressChanged(float value);
    }

    public FloatSeekBar(Context context) {
        super(context);
        throw new AssertionError();
    }

    public FloatSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMax(NOTCHES);

        boolean snap;
        TypedArray attrArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.FloatSeekBar, 0, 0);
        try {
            snap = attrArray.getBoolean(R.styleable.FloatSeekBar_centerSnap, false);
        } finally {
            attrArray.recycle();
        }

        mCenterSnapMargin = snap ? 0.05f : 0;
    }

    public FloatSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        throw new AssertionError();
    }

    public void setProgress(float value) {
        value = value < 0 ? 0 : value;
        value = value > 1 ? 1 : value;
        setProgress((int)(value * getMax()));
    }

    public void setOnSeekBarChangeListener(final FloatSeekBar.OnSeekBarChangeListener listener) {
        super.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float value = (float)i / getMax();
                if(mCenterSnapMargin > 0) {
                    if(value > 0.5f - mCenterSnapMargin && value < 0.5f + mCenterSnapMargin) {
                        setProgress(0.5f);
                        value = 0.5f;
                    } else {
                        if(value < 0.5f) {
                            value = value * 0.5f / (0.5f - mCenterSnapMargin);
                        } else {
                            value = (2 * mCenterSnapMargin - value) / (2 * mCenterSnapMargin - 1);
                        }
                    }
                }
                listener.onProgressChanged(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}
