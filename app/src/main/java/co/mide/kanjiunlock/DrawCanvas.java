package co.mide.kanjiunlock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;


public class DrawCanvas extends View {
    private Paint paint;
    private ArrayList<Stroke> strokes = new ArrayList<>();
    private int strokeCount = -1;
    private Bitmap viewCache;
    private final float STROKE_WIDTH = 18;
    private StrokeCallback strokeCallback;
    private int count = 1;
    private Canvas bitmapCanvas;
    private int last = 0;
    private boolean viewCacheEmpty = true;

    private void init() {
        paint = new Paint();
        paint.setColor(Color.parseColor("#EE010101"));
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);
    }

    public DrawCanvas(Context context) {
        super(context);
        init();
    }

    public DrawCanvas(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DrawCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public void onMeasure(int measuredWidth, int measuredHeight) {
        super.onMeasure(measuredWidth, measuredHeight);
        int dimension;
        Log.e("onMeasure" + count++, String.format("Width: %s: %d: %d\tHeight: %s: %d: %d",
                        MeasureSpec.toString(MeasureSpec.getMode(measuredWidth)), getMeasuredWidth(), getWidth(),
                        MeasureSpec.toString(MeasureSpec.getMode(measuredHeight)), getMeasuredHeight(), getHeight())
        );
        if ((getWidth() == 0) && (getHeight() == 0)){
            dimension = MeasureSpec.makeMeasureSpec(Math.min(getMeasuredWidth(), getMeasuredHeight()), MeasureSpec.EXACTLY);
            setMeasuredDimension(dimension, dimension);
        }else{
            dimension = MeasureSpec.makeMeasureSpec(Math.min(getWidth(), getHeight()), MeasureSpec.EXACTLY);
            setMeasuredDimension(dimension, dimension);
        }
    }

    public Stroke getStroke(int index){
        return strokes.get(index);
    }

    public void undoStroke(){
        if(strokeCount >= 0) {
            strokeCount--;
            strokes.remove(strokes.size() - 1);
            viewCacheEmpty = true;
            viewCache = Bitmap.createBitmap(getWidth(), getHeight(),
                    Bitmap.Config.ARGB_8888);
            bitmapCanvas = new Canvas(viewCache);
            invalidate();
            if(strokeCallback != null)
                strokeCallback.onStrokeCountChange(strokeCount+1);
        }
    }

    public void resetCanvas(){
        strokes = new ArrayList<>();
        strokeCount = -1;
        viewCacheEmpty = true;
        viewCache = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(viewCache);
        invalidate();
    }

    public void registerStrokeCallback(StrokeCallback s){
        if(strokeCallback == null)
            this.strokeCallback = s;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw strokePath
        if(strokeCount >= 0) {
            if(!viewCacheEmpty) {
                Stroke stroke = strokes.get(strokes.size() - 1);
                if (stroke.getBezierCount() == 0){
                    paint.setStrokeWidth(STROKE_WIDTH);
                    bitmapCanvas.drawPoint(stroke.getPoint(0).x, stroke.getPoint(0).y, paint);
                }else {
                    for(; last < stroke.getBezierCount(); last++)
                        stroke.getBezier(last).draw(bitmapCanvas, paint);
                }
            }else {
                viewCacheEmpty = false;
                for (int i = 0; i <= strokeCount; i++) {
                    Stroke stroke = strokes.get(i);
                    paint.setStrokeWidth(STROKE_WIDTH);
                    bitmapCanvas.drawPoint(stroke.getPoint(0).x, stroke.getPoint(0).y, paint);
                    for (int j = 0; j < stroke.getSize(); j++) {
                        if (j < stroke.getSize() - 1) {
                            paint.setStrokeWidth(stroke.getBezier(j).endWidth);
                            stroke.getBezier(j).draw(bitmapCanvas, paint);
                        }
                    }
                }
            }
            canvas.drawBitmap(viewCache, 0, 0, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(bitmapCanvas == null) {
            viewCache = Bitmap.createBitmap(getWidth(), getHeight(),
                    Bitmap.Config.ARGB_8888);
            bitmapCanvas = new Canvas(viewCache);
        }
        boolean returnValue = false;
        if ((event.getAction() == MotionEvent.ACTION_MOVE) || (event.getAction() == MotionEvent.ACTION_DOWN)) {
            getParent().requestDisallowInterceptTouchEvent(true);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                strokeCount++;
                last = 0;
                strokes.add(new Stroke());
            }
            Stroke stroke = strokes.get(strokes.size() - 1);
            if(event.getAction() == MotionEvent.ACTION_MOVE){
                final int historySize = event.getHistorySize();
                for (int h = 0; h < historySize; h++) {
                    stroke.addPoint(event.getHistoricalX(h),
                            event.getHistoricalY(h), event.getHistoricalEventTime(h));
                }
                stroke.addPoint(event.getX(), event.getY(), event.getEventTime());
            } else {
                if(stroke.getSize() > 0) {
                    stroke.addPoint(event.getX(), event.getY(), event.getEventTime());
                }else{
                    stroke.addPoint(event.getX(), event.getY(), event.getEventTime());
                }
            }
            invalidate();
            returnValue = true;
        }else{
            if((event.getAction() == MotionEvent.ACTION_UP)||(event.getAction() == MotionEvent.ACTION_CANCEL)){
                if (strokeCallback != null)
                    strokeCallback.onStrokeCountChange(strokeCount + 1);
            }
        }
        return returnValue;
    }


    public class Stroke{
        private ArrayList<Bezier> stroke = new ArrayList<>();
        private ArrayList<Point> points = new ArrayList<>();

        public Stroke(){
            stroke.ensureCapacity(250);
        }

        public Bezier getBezier(int index){
            return stroke.get(index);
        }

        public int getBezierCount(){
            return stroke.size();
        }

        public void addPoint(float x, float y, long time){
            Point p = new Point(x, y, time);
            if(points.size() > 0){
                Bezier b = stroke.size() == 0? null : stroke.get(stroke.size() - 1);
                Point point = points.get(points.size() - 1);
                Bezier bezier = new Bezier(b, point, p);
                stroke.add(bezier);
            }
            points.add(p);
        }

        public Point getPoint(int index){
            return points.get(index);
        }

        public int getX(int index){
            return (int)points.get(index).x;
        }

        public int getY(int index){
            return (int)points.get(index).y;
        }

        public int getSize(){
            return points.size();
        }
    }

    public class Point{
        public final float x, y;
        public final long time;

        public Point(float x, float y, long t){
            this.x = x;
            this.y = y;
            time = t;
        }

        public double calcDistance(Point p){
            float x = this.x - p.x;
            float y = this.y - p.y;
            float yy = y*y;
            float xx = x*x;
            return Math.sqrt(xx + yy);
        }


        public double calcSpeed(Point p){
            double dist = calcDistance(p);
            return dist/(this.time - p.time);
        }

        @Override
        public String toString(){
            return String.format("x: %.0f, y: %.0f", x, y);
        }
    }

    public class Bezier{
        public Point p0, p1, p2, p3, A;
        public final Bezier bezier;
        private final int numPoints;
        private final float startWidth, endWidth;
        private final float VELOCITY_FILTER_WEIGHT = 0.1f;

        public Bezier(@Nullable Bezier bezier, Point p0, Point p3){
            this.p0 = p0;
            this.p3 = p3;
            this.bezier = bezier;
            this.p1 = calcP1();
            this.p2 = calcP2();
            this.A = calcA();
            numPoints = (int) (p3.calcDistance(p0));
            if(bezier == null){
                startWidth = STROKE_WIDTH;
            }else{
                startWidth = bezier.endWidth;
            }
            endWidth = VELOCITY_FILTER_WEIGHT * getStrokeWidth(p3.calcSpeed(p0))
                    + (1 - VELOCITY_FILTER_WEIGHT) * startWidth;
        }


        /** Draws a variable-width Bezier curve. */
        public void draw(Canvas canvas, Paint paint) {
            float originalWidth = paint.getStrokeWidth();
            float widthDelta = endWidth - startWidth;

            for (int i = 0; i < numPoints; i++) {
                // Calculate the Bezier (x, y) coordinate for this step.
                float t = ((float) i) / numPoints;

                float x = p0.x + t*(p3.x - p0.x);
                float y = p0.y + t*(p3.y - p0.y);

                paint.setStrokeWidth(startWidth + t * widthDelta);
                canvas.drawPoint(x, y, paint);
            }

            paint.setStrokeWidth(originalWidth);
        }

        private float getStrokeWidth(double velocity){
            double num = 1d;
            double den = calcDenominator(velocity);
            return (float)(STROKE_WIDTH*num/den);
        }

        private double calcDenominator(double dist){
            if(dist == Double.NaN)
                return 1d;
            double returnValue = 0;
            returnValue+= dist;
            returnValue+= 1;
            return returnValue;
        }

        private Point calcP1(){
            if(bezier == null){
                Point p2 = calcP2();
                float x = (p2.x - p0.x)/2f;
                float y = (p2.y - p0.y)/2f;
                return new Point(x+p0.x, y+p0.y, -1);
            }else{
                float x = 2 * bezier.p3.x - bezier.p2.x;
                float y = 2 * bezier.p3.y - bezier.p2.y;
                return new Point(x, y, -1);
            }
        }

        private Point calcP2(){
            if(bezier == null) {
                float x = 1.5f * (p3.x - p0.x) / 3f;
                float y = 1.5f * (p3.y - p0.y) / 3f;
                return new Point(x + p0.x, y + p0.y, -1);
            }else{
                float x = 2.0f * (p3.x - p0.x) / 3f;
                float y = 2.0f * (p3.y - p0.y) / 3f;
                return new Point(x + p0.x, y + p0.y, -1);
            }
        }

        private Point calcA(){
            float x = 2 * p2.x - p1.x;
            float y = 2 * p2.y - p1.y;
            return new Point(x, y, -1);
        }
    }

    public interface StrokeCallback {
        void onStrokeCountChange(int strokeCount);
    }
}