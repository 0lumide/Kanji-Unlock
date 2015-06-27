package co.mide.kanjiunlock;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Olumide on 6/6/2015.
 */
public class MyVerticalFragment extends Fragment{
    private WindowManager winManager = null;
    private RelativeLayout wrapperView = null;

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
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(AppConstants.PREF_NAME, getActivity().MODE_PRIVATE);
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

//            if(Unlock.locked) {
//                Log.v("canvas", "got here");
//                WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
////                winManager = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE));
////                wrapperView = new RelativeLayout(getActivity());
////                winManager.addView(wrapperView, localLayoutParams);
//                  view = inflater.inflate(R.layout.fragment_write_to_unlock, null);
//                final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                        getResources().getDisplayMetrics().widthPixels,
//                        getResources().getDisplayMetrics().heightPixels,
//                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
//                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
//                        PixelFormat.TRANSLUCENT);
//                viewGroup.addView(view, localLayoutParams);
//
//            }


//            WindowManager.LayoutParams localLayoutParams1 = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//            getActivity().getWindow().setAttributes(localLayoutParams1);
//            view = View.inflate(getActivity(), R.layout.fragment_write_to_unlock, viewGroup);
//            pager.addView(viewGroup, localLayoutParams1);

            MyPagerAdapter pageAdapter = new MyPagerAdapter(getActivity().getSupportFragmentManager(), getFragments());
            pager.setAdapter(pageAdapter);
            return view;
        } else {
            View view = inflater.inflate(R.layout.fragment_pin_unlock, viewGroup, false);

//            if(Unlock.locked) {
//
//
//                view = inflater.inflate(R.layout.fragment_write_to_unlock, null);
//                final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                        getResources().getDisplayMetrics().widthPixels,
//                        getResources().getDisplayMetrics().heightPixels,
//                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
//                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
//                        PixelFormat.TRANSLUCENT);
//
//
//                viewGroup.addView(view, params);
//
////                WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
////                winManager = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE));
////                wrapperView = new RelativeLayout(getActivity());
////                winManager.addView(wrapperView, localLayoutParams);
//            }
//            WindowManager.LayoutParams localLayoutParams1 = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//            ViewPager pager = (ViewPager) view.findViewById(R.id.horizontal_pager);
//            getActivity().getWindow().setAttributes(localLayoutParams1);
//            view = View.inflate(getActivity(), R.layout.fragment_pin_unlock, viewGroup);
//            pager.addView(viewGroup, localLayoutParams1);

            return view;
        }
    }
}
