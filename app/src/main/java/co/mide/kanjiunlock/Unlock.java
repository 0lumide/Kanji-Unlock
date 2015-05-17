package co.mide.kanjiunlock;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import org.xdump.android.zinnia.Zinnia;


public class Unlock extends Activity {
    private boolean isPreview = true;
    public static boolean locked = false;
    private WindowManager winManager = null;
    private RelativeLayout wrapperView = null;

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
        }
        else if(((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)||(keyCode == KeyEvent.KEYCODE_VOLUME_UP))){//&& (!isPreview)){
            returnValue = true;
            Log.v("Volume","Volume pressed");
        }
        else{
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
            WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
            winManager = ((WindowManager)getApplicationContext().getSystemService(WINDOW_SERVICE));
            wrapperView = new RelativeLayout(getBaseContext());
            getWindow().setAttributes(localLayoutParams);
            View.inflate(this, R.layout.activity_unlock, wrapperView);
            winManager.addView(wrapperView, localLayoutParams);
            locked = true;
            findViewById(R.id.previewText).setVisibility(View.INVISIBLE);
        }else{
            isPreview = true;
            locked = false;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            findViewById(R.id.previewText).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
