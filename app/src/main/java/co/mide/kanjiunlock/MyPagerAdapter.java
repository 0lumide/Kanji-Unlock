package co.mide.kanjiunlock;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Olumide on 6/6/2015.
 */
public class MyPagerAdapter extends FragmentPagerAdapter{
    private List<Fragment> fragments;

    public MyPagerAdapter(FragmentManager fm, List<Fragment> fragments){
        super(fm);
        this.fragments = fragments;
    }
    @Override
    public int getCount(){
        return fragments.size();
    }

    @Override
    public Fragment getItem(int index){
        return fragments.get(index);
    }
}
