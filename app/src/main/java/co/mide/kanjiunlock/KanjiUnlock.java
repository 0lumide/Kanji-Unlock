package co.mide.kanjiunlock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.lang.reflect.Method;


public class KanjiUnlock extends AppCompatActivity {
    private Switch disableSwitch;
    private SharedPreferences sharedPreferences;
    private Switch enableSwitch;
    private Switch backupSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanji_unlock);
        enableSwitch = (Switch) findViewById(R.id.enableSwitch);
        backupSwitch = (Switch) findViewById(R.id.backupSwitch);
        disableSwitch = (Switch) findViewById(R.id.disableSwitch);
        sharedPreferences = getSharedPreferences(AppConstants.PREF_NAME, MODE_PRIVATE);
        setupSwitches();
        Intent i= new Intent(this, WishIDidntNeedThisService.class);
        startService(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kanji_unlock, menu);
        return true;
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

    @Override
    public void onResume(){
        super.onResume();
        //Disable listeners first to avoid double calling
        enableSwitch.setOnCheckedChangeListener(null);
        backupSwitch.setOnCheckedChangeListener(null);
        disableSwitch.setOnCheckedChangeListener(null);
        setupSwitches();
    }

    public void UnlockScreen(View v){
       Intent localIntent = new Intent(this, Unlock.class);
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.addFlags(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        startActivity(localIntent);
    }


    private void setupSwitches(){
        enableSwitch.setChecked(sharedPreferences.getBoolean(AppConstants.IS_ENABLED, false));
        backupSwitch.setChecked(sharedPreferences.getBoolean(AppConstants.PIN_SET, false));
        disableSwitch.setChecked(!isLockScreenDisabled());

        enableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isEnabled) {
                if (!isEnabled) {
                    Toast.makeText(getApplicationContext(), "LockScreen has been disabled", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), WishIDidntNeedThisService.class);
                    stopService(intent);
                    sharedPreferences.edit().putBoolean(AppConstants.IS_ENABLED, isEnabled).apply();
                } else {
                    Toast.makeText(getApplicationContext(), "LockScreen has been enabled", Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().putBoolean(AppConstants.IS_ENABLED, isEnabled).apply();
                    Intent intent = new Intent(getApplicationContext(), WishIDidntNeedThisService.class);
                    startService(intent);
                }
            }
        });
        backupSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isEnabled) {
                if (!isEnabled) {
                    Intent intent = new Intent(getApplicationContext(), SelectPin.class);
                    intent.putExtra(AppConstants.CLEAR_PIN, true);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), SelectPin.class);
                    startActivity(intent);
                }
            }
        });
        disableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isEnabled) {
                Intent intent = new Intent("android.app.action.SET_NEW_PASSWORD");
                startActivity(intent);
                Log.v("disable", "disable toggled");
            }
        });
    }

    //From stack overflow
    //http://stackoverflow.com/a/25715384/2057884
    private boolean isLockScreenDisabled(){
        String LOCKSCREEN_UTILS = "com.android.internal.widget.LockPatternUtils";

        try
        {
            Class<?> lockUtilsClass = Class.forName(LOCKSCREEN_UTILS);
            // "this" is a Context, in my case an Activity
            Object lockUtils = lockUtilsClass.getConstructor(Context.class).newInstance(getApplicationContext());

            Method method = lockUtilsClass.getMethod("isLockScreenDisabled");

            boolean isDisabled = Boolean.valueOf(String.valueOf(method.invoke(lockUtils)));

            return isDisabled;
        }
        catch (Exception e)
        {
            Log.e("reflectInternalUtils", "ex:"+e);
        }

        return false;
    }
}
