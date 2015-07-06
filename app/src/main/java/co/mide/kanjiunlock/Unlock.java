package co.mide.kanjiunlock;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;
//import org.xdump.android.zinnia.Zinnia;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Unlock extends FragmentActivity {
    private boolean isPreview = false;
    public static boolean locked = false;
    private WindowManager winManager = null;
    private WindowManager winManager1 = null;
    private CustomViewGroup wrapperView = null;
    private RelativeLayout wrapperView1 = null;
    private TextView dateText = null;
    private TextView timeText = null;
    private TextView amPmText = null;
    private DateFormat dateFormat;
    private DateFormat timeFormat;
    private DateFormat amPmFormat;
    private VerticalViewPager pager;
//    private long recognizer;
//    private long zinniaCharacter;
//    private Zinnia zin;
    private MyPagerAdapter pageAdapter;
    private static Unlock unlock;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateFormat = new SimpleDateFormat("EEE, MMM d");
        timeFormat = new SimpleDateFormat("h:mm");
        amPmFormat = new SimpleDateFormat("a");
        dateFormat.setTimeZone(TimeZone.getDefault());
        timeFormat.setTimeZone(TimeZone.getDefault());
        amPmFormat.setTimeZone(TimeZone.getDefault());
        preferences = getSharedPreferences(AppConstants.PREF_NAME, MODE_PRIVATE);
        setupActivity();
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

    private List<Fragment> getFragments(){
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(MyVerticalFragment.newInstance(1));
        fragmentList.add(MyVerticalFragment.newInstance(2));
        return  fragmentList;
    }
    private void zinniaStuff(){
        //        zin = new Zinnia();
//        recognizer = zin.zinnia_recognizer_new();
//        zinniaCharacter = zin.zinnia_character_new();
////        getAssets().open("handwriting-ja.model").
//        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "kanjiUnlock" + File.separatorChar + "handwriting-ja.model");
//        if(!file.exists()) {
//            Toast.makeText(this, "File doesn't Exist", Toast.LENGTH_SHORT).show();
//        } else{
//            Toast.makeText(this, "File Exists", Toast.LENGTH_SHORT).show();
//            zin.zinnia_recognizer_open(recognizer, file.getAbsolutePath());
//            zin.zinnia_character_set_height(zinniaCharacter, 300);
//            zin.zinnia_character_set_width(zinniaCharacter, 300);
//            zin.zinnia_character_add(zinniaCharacter, 0, 51, 29);
//            zin.zinnia_character_add(zinniaCharacter, 0, 117, 41);
//            zin.zinnia_character_add(zinniaCharacter, 1, 99, 65);
//            zin.zinnia_character_add(zinniaCharacter, 1, 219, 77);
//            zin.zinnia_character_add(zinniaCharacter, 2, 27, 131);
//            zin.zinnia_character_add(zinniaCharacter, 2, 261, 131);
//            zin.zinnia_character_add(zinniaCharacter, 3, 129, 17);
//            zin.zinnia_character_add(zinniaCharacter, 3, 57, 203);
//            zin.zinnia_character_add(zinniaCharacter, 4, 111, 71);
//            zin.zinnia_character_add(zinniaCharacter, 4, 219, 173);
//            zin.zinnia_character_add(zinniaCharacter, 5, 81, 161);
//            zin.zinnia_character_add(zinniaCharacter, 5, 93, 281);
//            zin.zinnia_character_add(zinniaCharacter, 6, 99, 167);
//            zin.zinnia_character_add(zinniaCharacter, 6, 207, 167);
//            zin.zinnia_character_add(zinniaCharacter, 6, 189, 245);
//            zin.zinnia_character_add(zinniaCharacter, 7, 99, 227);
//            zin.zinnia_character_add(zinniaCharacter, 7, 189, 227);
//            zin.zinnia_character_add(zinniaCharacter, 8, 111, 257);
//            zin.zinnia_character_add(zinniaCharacter, 8, 189, 245);
//        }
//
//        long result = zin.zinnia_recognizer_classify(recognizer, zinniaCharacter, 10);
//        if (result == 0) {
//            Toast.makeText(this, String.format("%s\n", zin.zinnia_recognizer_strerror(recognizer)), Toast.LENGTH_SHORT).show();
//            Log.v("Zinnia", String.format("%s\n", zin.zinnia_recognizer_strerror(recognizer)));
//        }else {
//            for (int i = 0; i < zin.zinnia_result_size(result); ++i) {
//                Log.v("Zinnia", String.format("%s\t%f\n", zin.zinnia_result_value(result, i), zin.zinnia_result_score(result, i)));
//                Toast.makeText(this, String.format("%s\t%f\n", zin.zinnia_result_value(result, i), zin.zinnia_result_score(result, i)), Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    @Override
    public void onDestroy(){
        if((winManager1 != null)&&(wrapperView1 != null)){
            winManager1.removeView(wrapperView1);
            wrapperView1.removeAllViews();
        }
        if((winManager != null)&&(wrapperView != null)){
            winManager.removeView(wrapperView);
            wrapperView.removeAllViews();
        }
        locked = false;
        try {
//            zin.zinnia_character_destroy(zinniaCharacter);
//            zin.zinnia_recognizer_destroy(recognizer);
        }catch (Exception e){

        }
        Log.v("Unlock", "Unlocked");
        unlock = null;
        super.onDestroy();
    }

    @Override
    public void onStop(){
        if(!isPreview)
            overridePendingTransition(0, 0);
        super.onStop();
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

    @Override
    public void onResume(){
        super.onResume();
        if(!isPreview)
            overridePendingTransition(0, 0);
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
        View view = null;
        if(getIntent().getBooleanExtra(AppConstants.IS_ACTUALLY_LOCKED, false)){
            isPreview = false;
            WindowManager.LayoutParams localLayoutParams1 = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            winManager1 = ((WindowManager)getApplicationContext().getSystemService(WINDOW_SERVICE));
            wrapperView1 = new RelativeLayout(getBaseContext());
            getWindow().setAttributes(localLayoutParams1);
            winManager1.addView(wrapperView1, localLayoutParams1);
            view = View.inflate(this, R.layout.activity_unlock, wrapperView1);
            locked = true;

            RelativeLayout unlockLayout = (RelativeLayout)view.findViewById(R.id.unlock_layout);
            dateText = (TextView)view.findViewById(R.id.date);
            timeText = (TextView)view.findViewById(R.id.time);
            amPmText = (TextView)view.findViewById(R.id.am_pm);
            getSupportFragmentManager().setViewGroup(unlockLayout);

            view.findViewById(R.id.previewText).setVisibility(View.INVISIBLE);
            pageAdapter = new MyPagerAdapter(getSupportFragmentManager(), getFragments());
            pager = (VerticalViewPager)view.findViewById(R.id.vertical_pager);
            pager.setAdapter(pageAdapter);

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
            Log.v("Unlock", "Locked");

        }else{
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

    public void unlockPhone(View v){
        //Unlock Phone here
        unlock();
    }

    private void unlock() {
        locked = false;
        finish();
        if (!isPreview)
            overridePendingTransition(0, 0);
    }
}
