package co.mide.kanjiunlock;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class LockScreenLauncher extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isEnabled = context.getSharedPreferences(AppConstants.PREF_NAME, context.MODE_PRIVATE).getBoolean(AppConstants.IS_ENABLED, false);
        if(intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
            Log.v("Broadcast", "Screen off broadcast received");
            if(isEnabled)
                launchLockScreen(context);
        }
        else if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            //Start the service if need be
            if(isEnabled) {
                Intent i= new Intent(context, WishIDidntNeedThisService.class);
                context.startService(i);
            }
        }
        else if(intent.getAction().equals("co.mide.CLEAN_UP")){
            int o = 0;
        }
    }
    
    private void launchLockScreen(Context context){
        Log.v("LockScreen", "Attempting to launch LockScreen");
        Intent localIntent = new Intent(context, Unlock.class);
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            localIntent.addFlags(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        context.startActivity(localIntent);
    }
}
