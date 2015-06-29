package co.mide.kanjiunlock;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Olumide on 6/6/2015.
 */
public class MyVerticalFragment extends Fragment{

    public static MyVerticalFragment newInstance(int screen){
        Log.v("Vertical fragment", "new Instance");
        MyVerticalFragment myVerticalFragment = new MyVerticalFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt(AppConstants.FRAGMENT_BUNDLE_INIT_INT, screen);
        myVerticalFragment.setArguments(bundle);
        return myVerticalFragment;
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
        if (screen == 1) {
            View view = inflater.inflate(R.layout.fragment_write_to_unlock, viewGroup, false);
            ViewPager pager = (ViewPager) view.findViewById(R.id.horizontal_pager);
            MyPagerAdapter pageAdapter = new MyPagerAdapter(getActivity().getSupportFragmentManager(), getFragments());
            pager.setAdapter(pageAdapter);
            return view;
        } else {
            return inflater.inflate(R.layout.fragment_pin_unlock, viewGroup, false);
        }
    }
}
