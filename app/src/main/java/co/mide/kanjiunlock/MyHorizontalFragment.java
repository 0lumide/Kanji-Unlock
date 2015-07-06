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

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import com.caverock.androidsvg.SVGParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;

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

    private String loadAssetTextAsString(Context context, String name) {
        final String TAG = "Asset";
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = context.getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ( (str = in.readLine()) != null ) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error opening asset " + name);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing asset " + name);
                }
            }
        }

        return null;
    }

    public String formatSVGString(String svgFileString){
        String tmp = svgFileString.replace("#000000", "#818181");
        tmp = tmp.replace("#808080", "#000000");
        tmp = tmp.replace("viewBox=\"0 0 109 109\">", String.format("viewBox=\"0 0 109 109\">\n<title>%d</title>", countStrokes(svgFileString)));
        return tmp;
    }

    private int countStrokes(String svgFileString){
        String counter = "<text";
        return (svgFileString.length() - svgFileString.replace(counter, "").length())/counter.length();
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
        String rawSvg = loadAssetTextAsString(getActivity(), JapCharacter.getResourceName(character));
        String modSvg = formatSVGString(rawSvg);
        try {
            SVG svg = SVG.getFromString(modSvg);
            kanjiBackground.setSVG(svg);
        }catch(SVGParseException e){
            Log.v("SVG", "couldn't parse");
            e.printStackTrace();
            kanjiBackground.setImageAsset(JapCharacter.getResourceName(character));
        }
        ((SVGImageView) view.findViewById(R.id.grid_background)).setImageAsset("grid.svg");
        return view;
    }
}
