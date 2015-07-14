package co.mide.kanjiunlock;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;

public class LockScreenLauncher extends BroadcastReceiver {
    //Stuff for lock after time
    private Handler handler;
    private boolean notCancelled = true;
    private final int THREE_SECONDS = 3000;
    @Override
    public void onReceive(final Context context, Intent intent) {
        boolean isEnabled = context.getSharedPreferences(AppConstants.PREF_NAME, context.MODE_PRIVATE).getBoolean(AppConstants.IS_ENABLED, false);
        if(intent.getAction().equalsIgnoreCase(Intent.ACTION_SCREEN_OFF)) {
            Log.v("Broadcast", "Screen off broadcast received");
            if (isEnabled) {
                notCancelled = true;
                //launch lockscreen after 3 seconds
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                        if (notCancelled) {
                            launchLockScreen(context);
                        }
                    }
                }, THREE_SECONDS);
            }
        } else if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SCREEN_ON)) {
            Log.v("Broadcast", "Screen on broadcast received");
            if(isEnabled && (Unlock.getUnlock() == null)) {
                handler.removeCallbacks(null);
                notCancelled = false;
            }
        }
        else if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)){
            //Start the service if need be
            if(isEnabled) {
                Intent i= new Intent(context, WishIDidntNeedThisService.class);
                context.startService(i);
            }
        }
        else if(intent.getAction().equalsIgnoreCase(Intent.ACTION_TIME_TICK)
                //not sure about time set
                || intent.getAction().equalsIgnoreCase("android.intent.action.TIME_SET")
                ||intent.getAction().equalsIgnoreCase(Intent.ACTION_TIMEZONE_CHANGED)){
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

    public static boolean isModeInCall(Context context){
        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        return(manager.getMode()!=AudioManager.MODE_NORMAL);
    }

    private static boolean isCallIdle(Context context){
        return (((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getCallState() == TelephonyManager.CALL_STATE_IDLE );
    }
    private void launchLockScreen(Context context){
        if(!Unlock.locked && !isModeInCall(context) && isCallIdle(context)) {
            Unlock.locked = true;
            Log.v("LockScreen", "Attempting to launch LockScreen");
            Intent localIntent = new Intent(context, Unlock.class);
            localIntent.putExtra(AppConstants.IS_ACTUALLY_LOCKED, true);
            localIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(localIntent);
        }
    }
}
