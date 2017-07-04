package com.example.artem.myapplication.control.imp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ScaleGestureDetectorCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.example.artem.myapplication.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by artem on 03.07.17.
 */

public class Container extends RelativeLayout {
//    private String scheme = "http://ns.autosuper.ru/2003/arch11/030/plan_2et_b.jpg";
    private String scheme = "http://www.biblioteca-nn.ru/file/0007/4473/plan1.gif";

    private List<Control> controls;
    private Rect bounds;
    private Point center;
//    private int zoom = 2;

    private int width;
    private int height;

    private float mx, my;

    private Target target;

//    private float ox, oy;

    private ImageView ivScheme;
    private RelativeLayout container;
    private ScrollView vScroll;
    private HorizontalScrollView hScroll;
    private boolean mIsScrolling = false;
    private ScaleGestureDetector mScaleDetector;
    private float scaleFactor = .5f;

    private float minScaleFactor = .2f;



    private int mTouchSlop;
    private Context context;

    public Container(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
        target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                width = bitmap.getWidth();
                height = bitmap.getHeight();
                ivScheme.setImageBitmap(bitmap);
//                minScaleFactor = Math.min(getHeight()/height, getWidth() /width);
                onUIChanged();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.container, this);
        vScroll = (ScrollView) findViewById(R.id.vScroll);
        hScroll = (HorizontalScrollView) findViewById(R.id.hScroll);
        ivScheme = (ImageView)findViewById(R.id.ivScheme);
        ivScheme.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(scaleFactor == 1) {
                    scaleFactor = .5f;
                } else {
                    scaleFactor = 1;
                }
                onUIChanged();
            }
        });

        container = (RelativeLayout)findViewById(R.id.rlContainer);
        Picasso.with(context).load(scheme).into(target);

        ivScheme.setOnTouchListener(new ImageMatrixTouchHandler(context));

        controls = new ArrayList<>();
        add(new Control(container, 1, new Point(728, 348), 0));
        add(new Control(container, 2, new Point(1044, 548), 1));
        add(new Control(container, 3, new Point(1472, 468), 0));
        add(new Control(container, 1, new Point(1204, 1000), 1));
    }

    public void onUIChanged(){
        for(Control c : controls){
            c.onUIChanged(scaleFactor, 1);
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivScheme.getLayoutParams();
        float originX = hScroll.getScrollX() + vScroll.getScrollX() + mx;
        float originY = hScroll.getScrollY() + vScroll.getScrollY() + my;

        float relativeX = originX/params.width;
        float relativeY = originY/params.height;
        params.height = (int)(height*scaleFactor);
        params.width = (int)(width*scaleFactor);
        float newX = relativeX*width*scaleFactor;
        float newY = relativeY*height*scaleFactor;

        final float newCenterY = newY - getHeight()/2;
        final float newCenterX = newX - getWidth() /2;

        ivScheme.setLayoutParams(params);

        hScroll.post(new Runnable() {
            @Override
            public void run() {
                hScroll.scrollTo((int)newCenterX, (int)newCenterY);
            }
        });

        vScroll.post(new Runnable() {
            @Override
            public void run() {
                vScroll.scrollTo((int)newCenterX, (int)newCenterY);
            }
        });
    }

    public void REonUIChanged(float focusX, float focusY){
        for(Control c : controls){
            c.onUIChanged(scaleFactor, 1);
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivScheme.getLayoutParams();
        float originX = hScroll.getScrollX() + vScroll.getScrollX() + focusX;
        float originY = hScroll.getScrollY() + vScroll.getScrollY() + focusY;

        float relativeX = originX/params.width;
        float relativeY = originY/params.height;


        float newX = relativeX*width*scaleFactor;
        float newY = relativeY*height*scaleFactor;

        final float newCenterY = newY - getHeight()/2;
        final float newCenterX = newX - getWidth() /2;

        params.height = (int)(height*scaleFactor);
        params.width = (int)(width*scaleFactor);
        ivScheme.setLayoutParams(params);

        hScroll.post(new Runnable() {
            @Override
            public void run() {
                hScroll.scrollTo((int)newCenterX, (int)newCenterY);
            }
        });

        vScroll.post(new Runnable() {
            @Override
            public void run() {
                vScroll.scrollTo((int)newCenterX, (int)newCenterY);
            }
        });

    }



    private void add(final Control control){
        container.addView(control);
        controls.add(control);
        control.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Clicked : " + control.getIndex(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Control getControl(int position) {
        if(controls != null && !controls.isEmpty() && position < controls.size() && position > 0){
            return controls.get(position);
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        mScaleDetector.onTouchEvent(ev);

        final int action = MotionEventCompat.getActionMasked(ev);

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mIsScrolling = false;
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mx = ev.getX();
                my = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE: {

                if (mIsScrolling) {
                    return true;
                }

                final int xDiff = (int)Math.round(Math.sqrt(Math.pow(mx - ev.getX(), 2) + Math.pow(my - ev.getY(), 2)));

                if (xDiff > mTouchSlop) {
                    mIsScrolling = true;
                    return true;
                }
                break;
            }
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float curX, curY;
        mScaleDetector.onTouchEvent(event);
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mx = event.getX();
                my = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                curX = event.getX();
                curY = event.getY();
                vScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                hScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                mx = curX;
                my = curY;
                break;
            case MotionEvent.ACTION_UP:
                curX = event.getX();
                curY = event.getY();
                vScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                hScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                break;
        }

        return true;
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            scaleFactor = Math.max(minScaleFactor, Math.min(scaleFactor, 1.0f));

            REonUIChanged(detector.getFocusX(), detector.getFocusY());
            return true;
        }
    }

//    private final ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener
//            = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
//        /**
//         * This is the active focal point in terms of the viewport. Could be a local
//         * variable but kept here to minimize per-frame allocations.
//         */
//        private PointF viewportFocus = new PointF();
//        private float lastSpanX;
//        private float lastSpanY;
//
//        // Detects that new pointers are going down.
//        @Override
//        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
//            lastSpanX = ScaleGestureDetectorCompat.
//                    getCurrentSpanX(scaleGestureDetector);
//            lastSpanY = ScaleGestureDetectorCompat.
//                    getCurrentSpanY(scaleGestureDetector);
//            return true;
//        }
//
//        @Override
//        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
//
//            float spanX = ScaleGestureDetectorCompat.
//                    getCurrentSpanX(scaleGestureDetector);
//            float spanY = ScaleGestureDetectorCompat.
//                    getCurrentSpanY(scaleGestureDetector);
//
//            float newWidth = lastSpanX / spanX * mCurrentViewport.width();
//            float newHeight = lastSpanY / spanY * mCurrentViewport.height();
//
//            float focusX = scaleGestureDetector.getFocusX();
//            float focusY = scaleGestureDetector.getFocusY();
//            // Makes sure that the chart point is within the chart region.
//            // See the sample for the implementation of hitTest().
//            hitTest(scaleGestureDetector.getFocusX(),
//                    scaleGestureDetector.getFocusY(),
//                    viewportFocus);
//
//            mCurrentViewport.set(
//                    viewportFocus.x
//                            - newWidth * (focusX - mContentRect.left)
//                            / mContentRect.width(),
//                    viewportFocus.y
//                            - newHeight * (mContentRect.bottom - focusY)
//                            / mContentRect.height(),
//                    0,
//                    0);
//            mCurrentViewport.right = mCurrentViewport.left + newWidth;
//            mCurrentViewport.bottom = mCurrentViewport.top + newHeight;
//        ...
//            // Invalidates the View to update the display.
//            ViewCompat.postInvalidateOnAnimation(InteractiveLineGraphView.this);
//
//            lastSpanX = spanX;
//            lastSpanY = spanY;
//            return true;
//        }
//    };

}
