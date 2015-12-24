package co.mide.kanjiunlock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import com.caverock.androidsvg.SVGParseException;


import org.xdump.android.zinnia.Zinnia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MyHorizontalFragment extends Fragment implements DrawCanvas.StrokeCallback {
    private char character;
    private int strokeNum;
    private DrawCanvas canvas;
    private long zinniaCharacter = -1;
    private TextView progressReport;
    private int lastStroke = -1;

    public static MyHorizontalFragment newInstance(int screen){
        Log.v("Horizontal fragment", "new Instance");
        MyHorizontalFragment myHorizontalFragment = new MyHorizontalFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt(AppConstants.FRAGMENT_BUNDLE_INIT_INT, screen);
        myHorizontalFragment.setArguments(bundle);
        return myHorizontalFragment;
    }

    public void onStrokeCountChange(int strokeCount){
        formatProgressReport(strokeCount);
        if(zinniaCharacter == -1)
            //create and initialize character
            zinniaCharacter = ((Unlock)getActivity()).createCharacter(canvas.getWidth(), canvas.getHeight());
        if(strokeCount > 0) {
            DrawCanvas.Stroke stroke = canvas.getStroke(strokeCount - 1);
            for (int i = 0; i < stroke.getSize(); i++) {
                ((Unlock) getActivity()).addStroke(zinniaCharacter, strokeCount - 1, stroke.getX(i), stroke.getY(i));
            }
        }
        if(strokeCount <= lastStroke){
            //rebuild character
            if(zinniaCharacter != -1)
                Zinnia.zinnia_character_destroy(zinniaCharacter);
            zinniaCharacter = ((Unlock)getActivity()).createCharacter(canvas.getWidth(), canvas.getHeight());
            for(int i = 0; i < strokeCount; i++) {
                DrawCanvas.Stroke stroke = canvas.getStroke(i);
                for (int j = 0; j < stroke.getSize(); j++) {
                    ((Unlock) getActivity()).addStroke(zinniaCharacter, strokeCount - 1, stroke.getX(j), stroke.getY(j));
                }
            }
        }else if(strokeCount >= strokeNum){
            if(!((Unlock)getActivity()).verifyCharacter(character, zinniaCharacter)) {
                resetCanvas();
                progressReport.setText(R.string.write_try_again_instruction);
            }
        }
        lastStroke = strokeCount;
    }

    private void resetCanvas(){
        if(zinniaCharacter != -1)
            Zinnia.zinnia_character_destroy(zinniaCharacter);
        zinniaCharacter = ((Unlock)getActivity()).createCharacter(canvas.getWidth(), canvas.getHeight());
        canvas.resetCanvas();
        formatProgressReport(0);
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

    private void formatProgressReport(int strokeCount){
        progressReport.setText(getResources().getString(R.string.stroke_progress, strokeCount+"", strokeNum+""));
    }

    private int countStrokes(String svgFileString){
        String counter = "<text";
        return (svgFileString.length() - svgFileString.replace(counter, "").length())/counter.length();
    }

    public void onBackPressed(){
        canvas.undoStroke();
    }

    public void onBackLongPressed(){
        resetCanvas();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle){
        int screen = getArguments().getInt(AppConstants.FRAGMENT_BUNDLE_INIT_INT);
        View view = inflater.inflate(R.layout.fragment_unlock_canvas, viewGroup, false);
        TextView instruction = (TextView)view.findViewById(R.id.draw_instruc);
        SVGImageView kanjiBackground = (SVGImageView)view.findViewById(R.id.kanji_background);
        canvas = (DrawCanvas)view.findViewById(R.id.canvas);
        canvas.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.v("LongClick","LongClick");
                return true;
            }
        });
        progressReport = (TextView)view.findViewById(R.id.progress_report);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        character = (char)sharedPreferences.getInt(AppConstants.CHAR_PREFIX + screen, '小');
        instruction.setText(getString(R.string.write_instructions, character));
        String rawSvg = loadAssetTextAsString(getActivity(), JapCharacter.getResourceName(character));
        String modSvg = formatSVGString(rawSvg);
        try {
            SVG svg = SVG.getFromString(modSvg);
            strokeNum = Integer.parseInt(svg.getDocumentTitle());
            Log.v("Stroke Num", strokeNum+"");
            kanjiBackground.setSVG(svg);
        }catch(SVGParseException e){
            Log.v("SVG", "couldn't parse");
            e.printStackTrace();
            throw new RuntimeException(String.format("Unable to parse %s", JapCharacter.getResourceName(character)));
        }
        canvas.registerStrokeCallback(this);
        formatProgressReport(0);
        ((SVGImageView) view.findViewById(R.id.grid_background)).setImageAsset("grid.svg");
        return view;
    }
}
