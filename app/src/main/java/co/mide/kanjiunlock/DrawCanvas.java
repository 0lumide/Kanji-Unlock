package co.mide.kanjiunlock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;


/**
 * Created by Olumide on 6/6/2015.
 */
public class DrawCanvas extends ImageView {
    private Paint paint;

    public DrawCanvas(Context context){
        super(context);
        paint = new Paint();
        setFocusable(true);
    }

    public DrawCanvas(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        paint = new Paint();
        setFocusable(true);
    }

    public DrawCanvas(Context context, AttributeSet attrs){
        super(context, attrs);
        paint = new Paint();
        setFocusable(true);
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean returnValue = false;
        if ((event.getAction() != MotionEvent.ACTION_CANCEL) && (event.getAction() != MotionEvent.ACTION_UP)) {
            getParent().requestDisallowInterceptTouchEvent(true);
            invalidate();
            returnValue = true;
        }else{
            getParent().requestDisallowInterceptTouchEvent(false);
            invalidate();
        }
        return returnValue;
    }
}
