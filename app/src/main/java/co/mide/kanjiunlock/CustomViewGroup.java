package co.mide.kanjiunlock;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class CustomViewGroup extends ViewGroup {
    private KeyPressedCallback callback;
    public CustomViewGroup(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    public void registerCallback(KeyPressedCallback callback){
        this.callback = callback;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.v("customViewGroup", "**********Intercepted");
        return true;
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent key) {
        if((key.getAction() == KeyEvent.ACTION_DOWN) && (key.getKeyCode() == KeyEvent.KEYCODE_BACK)&&(key.isLongPress())) {
            if (callback != null)
                callback.onBackKeyLongPressed();
            return true;
            //else still gets called once more, but it doesn't matter since canvas would be clear
        }else if((key.getAction() == KeyEvent.ACTION_UP) && (key.getKeyCode() == KeyEvent.KEYCODE_BACK) && !key.isCanceled()){
            if (callback != null)
                callback.onBackKeyPressed();
            return true;
        }
        return false;
    }
}