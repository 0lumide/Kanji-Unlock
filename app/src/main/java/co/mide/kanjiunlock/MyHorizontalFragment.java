package co.mide.kanjiunlock;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caverock.androidsvg.SVGImageView;

/**
 * Created by Olumide on 6/6/2015.
 */
public class MyHorizontalFragment extends Fragment{
    private SVGImageView kanjiBackground;
    private TextView instruction;
    private char character;

    public static MyHorizontalFragment newInstance(int screen){
        Log.v("Horizontal fragment", "new Instance");
        MyHorizontalFragment myHorizontalFragment = new MyHorizontalFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt(AppConstants.FRAGMENT_BUNDLE_INIT_INT, screen);
        myHorizontalFragment.setArguments(bundle);
        return myHorizontalFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle){
        int screen = getArguments().getInt(AppConstants.FRAGMENT_BUNDLE_INIT_INT);
        View view = inflater.inflate(R.layout.fragment_unlock_canvas, viewGroup, false);
        instruction = (TextView)view.findViewById(R.id.draw_instruc);
        kanjiBackground = (SVGImageView)view.findViewById(R.id.kanji_background);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        character = (char)sharedPreferences.getInt(AppConstants.CHAR_PREFIX + screen, 'A');
        instruction.setText(getString(R.string.write_instructions, character));
        kanjiBackground.setImageAsset(JapCharacter.getResourceName(character));
        ((SVGImageView)view.findViewById(R.id.grid_background)).setImageAsset("grid.svg");
        return view;
    }
}
