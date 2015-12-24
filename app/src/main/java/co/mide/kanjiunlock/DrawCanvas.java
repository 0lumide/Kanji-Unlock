package co.mide.kanjiunlock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class DrawCanvas extends View {
    //The stroke width (default will be 20 pixels).
    private int STROKE_WIDTH = 20;
    // A value is used in a low pass filter to calculate the velocity between two points.
    private float VELOCITY_FILTER_WEIGHT = 0.2f;
    // Those of values present for : ---startPoint---previousPoint----currentPoint
    private Point previousPoint;
    private Point startPoint;
    private Point currentPoint;
    // contain the last velocity. Will be used to calculate the Stroke Width
    private float lastVelocity;
    // contain the last stroke width. Will be used to calculate the Stroke Width
    private float lastWidth;
    // The paint will be used to drawing the line
    private Paint paint;
    // We 'll draw lines to this bitmap.
    private Bitmap bmp;
    // The Canvas which is used to draw line and contain data to the bitmap @bmp
    private Canvas canvasBmp;
    private ArrayList<Stroke> strokes = new ArrayList<>();
    private int strokeCount = -1;
    private StrokeCallback strokeCallback;

    /**
     * This method is used to init the paints.
     */
    public void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#EE010101"));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setColor(Color.BLACK);
    }

    public DrawCanvas(Context context) {
        super(context);
        this.setWillNotDraw(false);
        this.setDrawingCacheEnabled(true);
        init();

    }

    public DrawCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setWillNotDraw(false);
        this.setDrawingCacheEnabled(true);
        init();
    }

    public DrawCanvas(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void onMeasure(int measuredWidth, int measuredHeight) {
        super.onMeasure(measuredWidth, measuredHeight);
        int dimension;
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
            bmp = Bitmap.createBitmap(getWidth(), getHeight(),
                    Bitmap.Config.ARGB_8888);
            canvasBmp = new Canvas(bmp);
            for(int i = 0; i < strokes.size(); i++){
                Stroke stroke = strokes.get(i);
                for (int h = 0; h < stroke.getSize(); h++) {
                    startPoint = previousPoint;
                    previousPoint = currentPoint;
                    currentPoint = stroke.getPoint(h);
                    // Calculate the velocity between the current point to the previous point
                    float velocity = currentPoint.velocityFrom(previousPoint);
                    // A simple low pass filter to mitigate velocity aberrations.
                    velocity = VELOCITY_FILTER_WEIGHT * velocity + (1 - VELOCITY_FILTER_WEIGHT) * lastVelocity;
                    // Calculate the stroke width based on the velocity
                    float strokeWidth = getStrokeWidth(velocity);
                    // Draw line to the canvasBmp canvas.
                    drawLine(canvasBmp, paint, lastWidth, strokeWidth);
                    // Tracker the velocity and the stroke width
                    lastVelocity = velocity;
                    lastWidth = strokeWidth;
                }
            }
            invalidate();
            if(strokeCallback != null)
                strokeCallback.onStrokeCountChange(strokeCount+1);
        }
    }

    public void resetCanvas(){
        strokes = new ArrayList<>();
        strokeCount = -1;
        bmp = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        canvasBmp = new Canvas(bmp);
        invalidate();
    }

    public void registerStrokeCallback(StrokeCallback s){
        if(strokeCallback == null)
            this.strokeCallback = s;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(bmp != null)
            canvas.drawBitmap(bmp, 0, 0, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean returnValue = false;
        if (bmp == null) {
            bmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            canvasBmp = new Canvas(bmp);
        }
        Stroke stroke;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                // In Action down  currentPoint, previousPoint, startPoint will be set at the same point.
                currentPoint = new Point(event.getX(), event.getY(), event.getEventTime());
                previousPoint = currentPoint;
                startPoint = previousPoint;
                strokes.add(new Stroke());
                stroke = strokes.get(strokes.size() - 1);
                stroke.addPoint(currentPoint);
                returnValue = true;
                strokeCount++;
                lastVelocity = 0f;
                break;

            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);
                final int historySize = event.getHistorySize();
                for (int h = 0; h < historySize; h++) {
                    startPoint = previousPoint;
                    previousPoint = currentPoint;
                    currentPoint = new Point(event.getHistoricalX(h), event.getHistoricalY(h), event.getHistoricalEventTime(h));
                    // Calculate the velocity between the current point to the previous point
                    float velocity = currentPoint.velocityFrom(previousPoint);
                    // A simple low pass filter to mitigate velocity aberrations.
                    velocity = VELOCITY_FILTER_WEIGHT * velocity + (1 - VELOCITY_FILTER_WEIGHT) * lastVelocity;
                    // Calculate the stroke width based on the velocity
                    float strokeWidth = getStrokeWidth(velocity);
                    // Draw line to the canvasBmp canvas.
                    drawLine(canvasBmp, paint, lastWidth, strokeWidth);
                    // Tracker the velocity and the stroke width
                    lastVelocity = velocity;
                    lastWidth = strokeWidth;
                    stroke = strokes.get(strokes.size() - 1);
                    stroke.addPoint(currentPoint);
                }
                // Those of values present for : ---startPoint---previousPoint----currentPoint-----
                startPoint = previousPoint;
                previousPoint = currentPoint;
                currentPoint = new Point(event.getX(), event.getY(), event.getEventTime());
                // Calculate the velocity between the current point to the previous point
                float velocity = currentPoint.velocityFrom(previousPoint);
                // A simple low pass filter to mitigate velocity aberrations.
                velocity = VELOCITY_FILTER_WEIGHT * velocity + (1 - VELOCITY_FILTER_WEIGHT) * lastVelocity;
                // Calculate the stroke width based on the velocity
                float strokeWidth = getStrokeWidth(velocity);
                // Draw line to the canvasBmp canvas.
                drawLine(canvasBmp, paint, lastWidth, strokeWidth);
                // Tracker the velocity and the stroke width
                lastVelocity = velocity;
                lastWidth = strokeWidth;
                stroke = strokes.get(strokes.size() - 1);
                stroke.addPoint(currentPoint);
                returnValue = true;
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startPoint = previousPoint;
                previousPoint = currentPoint;
                currentPoint = new Point(event.getX(), event.getY(), event.getEventTime());
                drawLine(canvasBmp, paint, lastWidth, 0);
                stroke = strokes.get(strokes.size() - 1);
                stroke.addPoint(currentPoint);
                invalidate();
                if (strokeCallback != null)
                    strokeCallback.onStrokeCountChange(strokeCount + 1);
                break;
            default:
                break;
        }
        return returnValue;
    }


    private float getStrokeWidth(float velocity) {
//        return STROKE_WIDTH - velocity;
        double num = 1d;
        double den = calcDenominator(velocity);
        float width =  (float)(STROKE_WIDTH*num/den);
        Log.e("Stroke Width", velocity + ":\t" + width);
        return width;
    }

    private double calcDenominator(double dist){
        if(dist == Double.NaN)
            return 1d;
        double returnValue = 0;
        returnValue+= 0.3*dist;
        returnValue+= 1;
        return returnValue;
    }

    // Generate mid point values
    private Point midPoint(Point p1, Point p2) {
        return new Point((p1.x + p2.x) / 2.0f, (p1.y + p2.y) / 2, (p1.time + p2.time) / 2);
    }

    private void drawLine(Canvas canvas, Paint paint, float lastWidth, float currentWidth) {
        Point mid1 = midPoint(previousPoint, startPoint);
        Point mid2 = midPoint(currentPoint, previousPoint);
        draw(canvas, mid1, previousPoint, mid2, paint, lastWidth, currentWidth);
    }


    /**
     * This method is used to draw a smooth line. It follow "Bézier Curve" algorithm (it's Quadratic curves).
     * </br> For reference, you can see more detail here: <a href="http://en.wikipedia.org/wiki/B%C3%A9zier_curve">Wiki</a>
     * </br> We 'll draw a  smooth curves from three points. And the stroke size will be changed depend on the start width and the end width
     *
     * @param canvas : we 'll draw on this canvas
     * @param p0 the start point
     * @param p1 mid point
     * @param p2 end point
     * @param paint the paint is used to draw the points.
     * @param lastWidth start stroke width
     * @param currentWidth end stroke width
     */
    private void draw(Canvas canvas, Point p0, Point p1, Point p2, Paint paint, float lastWidth, float currentWidth) {
        float xa, xb, ya, yb, x, y;
        float different = (currentWidth - lastWidth);

        for (float i = 0; i < 1; i += 0.01) {


            // This block of code is used to calculate next point to draw on the curves
            xa = getPt(p0.x, p1.x, i);
            ya = getPt(p0.y, p1.y, i);
            xb = getPt(p1.x, p2.x, i);
            yb = getPt(p1.y, p2.y, i);

            x = getPt(xa, xb, i);
            y = getPt(ya, yb, i);
            //

            // reset strokeWidth
            paint.setStrokeWidth(lastWidth + different * (i));
            canvas.drawPoint(x, y, paint);
        }
    }


    // This method is used to calculate the next point coordinate.
    private float getPt(float n1, float n2, float percent) {
        float diff = n2 - n1;
        return n1 + (diff * percent);
    }

    public class Stroke{
        private ArrayList<Point> points = new ArrayList<>();

        public Stroke(){
            points.ensureCapacity(100);
        }

        public void addPoint(Point p){
            Point q = new Point(p.x, p.y, p.time);
            points.add(q);
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

    public class Point {
        public final float x;
        public final float y;
        public final long time;

        public Point(float x, float y, long time){
            this.x = x;
            this.y = y;
            this.time = time;
        }

        /**
         * Calculate the distance between current point to the other.
         * @param p the other point
         * @return distance
         */
        private float distanceTo(Point p){
            return (float) (Math.sqrt(Math.pow((x - p.x), 2) + Math.pow((y - p.y), 2)));
        }


        /**
         * Calculate the velocity from the current point to the other.
         * @param p the other point
         * @return velocity
         */
        public float velocityFrom(Point p) {
            if(this.time == p.time)
                return lastVelocity;
            return distanceTo(p) / (this.time - p.time);
        }
    }

    public interface StrokeCallback {
        void onStrokeCountChange(int strokeCount);
    }
}
