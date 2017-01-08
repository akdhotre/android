package com.bignerdranch.android.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adhotre on 11/18/16.
 */
public class BoxDrawingView extends View{

    private static String TAG = "BoxDrawingView";

    List<Box> mBoxen = new ArrayList<>();

    private Box mCurrent;
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());
        String action = "";

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                // reset drawing state
                mCurrent = new Box(current);
                mBoxen.add(mCurrent);
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                mCurrent = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                mCurrent = null;
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                if(mCurrent != null){
                    mCurrent.setCurrent(current);
                    invalidate();
                }
                break;
        }

        Log.d(TAG, action + " at  x=" + current.x + ", y=" + current.y);
        return true;
    }

    public BoxDrawingView(Context context) {
        super(context, null);
    }

    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // paint semi-transparent
        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);

        // paint background off-white
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // fill the background
        canvas.drawPaint(mBackgroundPaint);

        for(Box box : mBoxen){
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);

            canvas.drawRect(left, top, right, bottom, mBoxPaint);
        }
    }
}
