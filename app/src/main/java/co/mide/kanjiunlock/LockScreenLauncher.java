package co.mide.kanjiunlock;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class LockScreenLauncher extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Intent Detected.", Toast.LENGTH_LONG).show();
        if(intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
            Log.v("Broad", "Broadcast Received");
            Intent localIntent = new Intent(context, Unlock.class);
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            localIntent.addFlags(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
            context.startActivity(localIntent);
        }
    }
}
