package co.mide.kanjiunlock;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
//import android.support.v4.view.VerticalViewPager;
//import android.support.v4.view.VerticalViewPager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;
//import org.xdump.android.zinnia.Zinnia;

import java.util.ArrayList;
import java.util.List;

public class Unlock extends FragmentActivity {
    private boolean isPreview = false;
    public static boolean locked = false;
    private WindowManager winManager = null;
    private WindowManager winManager1 = null;
    private CustomViewGroup wrapperView = null;
    private RelativeLayout wrapperView1 = null;
//    private long recognizer;
//    private long zinniaCharacter;
//    private Zinnia zin;
    private MyPagerAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_unlock);
//        ((FragmentManagerImpl)getSupportFragmentManager()).set;
        setupActivity();
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
        if((winManager != null)&&(wrapperView != null)){
            winManager.removeView(wrapperView);
            wrapperView.removeAllViews();
        }
        if((winManager1 != null)&&(wrapperView1 != null)){
            winManager1.removeView(wrapperView1);
            wrapperView1.removeAllViews();
        }
        locked = false;
        try {
//            zin.zinnia_character_destroy(zinniaCharacter);
//            zin.zinnia_recognizer_destroy(recognizer);
        }catch (Exception e){

        }
        super.onDestroy();
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.v("On stop", "stopping");
//        finish();
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

    private void setupActivity(){
        if(getIntent().getBooleanExtra(AppConstants.IS_ACTUALLY_LOCKED, false)){
            isPreview = false;
            WindowManager.LayoutParams localLayoutParams1 = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            winManager1 = ((WindowManager)getApplicationContext().getSystemService(WINDOW_SERVICE));
            wrapperView1 = new RelativeLayout(getBaseContext());
            getWindow().setAttributes(localLayoutParams1);
//            winManager1.addView(unlockLayout, localLayoutParams1);
//            VerticalViewPager pager = (VerticalViewPager)findViewById(R.id.vertical_pager);
            winManager1.addView(wrapperView1, localLayoutParams1);
            View view = View.inflate(this, R.layout.activity_unlock, wrapperView1);
            locked = true;

            RelativeLayout unlockLayout = (RelativeLayout)view.findViewById(R.id.unlock_layout);
            getSupportFragmentManager().setViewGroup(unlockLayout);

            view.findViewById(R.id.previewText).setVisibility(View.INVISIBLE);
            pageAdapter = new MyPagerAdapter(getSupportFragmentManager(), getFragments());
            VerticalViewPager pager = (VerticalViewPager)view.findViewById(R.id.vertical_pager);
            pager.setAdapter(pageAdapter);

//
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


        }else{
            setContentView(R.layout.activity_unlock);
            pageAdapter = new MyPagerAdapter(getSupportFragmentManager(), getFragments());
            VerticalViewPager pager = (VerticalViewPager)findViewById(R.id.vertical_pager);
            Log.v("pager", pager.getId()+"");
            pager.setAdapter(pageAdapter);

            isPreview = true;
            locked = false;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            findViewById(R.id.previewText).setVisibility(View.VISIBLE);
        }
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
