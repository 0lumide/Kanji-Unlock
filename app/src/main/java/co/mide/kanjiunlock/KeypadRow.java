package co.mide.kanjiunlock;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class KeypadRow extends LinearLayout {
    private String keyA, keyB, keyC, key1, key2, key3;

    public KeypadRow(Context context) {
        super(context);
        init(null, 0);
    }

    public KeypadRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public KeypadRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        Context context = getContext();
        // Load attributes
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.KeypadRow, defStyle, 0);

        keyA = a.getString(
                R.styleable.KeypadRow_keyA);
        key1 = a.getString(
                R.styleable.KeypadRow_key1);
        keyB = a.getString(
                R.styleable.KeypadRow_keyB);
        key2 = a.getString(
                R.styleable.KeypadRow_key2);
        keyC = a.getString(
                R.styleable.KeypadRow_keyC);
        key3 = a.getString(
                R.styleable.KeypadRow_key3);
        a.recycle();

        inflate(context, R.layout.view_keypad_row, this);
    }

    @Override
    public void onFinishInflate(){
        super.onFinishInflate();
        TextView tKeyA, tKeyB, tKeyC, tKey1, tKey2, tKey3;
        tKey1 = (TextView) findViewById(R.id.key1);
        tKeyA = (TextView) findViewById(R.id.keyA);
        tKey2 = (TextView) findViewById(R.id.key2);
        tKeyB = (TextView) findViewById(R.id.keyB);
        tKey3 = (TextView) findViewById(R.id.key3);
        tKeyC = (TextView) findViewById(R.id.keyC);

        tKey1.setText(key1);
        tKey2.setText(key2);
        tKey3.setText(key3);
        tKeyA.setText(keyA);
        tKeyB.setText(keyB);
        tKeyC.setText(keyC);
    }
}
