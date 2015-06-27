package co.mide.kanjiunlock;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.support.v4.app.FragmentActivity;

import com.caverock.androidsvg.SVGImageView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private KanjiUnlock kanjiUnlock;
    private SharedPreferences sharedPreferences;
    // references to our images
    private boolean[] marked;
    private int[] characters;

    public boolean getMarked(int i){
        return marked[i];
    }

    public void deleteCharacter(int pos){
        int j = 1;
        int[] newChars = new int[characters.length - 1];
        boolean[] newmarked = new boolean[characters.length - 1];
        for(int i = 1; i < characters.length; i++){
            if(i != pos){
                newChars[j] = characters[i];
                sharedPreferences.edit().putInt(AppConstants.CHAR_PREFIX + j, newChars[j]).apply();
                newmarked[j++] = marked[i];
            }
        }
        marked = newmarked;
        characters = newChars;
        if(characters.length == 1)
            sharedPreferences.edit().putBoolean(AppConstants.CHAR_CHOSEN, false).apply();
        sharedPreferences.edit().putInt(AppConstants.CHAR_COUNT, characters.length - 1).apply();
    }

    public void editCharacter(int pos, char character){
        sharedPreferences.edit().putInt(AppConstants.CHAR_PREFIX + pos, character).apply();
        characters[pos] = (int)character;
        notifyDataSetChanged();
    }

    public void unMark(int pos){
        marked[pos] = false;
    }

    public void mark(int pos){
        marked[pos] = true;
    }

    public char getCharacter(int pos){
        return (char)characters[pos];
    }

    public void addCharacter(char character){
        int[] newChars = new int[characters.length + 1];
        boolean[] newMarked = new boolean[characters.length + 1];
        for(int i = 1; i < characters.length; i++){
            newChars[i] = characters[i];
            newMarked[i] = marked[i];
        }
        newMarked[characters.length] = false;
        marked = newMarked;
        characters = newChars;
        sharedPreferences.edit().putInt(AppConstants.CHAR_COUNT, characters.length - 1).apply();
        sharedPreferences.edit().putBoolean(AppConstants.CHAR_CHOSEN, true).apply();
        editCharacter(characters.length - 1, character);
    }

    public ImageAdapter(Context c, KanjiUnlock kanjiUnlock) {
        this.kanjiUnlock = kanjiUnlock;
        //Initialize characters
        sharedPreferences = c.getSharedPreferences(AppConstants.PREF_NAME, c.MODE_PRIVATE);
        int count = sharedPreferences.getInt(AppConstants.CHAR_COUNT, 1);
        marked = new boolean[count + 1];
        characters = new int[count + 1];
        if(sharedPreferences.getBoolean(AppConstants.CHAR_CHOSEN, true) && (count > 0)) {
            for (int i = 1; i <= count; i++) {
                marked[i] = false;
                characters[i] = sharedPreferences.getInt(AppConstants.CHAR_PREFIX + i, 'A');
            }
        }
        mContext = c;
    }

    @Override
    public int getCount() {
        return characters.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(position == 0){
            Button button;
            if ((convertView == null) || !(convertView instanceof Button)) {
                // if it's not recycled, initialize some attributes
                button = new Button(mContext);
                button.setText(R.string.plus_sign);
                Resources r = mContext.getResources();
                float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, r.getDisplayMetrics());
                button.setWidth((int)px);
                button.setHeight((int) px);
                button.setGravity(Gravity.CENTER);
                button.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, r.getDisplayMetrics()));
            } else {
                button = (Button)convertView;
            }
            if(!(characters.length < 4))
                button.setEnabled(false);
            else {
                button.setEnabled(true);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        kanjiUnlock.plusButtonClicked();
                    }
                });
            }
            view = button;
        }
        else {
            SVGImageView svgImageView;
            if ((convertView instanceof Button) || (convertView == null)){
                svgImageView = new SVGImageView(mContext);
                svgImageView.setAdjustViewBounds(true);
                svgImageView.setBackgroundColor(mContext.getResources().getColor(R.color.svg_background));
            } else {
                svgImageView = (SVGImageView) convertView;
            }
            int character = characters[position];
            String name = JapCharacter.getResourceName(character);
            svgImageView.setImageAsset(name);
            view = svgImageView;
        }
        if(marked[position])
            view.setAlpha(0.4f);
        else
            view.setAlpha(1.0f);
        return view;
    }
}