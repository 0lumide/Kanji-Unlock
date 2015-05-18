package co.mide.kanjiunlock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;


public class KanjiUnlock extends AppCompatActivity {
    private Switch disableSwitch;
    private SharedPreferences sharedPreferences;
    private Switch enableSwitch;
    private Switch backupSwitch;
    private GridView gridview;
    private ImageAdapter adapter;// = new ImageAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanji_unlock);
        enableSwitch = (Switch) findViewById(R.id.enableSwitch);
        backupSwitch = (Switch) findViewById(R.id.backupSwitch);
        disableSwitch = (Switch) findViewById(R.id.disableSwitch);
        gridview = (GridView) findViewById(R.id.gridView);
        adapter = new ImageAdapter(this);
        sharedPreferences = getSharedPreferences(AppConstants.PREF_NAME, MODE_PRIVATE);
        setupSwitches();
        setupCharacters();
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
        if (id == R.id.action_preview) {
            previewUnlockScreen();
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

    private void previewUnlockScreen(){
       Intent localIntent = new Intent(this, Unlock.class);
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.addFlags(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        startActivity(localIntent);
    }

    public void previewLockScreen(View v) {
        previewUnlockScreen();
    }
    private void setupCharacters(){
        gridview.setAdapter(adapter);

        gridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridview.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                final int checkedCount = gridview.getCheckedItemCount();
                // Set the CAB title according to total checked items
                if(checkedCount == 0)
                    return;
                mode.setTitle(checkedCount + " " + getResources().getString(R.string.selected_text));
                if(checkedCount > 1){
                    mode.getMenu().removeItem(R.id.edit_char);
                    mode.invalidate();
                }
                else{
                    mode.getMenu().clear();
                    mode.getMenuInflater().inflate(R.menu.context_menu, mode.getMenu());
                    mode.invalidate();
                }
                if(checked) {
                    adapter.mark(position);
                    Log.v("check", "Position " + position);
                    if(gridview.getChildAt(position) == null)
                        gridview.getChildAt(position % gridview.getNumColumns()).setAlpha(0.4f);
                    else
                        gridview.getChildAt(position).setAlpha(0.4f);
                }else {
                    if(gridview.getChildAt(position) == null)
                        gridview.getChildAt(position % gridview.getNumColumns()).setAlpha(1.0f);
                    else
                        gridview.getChildAt(position).setAlpha(1.0f);
                    adapter.unMark(position);
                }
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.delete_char:
                        //TODO delete
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    case R.id.edit_char:
                        //TODO edit
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context_menu, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                for(int i = 0; i < adapter.getCount(); i++) {
                    if(gridview.getChildAt(i) != null)
                        gridview.getChildAt(i).setAlpha(1.0f);
                    adapter.unMark(i);
                }
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                return false;
            }
        });
//        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v,
//                                    int position, long id) {
//                Toast.makeText(getApplicationContext(), "" + position,
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
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

        try{
            Class<?> lockUtilsClass = Class.forName(LOCKSCREEN_UTILS);
            // "this" is a Context, in my case an Activity
            Object lockUtils = lockUtilsClass.getConstructor(Context.class).newInstance(getApplicationContext());

            Method method = lockUtilsClass.getMethod("isLockScreenDisabled");

            return Boolean.valueOf(String.valueOf(method.invoke(lockUtils)));
        }
        catch (Exception e)
        {
            Log.e("reflectInternalUtils", "ex:"+e);
        }

        return false;
    }
}
