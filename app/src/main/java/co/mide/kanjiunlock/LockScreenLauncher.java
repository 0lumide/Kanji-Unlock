package co.mide.kanjiunlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;

public class LockScreenLauncher extends BroadcastReceiver {
    //Stuff for lock after time
    private long lockTime;
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isEnabled = context.getSharedPreferences(AppConstants.PREF_NAME, context.MODE_PRIVATE).getBoolean(AppConstants.IS_ENABLED, false);
        if(intent.getAction().equalsIgnoreCase("android.intent.action.SCREEN_OFF")) {
            Log.v("Broadcast", "Screen off broadcast received");
            if(isEnabled)
                launchLockScreen(context);
        }
        else if(intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED")){
            //Start the service if need be
            if(isEnabled) {
                Intent i= new Intent(context, WishIDidntNeedThisService.class);
                context.startService(i);
            }
        }
        else if(intent.getAction().equalsIgnoreCase("android.intent.action.TIME_TICK")
                || intent.getAction().equalsIgnoreCase("android.intent.action.TIME_SET")
                ||intent.getAction().equalsIgnoreCase("android.intent.action.TIMEZONE_CHANGED")){
            Log.v("Broadcast", intent.getAction()+ " received");
            Unlock unlock = Unlock.getUnlock();
            if(unlock != null) {
                unlock.updateTime();
            }
        }
        else if(intent.getAction().equals("co.mide.CLEAN_UP")){
            int o = 0;
        }
    }
    
    private void launchLockScreen(Context context){
        if(!Unlock.locked && (((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getCallState() == TelephonyManager.CALL_STATE_IDLE )) {
            Unlock.locked = true;
            Log.v("LockScreen", "Attempting to launch LockScreen");
            Intent localIntent = new Intent(context, Unlock.class);
            localIntent.putExtra(AppConstants.IS_ACTUALLY_LOCKED, true);
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(localIntent);
        }
    }

    private void updateTime(Context context){

    }
}
