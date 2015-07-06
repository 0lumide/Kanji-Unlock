package co.mide.kanjiunlock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.ArrayList;


/**
 * Created by Olumide on 6/6/2015.
 */
public class DrawCanvas extends ImageView {
    private Paint paint;
    private ArrayList<Stroke> strokes = new ArrayList<>();

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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);

        //draw stroke
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.parseColor("#EE010101"));
        for(int i = 0; i < strokes.size(); i++) {
            paint.setStrokeWidth(15);
            Stroke stroke = strokes.get(i);
            if (stroke.getSize() > 1) {
                for (int f = 1; f < stroke.getSize(); f++) {
                    double distance = pythag(stroke.getX(f - 1), stroke.getY(f - 1), stroke.getX(f), stroke.getY(f));
                    paint.setStrokeWidth(getStrokeWidth(distance, stroke.getTime(f) - stroke.getTime(f-1)));
                    canvas.drawLine(stroke.getX(f - 1), stroke.getY(f - 1), stroke.getX(f), stroke.getY(f), paint);
                }
            } else if (stroke.getSize() == 1) {
//                canvas.drawPoint(stroke.getX(0), stroke.getY(0), paint);
            }
        }
    }

    private int squared(int num){
        return num*num;
    }

    private int getStrokeWidth(double dist, long time){
        double num = 1d;
        double den = calcDenominator(dist/time);
        Log.v("dist and time", String.format("dist: %s, time: %s", dist+"", time+""));
        return (int)(15*num/den);
    }

    private double calcDenominator(double dist){
        if(dist == Double.NaN)
            return 1d;
        double returnValue = 0;
        returnValue+= 0.1*dist;
        returnValue+= 1;
        return returnValue;
    }
    private double pythag(int x1, int y1, int x2, int y2){
        int sum = squared(x2-x1)+squared(y2 - y1);
        return Math.sqrt(sum);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean returnValue = false;
        if ((event.getAction() != MotionEvent.ACTION_CANCEL) && (event.getAction() != MotionEvent.ACTION_UP)) {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
                strokes.add(new Stroke());
            Stroke stroke = strokes.get(strokes.size()-1);
            getParent().requestDisallowInterceptTouchEvent(true);
            Log.v("Down time", event.getDownTime()+"");
            stroke.addPoint((int) event.getX(), (int) event.getY(), event.getEventTime());
            invalidate();
            returnValue = true;
        }else{
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return returnValue;
    }

    private class Stroke{
        private ArrayList<Integer> xCoor = new ArrayList<>();
        private ArrayList<Integer> yCoor = new ArrayList<>();
        private ArrayList<Long> time = new ArrayList<>();

        public int getX(int index){
            return xCoor.get(index);
        }
        public int getY(int index){
            return yCoor.get(index);
        }
        public long getTime(int index){
            return time.get(index);
        }
        public void addPoint(int x, int y, long eventTime){
            xCoor.add(x);
            yCoor.add(y);
            time.add(eventTime);
        }
        public int getSize(){
            return xCoor.size();
        }
    }
}
