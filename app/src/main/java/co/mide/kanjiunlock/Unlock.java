package co.mide.kanjiunlock;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.xdump.android.zinnia.Zinnia;


public class Unlock extends Activity {
    private boolean isPreview = true;
    public static boolean locked = false;
    private WindowManager winManager = null;
    private WindowManager winManager1 = null;
    private CustomViewGroup wrapperView = null;
    private RelativeLayout wrapperView1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);
        setupActivity();
        Zinnia zin = new Zinnia();
    }

    @Override
    public void onDestroy(){
        if((winManager != null)&&(wrapperView != null)){
            winManager.removeView(wrapperView);
            wrapperView.removeAllViews();
        }
        if((winManager1 != null)&&(wrapperView1 != null)){
            winManager1.removeView(wrapperView1);
            wrapperView1.removeAllViews();
        }
        locked = false;
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        if(isPreview)
            super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        boolean returnValue;
        //noinspection SimplifiableIfStatement
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (!isPreview)) {
            returnValue = true;
        }else{
            returnValue = super.onKeyDown(keyCode, event);
        }
        return returnValue;
    }

    @Override
    public void onAttachedToWindow() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        super.onAttachedToWindow();
    }

        private void setupActivity(){
        if(getIntent().getBooleanExtra(AppConstants.IS_ACTUALLY_LOCKED, false)){
            isPreview = false;
            WindowManager.LayoutParams localLayoutParams1 = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            winManager1 = ((WindowManager)getApplicationContext().getSystemService(WINDOW_SERVICE));
            wrapperView1 = new RelativeLayout(getBaseContext());
            getWindow().setAttributes(localLayoutParams1);
            View.inflate(this, R.layout.activity_unlock, wrapperView1);
            winManager1.addView(wrapperView1, localLayoutParams1);
            locked = true;
            findViewById(R.id.previewText).setVisibility(View.INVISIBLE);



            winManager = ((WindowManager) getApplicationContext()
                    .getSystemService(Context.WINDOW_SERVICE));

            WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
            localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            localLayoutParams.gravity = Gravity.TOP;
            localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|

                    // this is to enable the notification to recieve touch events
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                    // Draws over status bar
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

            localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            localLayoutParams.height = (int) (50 * getResources()
                    .getDisplayMetrics().scaledDensity);
            localLayoutParams.format = PixelFormat.TRANSPARENT;

            wrapperView = new CustomViewGroup(this);

            winManager.addView(wrapperView, localLayoutParams);


        }else{
            isPreview = true;
            locked = false;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            findViewById(R.id.previewText).setVisibility(View.VISIBLE);
        }
    }

    public void unlockPhone(View v){
        //Unlock Phone here
        unlock();
    }

    private void unlock(){
        locked = false;
        finish();
    }
}
