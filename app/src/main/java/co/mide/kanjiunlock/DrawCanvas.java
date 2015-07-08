package co.mide.kanjiunlock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;


/**
 * Created by Olumide on 6/6/2015.
 */
public class DrawCanvas extends View {
    private Paint paint;
    private ArrayList<Stroke> strokes = new ArrayList<>();
    private int strokeCount = -1;
    private Bitmap viewCache;
    private final float STROKE_WIDTH = 17;
    private StrokeCallback strokeCallback;

    public DrawCanvas(Context context){
        super(context);
        paint = new Paint();
        paint.setColor(Color.parseColor("#EE010101"));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
    }

    public DrawCanvas(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        paint = new Paint();
        paint.setColor(Color.parseColor("#EE010101"));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
    }

    public DrawCanvas(Context context, AttributeSet attrs){
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.parseColor("#EE010101"));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
    }

    public Stroke getStroke(int strokeNum){
        return strokes.get(strokeNum);
    }

    public void resetCanvas(){
        strokes = new ArrayList<>();
        strokeCount = -1;
        viewCache = null;
        invalidate();
    }

    public void registerStrokeCallback(StrokeCallback s){
        if(strokeCallback == null)
            this.strokeCallback = s;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.v("onDraw", "onDraw");
        if(viewCache != null) {
            canvas.drawBitmap(viewCache, 0, 0, null);
            Log.v("Size:", viewCache.getWidth()+" x "+viewCache.getHeight());
        }else{
            Log.v("view cache", "Null");
        }
        //draw stroke
        if(strokeCount >= 0) {
            paint.setStrokeWidth(STROKE_WIDTH);
            Stroke stroke = strokes.get(strokeCount);
            if(stroke.getSize() >= 1) {
                canvas.drawPoint(stroke.getX(0), stroke.getY(0), paint);
            }
            if (stroke.getSize() >= 2) {
                paint.setStrokeWidth(STROKE_WIDTH);
                canvas.drawLine(stroke.getX(0), stroke.getY(0), stroke.getX(1), stroke.getY(1), paint);
            }
            if (stroke.getSize() > 2) {
                for (int f = 2; f < stroke.getSize(); f++) {
                    double distance = pythag(stroke.getX(f - 1), stroke.getY(f - 1), stroke.getX(f), stroke.getY(f));
                    paint.setStrokeWidth(getStrokeWidth(distance, stroke.getTime(f) - stroke.getTime(f-1)));
                    canvas.drawLine(stroke.getX(f - 1), stroke.getY(f - 1), stroke.getX(f), stroke.getY(f), paint);
                }
            }
        }
    }

    private int squared(int num){
        return num*num;
    }

    private float getStrokeWidth(double dist, long time){
        double num = 1d;
        double den = calcDenominator(dist/time);
        return (float)(STROKE_WIDTH*num/den);
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
        if ((event.getAction() == MotionEvent.ACTION_MOVE) || (event.getAction() == MotionEvent.ACTION_DOWN)) {
            getParent().requestDisallowInterceptTouchEvent(true);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                setDrawingCacheEnabled(true);
                buildDrawingCache(true);
                viewCache = Bitmap.createBitmap(getDrawingCache());
                setDrawingCacheEnabled(false);
                strokeCount++;
                strokes.add(new Stroke());
            }
            Stroke stroke = strokes.get(strokes.size() - 1);
            if(event.getAction() == MotionEvent.ACTION_MOVE){
                final int historySize = event.getHistorySize();
                final int pointerCount = event.getPointerCount();
                Log.d("stroke", String.format("Hist: %d, Point: %d",historySize, pointerCount));
                for (int h = 0; h < historySize; h++) {
                    stroke.addPoint((int) event.getHistoricalX(h),
                            (int) event.getHistoricalY(h),
                            event.getHistoricalPressure(h),
                            event.getHistoricalEventTime(h));
                }
                stroke.addPoint((int) event.getX(),
                        (int) event.getY(),
                        event.getPressure(),
                        event.getEventTime());
            }else{
                final int pointerCount = event.getPointerCount();
                Log.d("stroke", String.format("Point: %d", pointerCount));
                stroke.addPoint((int) event.getX(),
                        (int) event.getY(),
                        event.getPressure(),
                        event.getEventTime());
            }
            invalidate();
            returnValue = true;
        }else{
            if(((event.getAction() == MotionEvent.ACTION_UP)||(event.getAction() == MotionEvent.ACTION_CANCEL))&&(strokeCallback != null))
                strokeCallback.onStrokeCountChange(strokeCount+1);
        }
        return returnValue;
    }

    public class Stroke{
        private ArrayList<Integer> xCoor = new ArrayList<>();
        private ArrayList<Integer> yCoor = new ArrayList<>();
        private ArrayList<Float> pressure = new ArrayList<>();
        private ArrayList<Long> time = new ArrayList<>();

        public Stroke(){
            xCoor.ensureCapacity(250);
            yCoor.ensureCapacity(250);
            time.ensureCapacity(250);
            pressure.ensureCapacity(250);
        }
        public int getX(int index){
            return xCoor.get(index);
        }
        public int getY(int index){
            return yCoor.get(index);
        }
        public float getPressure(int index){
            return pressure.get(index);
        }
        public long getTime(int index){
            return time.get(index);
        }
        public void addPoint(int x, int y, float pressure, long time){
            Log.v("add", "added: "+x+","+y+","+pressure+","+time);
            xCoor.add(x);
            yCoor.add(y);
            this.time.add(time);
            float p = pressure;
            if(pressure < 0)
                p = 0;
            else if(pressure > 1)
                p = 1;
            this.pressure.add(p);
        }
        public int getSize(){
            return xCoor.size();
        }
    }
}