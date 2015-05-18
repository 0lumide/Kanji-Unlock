package co.mide.kanjiunlock;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private SharedPreferences sharedPreferences;
    // references to our images
    private boolean[] marked;
    private Integer[] characters = {
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
    };

    //probably would never use
//    public void setCharacters(char[] chars){
//        sharedPreferences.edit().putInt(AppConstants.CHAR_COUNT, chars.length).apply();
//        if(chars.length > 0) {
//            sharedPreferences.edit().putBoolean(AppConstants.CHAR_CHOSEN, true).apply();
//            characters = new Integer[chars.length + 1];
//            for (int i = 0; i < chars.length; i++) {
//                characters[i + 1] = (int)chars[i];
//                sharedPreferences.edit().putInt(AppConstants.CHAR_PREFIX + i, chars[i]);
//            }
//        }else{
//            sharedPreferences.edit().putBoolean(AppConstants.CHAR_CHOSEN, false).apply();
//        }
//    }

    public void deleteCharacter(int pos){
        int j = 1;
        Integer[] newChars = new Integer[characters.length - 1];
        boolean[] newmarked = new boolean[characters.length - 1];
        for(int i = 1; i < characters.length; i++){
            if(i != pos){
                newChars[j] = characters[i];
                newmarked[j++] = marked[i];
            }
        }
        marked = newmarked;
        characters = newChars;
        sharedPreferences.edit().putInt(AppConstants.CHAR_COUNT, characters.length).apply();
    }

    public void editCharacter(int pos, char character){
        sharedPreferences.edit().putInt(AppConstants.CHAR_PREFIX + pos, character).apply();
        characters[pos] = (int)character;
    }

    public void unMark(int pos){
        marked[pos] = false;
    }

    public void mark(int pos){
        marked[pos] = true;
    }

    public void addCharacter(char character){
        Integer[] newChars = new Integer[characters.length + 1];
        boolean[] newMarked = new boolean[characters.length + 1];
        for(int i = 1; i < characters.length; i++){
            newChars[i] = characters[i];
            newMarked[i] = marked[i];
        }
        newMarked[characters.length] = false;
        marked = newMarked;
        characters = newChars;
        sharedPreferences.edit().putInt(AppConstants.CHAR_COUNT, characters.length).apply();
        editCharacter(characters.length, character);
    }
    public ImageAdapter(Context c) {
        //Initialize characters
        int count = 5;
//        sharedPreferences = c.getSharedPreferences(AppConstants.PREF_NAME, c.MODE_PRIVATE);
//        int count = sharedPreferences.getInt(AppConstants.CHAR_COUNT, 0);
        marked = new boolean[count + 1];
//        if(sharedPreferences.getBoolean(AppConstants.CHAR_CHOSEN, false) && (count > 0)){
//            characters = new Integer[count + 1];
            for(int i = 1; i <= count; i++){
                marked[i] = false;
//                characters[i] = sharedPreferences.getInt(AppConstants.CHAR_PREFIX + i, 0);
            }
//        }else
//            //Remember the button
//            characters = new Integer[1];
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
            view = button;
        }
        else {
            ImageView imageView;
            if ((convertView instanceof Button) || (convertView == null)){
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setAdjustViewBounds(true);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageResource(characters[position]);
            view = imageView;
        }
        if(marked[position])
            view.setAlpha(0.4f);
        else
            view.setAlpha(1.0f);
        return view;
    }
}