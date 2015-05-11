package co.mide.kanjiunlock;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class KanjiUnlock extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanji_unlock);
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

    public void broadcast(View v){
        Intent intent = new Intent();
        intent.setAction("co.mide.CUSTOM_INTENT");
        sendBroadcast(intent);
    }

    public void UnlockScreen(View v){
        Intent localIntent = new Intent(this, Unlock.class);
        //localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            localIntent.addFlags(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        startActivity(localIntent);
    }
}
