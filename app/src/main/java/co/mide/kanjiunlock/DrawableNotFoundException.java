package co.mide.kanjiunlock;

/**
 * Created by Olumide on 5/19/2015.
 */
public class DrawableNotFoundException extends Exception {
    DrawableNotFoundException(String message){
        super("Drawable " + message + " doesn't exist");
    }
}
