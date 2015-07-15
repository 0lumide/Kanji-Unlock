package co.mide.kanjiunlock;

import java.text.Normalizer;

/**
 * Created by Olumide on 5/17/2015.
 */
public class JapCharacter {

    public static String getResourceName(int n){
        String hex = Integer.toHexString(n);
        while(hex.length() < 5)
            hex = "0" + hex;
        hex = hex + ".svg";
        return hex;
    }
    public static boolean isValid(char n){
        if(isKana(n)||(n >= 19968)&&(n<=40879)||((n>13312)&&(n < 19903)))
            return true;
        return false;
    }

    //Ha! I knew my work on versal would pay off somewhere
    public static boolean isKana(char n){
        //outside kana range
        if((n <= 12353) || (n >= 12539)){
            return false;
        }
        //between the two kanas
        else if((n > 12436)  && (n < 12450)){
            return false;
        }
        //some little hiragana
        else if((n ==12355)||(n == 12357) || (n == 12359) || (n == 12361))
            return false;
            //Hiragana
        else if((n > 12353) && (n < 12436)){
            if((n == 12387)||(n == 12419) || (n == 12421) || (n == 12423) ||(n == 12430))
                return false;
            return true;
        }
        //Katakana
        else if((n > 12449) && (n < 12539)){
            //Some small katakana
            if((n == 12534)||(n == 12533) || (n == 12515) || (n == 12517) || (n == 12519) || (n == 12526) || (n == 12483))
                return false;
            else if((n == 12451) || (n == 12453) || (n == 12455) || (n == 12457))
                return false;
            return true;
        }
        return false;
    }

    public static boolean isVoiced(Character character){//i.e has ten ten
        //first check if kana
        if(!isKana(character))
            return false;
        return (character != getVoiceless(character));
    }

    public static char getVoiceless(char c){
        return Normalizer.normalize(Character.toString(c), Normalizer.Form.NFD).charAt(0);
    }
}
