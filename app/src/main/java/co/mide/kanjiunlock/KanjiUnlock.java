package co.mide.kanjiunlock;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;


public class KanjiUnlock extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private Switch enableSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanji_unlock);
        enableSwitch = (Switch) findViewById(R.id.enableSwitch);
        sharedPreferences = getSharedPreferences(AppConstants.PREF_NAME, MODE_PRIVATE);
        setupSwitch();
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
        setupSwitch();
    }

    public void UnlockScreen(View v){
       Intent localIntent = new Intent(this, Unlock.class);
        //localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            localIntent.addFlags(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        startActivity(localIntent);
    }


    private void setupSwitch(){
        enableSwitch.setChecked(sharedPreferences.getBoolean(AppConstants.IS_ENABLED,false));
        enableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isEnabled) {
                if(!isEnabled){
                    Toast.makeText(getApplicationContext(), "LockScreen has been disabled", Toast.LENGTH_SHORT).show();
                    Intent intent= new Intent(getApplicationContext(), WishIDidntNeedThisService.class);
                    stopService(intent);
                    sharedPreferences.edit().putBoolean(AppConstants.IS_ENABLED, isEnabled).commit();
                }
                else{
//                    Toast.makeText(getApplicationContext(), "LockScreen has been enabled", Toast.LENGTH_SHORT).show();
//                    Intent intent= new Intent(getApplicationContext(), WishIDidntNeedThisService.class);
//                    startService(intent);
                    Intent intent = new Intent(getApplicationContext(), SelectPin.class);
                    startActivity(intent);
                }
            }
        });
    }
}
