package co.mide.kanjiunlock;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Olumide on 6/6/2015.
 */
public class MyVerticalFragment extends Fragment{
    private int[] keys;
    private ViewPager pager;
    private MyPagerAdapter pageAdapter;

    public static MyVerticalFragment newInstance(int screen){
        Log.v("Vertical fragment", "new Instance");
        MyVerticalFragment myVerticalFragment = new MyVerticalFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt(AppConstants.FRAGMENT_BUNDLE_INIT_INT, screen);
        myVerticalFragment.setArguments(bundle);
        return myVerticalFragment;
    }

    public void onBackPressed(){
        if((pager != null)&&(pageAdapter!=null)) {
            ((MyHorizontalFragment)pageAdapter.getItem(pager.getCurrentItem())).onBackPressed();
        }
    }

    public void onBackLongPressed(){
        if((pager != null)&&(pageAdapter!=null)) {
            ((MyHorizontalFragment)pageAdapter.getItem(pager.getCurrentItem())).onBackLongPressed();
        }
    }

    private List<Fragment> getFragments(){
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(AppConstants.PREF_NAME, Activity.MODE_PRIVATE);
        int count = sharedPreferences.getInt(AppConstants.CHAR_COUNT, 1);
        if(sharedPreferences.getBoolean(AppConstants.CHAR_CHOSEN, true) && (count > 0)) {
            //starts rom 1 so that something else can occupy 0 when I'm ready
            for (int i = 1; i <= count; i++) {
                fragmentList.add(MyHorizontalFragment.newInstance(i));
            }
        }
        return  fragmentList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        int screen = getArguments().getInt(AppConstants.FRAGMENT_BUNDLE_INIT_INT, 1);
        keys = new int[]{R.id.but1, R.id.but2, R.id.but3, R.id.but4,
                R.id.but5, R.id.but6, R.id.but7, R.id.but8,
                R.id.but9, R.id.but10, R.id.but11, R.id.but12, R.id.but_back};
        if (screen == 1) {
            View view = inflater.inflate(R.layout.fragment_write_to_unlock, viewGroup, false);
            pager = (ViewPager) view.findViewById(R.id.horizontal_pager);
            pageAdapter = new MyPagerAdapter(getActivity().getSupportFragmentManager(), getFragments());
            LinePageIndicator indicator = (LinePageIndicator)view.findViewById(R.id.indicator);
            pager.setAdapter(pageAdapter);
            indicator.setViewPager(pager);
            return view;
        } else {
            final View view = inflater.inflate(R.layout.fragment_pin_unlock, viewGroup, false);
            final EditText pinEditText = (EditText) view.findViewById(R.id.pinEnterText);
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(pinEditText.getWindowToken(), 0);
            pinEditText.requestFocus();
            pinEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                }
            });
            pinEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            pinEditText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            // Disable standard keyboard hard way
            pinEditText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    EditText editText = (EditText) v;
                    editText.setTextIsSelectable(true);//This stops the IME from showing up
                    editText.onTouchEvent(event);               // Call native handler
                    return true;
                }
            });



            final PasswordTransformationMethod transformationMethod = (PasswordTransformationMethod)pinEditText.getTransformationMethod();
            for(int i = 0; i < keys.length; i++){
                view.findViewById(keys[i]).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView message = (TextView)view.findViewById(R.id.pin_message);
                        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                        for (int f = 0; f < keys.length; f++) {
                            if (v.getId() == keys[f]) {
                                message.setText("");
                                if(isDigit(v.getId())){
                                    int cursorLocation = pinEditText.getSelectionStart();
                                    Log.v("cursor Location", cursorLocation + "");
                                    String newText;
                                    String key = getKey(v.getId());
                                    String oldText = pinEditText.getEditableText().toString();
                                    if (oldText.length() == 4) {
                                        newText = (new StringBuilder(oldText)
                                                .insert(cursorLocation, key)
                                                .toString()).substring(0, 4);
                                    } else {
                                        newText = pinEditText.getEditableText()
                                                .insert(cursorLocation, key).toString();
                                    }
                                    pinEditText.setText(newText);
                                    Log.v("newText", newText);
                                    if (cursorLocation < 4) {
                                        pinEditText.setSelection(cursorLocation + 1);
                                        transformationMethod.onTextChanged(pinEditText.getText(), cursorLocation, 0, 1);
                                    } else {
                                        pinEditText.setSelection(4);
                                    }
                                    Log.v("Key", "Pin pressed");
                                }else if(isOkayButton(v.getId())){
                                    if(!((Unlock)getActivity()).verifyPin(pinEditText.getText().toString()))
                                        message.setText(R.string.pin_try_again_instruction);
                                    pinEditText.setText("");
                                }else if(isBackSpace(v.getId())){
                                    Log.v("oldText", pinEditText.getText().toString());
                                    int start = pinEditText.getSelectionStart();
                                    int end = pinEditText.getSelectionEnd();
                                    if((start == end) && (start > 0)) {
                                        pinEditText.setText(pinEditText.getEditableText().delete(start - 1, end));
                                        pinEditText.setSelection(start - 1);
                                    }else if(start != end){
                                        pinEditText.setText(pinEditText.getEditableText().delete(start, end));
                                        pinEditText.setSelection(start);
                                    }
                                    Log.v("newText", pinEditText.getText().toString());
                                }
                                break;
                            }
                        }
                    }
                });
            }
            return view;
        }
    }

    private String getKey(int id){
        switch(id) {
            case R.id.but1: return "1";
            case R.id.but2: return "2";
            case R.id.but3: return "3";
            case R.id.but4: return "4";
            case R.id.but5: return "5";
            case R.id.but6: return "6";
            case R.id.but7: return "7";
            case R.id.but8: return "8";
            case R.id.but9: return "9";
            case R.id.but11: return "0";
        }
        return "-1";
    }

    private boolean isOkayButton(int id){
        return id == R.id.but12;
    }

    private boolean isBackSpace(int id){
        return id == R.id.but_back;
    }

    private boolean isDigit(int id){
        return !getKey(id).equals("-1");
    }
}
