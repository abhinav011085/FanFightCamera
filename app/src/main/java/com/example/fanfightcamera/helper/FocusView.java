package com.example.fanfightcamera.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.example.fanfightcamera.R;

public class FocusView extends View {
    Context context;
    private Paint mTransparentPaint;
    private Paint mSemiBlackPaint;
    private Path mPath = new Path();

    public FocusView(Context context) {
        super(context);
        initPaints();
        this.context = context;
    }

    public FocusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
        this.context = context;
    }

    public FocusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaints();
        this.context = context;
    }

    private void initPaints() {
        mTransparentPaint = new Paint();
        mTransparentPaint.setStyle(Paint.Style.STROKE);
        mTransparentPaint.setColor(Color.WHITE);
        mTransparentPaint.setStrokeWidth(2);

        mSemiBlackPaint = new Paint();
        mSemiBlackPaint.setColor(Color.TRANSPARENT);
        mSemiBlackPaint.setStrokeWidth(10);

    }
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPath.reset();

        int sideMargins = context.getResources().getInteger(R.integer.image_box_side_margin);
        int height = context.getResources().getInteger(R.integer.image_box_height);

        RectF rect = new RectF(sideMargins, (int) (getHeight() / 2) - (height / 2), Utility.getScreenWidth((Activity) context) - sideMargins, (int) (getHeight() / 2) + (height / 2));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPath.addRoundRect(rect, 10, 10, Path.Direction.CW);
        } else
            mPath.addRect(rect, Path.Direction.CW);
        mPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(rect, 10, 10, mTransparentPaint);
        } else
            canvas.drawRect(rect, mTransparentPaint);

        canvas.drawPath(mPath, mSemiBlackPaint);
        canvas.clipPath(mPath);
        canvas.drawColor(Color.parseColor("#A6000000"));
    }
}
