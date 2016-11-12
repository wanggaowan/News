package com.suixingame.news.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.suixingame.news.R;


/**
 * ============================================================
 *
 * 版 权 ： (c) 2016
 *
 * 作 者 : 汪高皖
 *
 * 版 本 ： 1.0
 *
 * 创建日期 ： 2016/9/29 19:10
 *
 * 描 述 ：展示圆形图片的ImageView
 *        实现原理：就是先画一个纯色的圆形背景，然后再画圆形的要展示的图片，
 *        这个图片的半径就是纯色背景的半径减去要设置的边框的宽度。边框的颜色就是纯色背景的颜色
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class CircleImageView extends ImageView {
    /**
     * 要展示的Drawable转化的Bitmap
     */
    private Bitmap mBitmap;

    /**
     *
     */
    private Paint mPaintBitmap = new Paint (Paint.ANTI_ALIAS_FLAG);

    /**
     * 用来画要显示的圆形图像边框的画笔
     */
    private Paint mPaintBorder = new Paint (Paint.ANTI_ALIAS_FLAG);

    /**
     * 边框的宽度，默认0(无边框)
     */
    private int mBorderWidth;

    /**
     * 边框的颜色，默认灰色
     */
    private int mBorderColor;

    /**
     * 要显示的图像的着色器
     */
    private BitmapShader mShader;

    /**
     * 要显示图像的矩阵参数，此类中主要是用来设置图像的缩放
     */
    private Matrix mMatrix = new Matrix ();


    public CircleImageView (Context context) {
        this (context, null);
    }

    public CircleImageView (Context context, AttributeSet attrs) {
        this (context, attrs, 0);
    }

    public CircleImageView (Context context, AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes (attrs, R.styleable.CircleImageView);
        mBorderWidth = (int) typedArray.getDimension (R.styleable.CircleImageView_borderWidth, 0/*默认边框大小*/);
        mBorderColor = typedArray.getColor (R.styleable.CircleImageView_borderColor, Color.GRAY/*默认颜色*/);
        mPaintBorder.setStrokeWidth (mBorderWidth);
        mPaintBorder.setColor (mBorderColor);
        typedArray.recycle ();
    }

    @Override
    protected void onDraw (Canvas canvas) {
        Bitmap rawBitmap = getBitmap (getDrawable ());
        if (rawBitmap == null) {
            super.onDraw (canvas);
        }

        //获取要展示图像的着色器(Shader)
        float viewMinSize = Math.min (getMeasuredHeight (), getMeasuredWidth ());
        //!rawBitmap.equals (mBitmap)这个判断是因为ImageView有一个缓存机制
        if (mShader == null || !rawBitmap.equals (mBitmap)) {
            mBitmap = rawBitmap;
            mShader = new BitmapShader (mBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        }

        if (mShader != null) {
            mMatrix.setScale ((viewMinSize - mBorderWidth) / rawBitmap.getWidth (), (viewMinSize - mBorderWidth) / rawBitmap.getHeight ());
            mShader.setLocalMatrix (mMatrix);
        }
        mPaintBitmap.setShader (mShader);

        float radius = viewMinSize / 2.0f;
        if (mBorderWidth > 0) {
            //有边框
            canvas.drawCircle (radius, radius, radius, mPaintBorder);
        }
        canvas.drawCircle (radius, radius, radius - mBorderWidth, mPaintBitmap);
    }

    private Bitmap getBitmap (Drawable drawable) {
        if (drawable != null) {
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap ();
            } else if (drawable instanceof ColorDrawable) {
                Rect rect = drawable.getBounds ();
                int width = rect.right - rect.left;
                int height = rect.bottom - rect.top;
                int color = ((ColorDrawable) drawable).getColor ();
                Bitmap bitmap = Bitmap.createBitmap (width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas (bitmap);
                canvas.drawARGB (Color.alpha (color), Color.red (color), Color.green (color), Color.blue (color));
                return bitmap;
            } else {
                return drawableToBitmap (drawable);
            }
        }
        return null;
    }

    /**
     * drawable转Bitmap
     */
    private Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap;
        try {
            bitmap = Bitmap.createBitmap (drawable.getIntrinsicWidth (), drawable.getIntrinsicHeight (), //
                    drawable.getOpacity () != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas (bitmap);
            drawable.setBounds (0, 0, drawable.getIntrinsicWidth (), drawable.getIntrinsicHeight ());
            drawable.draw (canvas);
        } catch (Exception e) {
            Log.e ("CircleImageView", "drawable不能转化为bitmap，请检查drawable是否符合要求");
            return null;
        }
        return bitmap;
    }

    /**
     * dp单位转化为px单位
     *
     * @param dp 要转化的dp单位大小
     * @return 返回对应dp单位相同大小的px
     */
    private double dp2px (double dp) {
        float density = getContext ().getResources ().getDisplayMetrics ().density;
        // dp * density + 0.5加0.5是为了四舍五入
        return dp * density + 0.5;
    }

    /**
     * 这只边框的宽度
     *
     * @param borderWidth 介于0~10，小于0按0计算，大于10按10计算
     */
    public void setBorderWidth (double borderWidth) {
        int temp = (int) dp2px (borderWidth);
        if (temp < 0) {
            mBorderWidth = (int) dp2px (0);
        } else if (temp > 10) {
            mBorderWidth = (int) dp2px (10);
        } else {
            mBorderWidth = temp;
        }
        mPaintBorder.setStrokeWidth (mBorderWidth);
        invalidate ();
    }

    /**
     * 设置边框的颜色
     */
    public void setBorderColor (int borderColor) {
        mBorderColor = borderColor;
        mPaintBorder.setColor (mBorderColor);
        invalidate ();
    }
}
