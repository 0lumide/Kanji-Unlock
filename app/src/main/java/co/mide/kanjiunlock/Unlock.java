package co.mide.kanjiunlock;

import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xdump.android.zinnia.ModelDoesNotExistException;
import org.xdump.android.zinnia.Zinnia;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Unlock extends FragmentActivity implements KeyPressedCallback{
    private boolean isPreview = false;
    public static boolean locked = false;
    private CustomViewGroup wrapperView = null;
    private RelativeLayout wrapperView1 = null;
    private TextView dateText = null;
    private TextView timeText = null;
    private TextView amPmText = null;
    private DateFormat dateFormat;
    private DateFormat timeFormat;
    private DateFormat amPmFormat;
    private VerticalViewPager pager;
    private long recognizer;
    private Zinnia zin;
    private MyPagerAdapter pageAdapter;
    private static Unlock unlock;
    private SharedPreferences preferences;
    private int origTimeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        origTimeout = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, -1);
        Log.d("Timeout", origTimeout + "");
        setContentView(R.layout.activity_unlock);
        dateFormat = new SimpleDateFormat("EEE, MMM d");
        timeFormat = new SimpleDateFormat("h:mm");
        amPmFormat = new SimpleDateFormat("a");
        dateFormat.setTimeZone(TimeZone.getDefault());
        timeFormat.setTimeZone(TimeZone.getDefault());
        amPmFormat.setTimeZone(TimeZone.getDefault());
        preferences = getSharedPreferences(AppConstants.PREF_NAME, MODE_PRIVATE);
        setupActivity();
        zin = new Zinnia(this);
        try {
            recognizer = zin.zinnia_recognizer_new("handwriting-ja.model");
        }catch (ModelDoesNotExistException e){
            Log.e("Zinnia model", "Model does not exist");
        }
        unlock = this;
    }

    public static Unlock getUnlock(){
        return unlock;
    }

    public void updateTime(){
        Date date = new Date(System.currentTimeMillis());
        if(timeText != null){
            //update timeText
            timeText.setText(timeFormat.format(date));
        }
        if(dateText != null){
            //update dateText regardless
            dateText.setText(dateFormat.format(date));
        }
        if(amPmText != null){
            //update am/pm if need be
            amPmText.setText(amPmFormat.format(date));
        }
    }

    public void addStroke(long character, int strokeNum, int x, int y){
        zin.zinnia_character_add(character, strokeNum, x, y);
    }

    private void setCharacterSize(long character, int width, int height){
        zin.zinnia_character_set_width(character, width);
        zin.zinnia_character_set_height(character, height);
    }

    private List<Fragment> getFragments(){
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(MyVerticalFragment.newInstance(1));
        fragmentList.add(MyVerticalFragment.newInstance(2));
        return  fragmentList;
    }

    public boolean verifyCharacter(char character, long zinniaCharacter){
        boolean returnValue = false;
        long result = zin.zinnia_recognizer_classify(recognizer, zinniaCharacter, 10);
        if (result == 0) {
            Log.e("Zinnia", String.format("%s", zin.zinnia_recognizer_strerror(recognizer)));
        }else {
            for (int i = 0; i < zin.zinnia_result_size(result); i++) {
                Log.v("Zinnia", String.format("%s\t%f\n", zin.zinnia_result_value(result, i), zin.zinnia_result_score(result, i)));
                if(zin.zinnia_result_value(result, i).equals(Character.toString(character))){
                    returnValue = true;
                    unlock();
                }else if(JapCharacter.isKana(character)){//This is because zinnia doesn't play well with ten ten
                    if(JapCharacter.isVoiced(character)){
                        char voicelessCharacter = JapCharacter.getVoiceless(character);
                        if(zin.zinnia_result_value(result, i).equals(Character.toString(voicelessCharacter))){
                            returnValue = true;
                            unlock();
                        }
                    }
                }
            }
        }
        return returnValue;
    }

    public long createCharacter(int width, int height){
        long character = zin.zinnia_character_new();
        setCharacterSize(character, width, height);
        return character;
    }

    @Override
    public void onDestroy(){
        WindowManager winManager = ((WindowManager)getApplicationContext().getSystemService(WINDOW_SERVICE));
        if(winManager != null) {
            if (wrapperView1 != null) {
                winManager.removeView(wrapperView1);
                wrapperView1.removeAllViews();
            } else {
                Log.d("Destroy", "wrapperView1 is null");
            }
            if (wrapperView != null) {
                winManager.removeView(wrapperView);
                wrapperView.removeAllViews();
            } else {
                Log.d("Destroy", "wrapperView is null");
            }
        }
        try {
            zin.zinnia_recognizer_destroy(recognizer);
        }catch (Exception e){
            //do nothing
        }
        Log.v("Unlock", "Unlocked");
        unlock = null;
        locked = false;
        super.onDestroy();
    }

    @Override
    public void onPause(){
        super.onPause();
        if (origTimeout != -1) {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, origTimeout);
            Log.v("timeout", "Timeout set to: orig timeout: " + origTimeout);
        }
    }

    @Override
    public void onStop(){
        if(!isPreview)
            overridePendingTransition(0, 0);
        super.onStop();
    }

    public void onBackKeyPressed(){
        Log.d("Back", "back pressed");
        if(!isPreview && (pager.getCurrentItem() == 0))
            ((MyVerticalFragment)pageAdapter.getItem(0)).onBackPressed();
    }

    public void onBackKeyLongPressed(){
        Log.d("Back", "back long pressed");
        if(!isPreview && (pager.getCurrentItem() == 0))
            ((MyVerticalFragment)pageAdapter.getItem(0)).onBackLongPressed();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(locked)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!isPreview) {
            overridePendingTransition(0, 0);
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, AppConstants.FIVE_SECONDS);
            Log.v("timeout", "timeout set to Fiveseconds");
        }
        if(isPreview)
            locked = false;
    }

    public boolean verifyPin(String pinString){
        int pin = preferences.getInt(AppConstants.PIN, 0);
        if(Integer.parseInt(pinString) == pin)
            unlock();
        return Integer.parseInt(pinString) == pin;
    }

    private void setupActivity(){
        View view;
        if(getIntent().getBooleanExtra(AppConstants.IS_ACTUALLY_LOCKED, false) && !locked){
            //just to still be able to pick up onback pressed
            isPreview = false;
            locked = true;
            getWindow().setType(2004);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            WindowManager.LayoutParams localLayoutParams1 = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            WindowManager winManager = ((WindowManager)getApplicationContext().getSystemService(WINDOW_SERVICE));
            wrapperView1 = new RelativeLayout(getBaseContext());
            getWindow().setAttributes(localLayoutParams1);
            winManager.addView(wrapperView1, localLayoutParams1);
            view = View.inflate(this, R.layout.activity_unlock, wrapperView1);

            RelativeLayout unlockLayout = (RelativeLayout)view.findViewById(R.id.unlock_layout);
            dateText = (TextView)view.findViewById(R.id.date);
            timeText = (TextView)view.findViewById(R.id.time);
            amPmText = (TextView)view.findViewById(R.id.am_pm);
            getSupportFragmentManager().setViewGroup(unlockLayout);

            view.findViewById(R.id.previewText).setVisibility(View.INVISIBLE);
            pageAdapter = new MyPagerAdapter(getSupportFragmentManager(), getFragments());
            pager = (VerticalViewPager)view.findViewById(R.id.vertical_pager);
            pager.setAdapter(pageAdapter);


            WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
            localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            localLayoutParams.gravity = Gravity.TOP;
            localLayoutParams.flags = //WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|

                    // this is to enable the notification to recieve touch events
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                    // Draws over status bar
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

            localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            localLayoutParams.height = (int) (50 * getResources()
                    .getDisplayMetrics().scaledDensity);
            localLayoutParams.format = PixelFormat.TRANSPARENT;

            wrapperView = new CustomViewGroup(this);
            wrapperView.registerCallback(this);

            winManager.addView(wrapperView, localLayoutParams);
            wrapperView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    Log.d("Back", "item: " + pager.getCurrentItem());
                    if ((keyCode == KeyEvent.KEYCODE_BACK) && (pager.getCurrentItem() == 0)) {
                        ((MyVerticalFragment) pageAdapter.getItem(0)).onBackPressed();
                        return true;
                    }
                    return false;
                }
            });
            Log.v("Unlock", "Locked");

        }else if(!getIntent().getBooleanExtra(AppConstants.IS_ACTUALLY_LOCKED, false)){
            setContentView(R.layout.activity_unlock);
            pageAdapter = new MyPagerAdapter(getSupportFragmentManager(), getFragments());
            pager = (VerticalViewPager)findViewById(R.id.vertical_pager);
            Log.v("pager", pager.getId() + "");
            pager.setAdapter(pageAdapter);
            isPreview = true;
            locked = false;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            findViewById(R.id.previewText).setVisibility(View.VISIBLE);
            dateText = (TextView)findViewById(R.id.date);
            timeText = (TextView)findViewById(R.id.time);
            amPmText = (TextView)findViewById(R.id.am_pm);
        }
        updateTime();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        pager.setPageTransformer(true, new MyPageTransformer());
    }

//    public void unlockPhone(View v){
//        //Unlock Phone here
//        if(v.getId() == R.id.unlock_button)
//            unlock();
//    }

    private void unlock() {
        Log.d("Unlock", "Unlock function");
        finish();
        if (!isPreview)
            overridePendingTransition(0, 0);
        locked = false;
    }
}
