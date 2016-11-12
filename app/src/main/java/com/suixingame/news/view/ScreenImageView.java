package com.suixingame.news.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
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
 * 描 述 ：用来处理图像在和显示区域大小不匹配时 高清完全填充显示区域
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class ScreenImageView extends ImageView {
    /**
     * 要展示的Drawable转化的Bitmap
     */
    private Bitmap mBitmap;

    /**
     *
     */
    private Paint mPaintBitmap = new Paint (Paint.ANTI_ALIAS_FLAG);


    /**
     * 要显示的图像的着色器
     */
    private BitmapShader mShader;

    /**
     * 要显示图像的矩阵参数，此类中主要是用来设置图像的缩放
     */


    public ScreenImageView (Context context) {
        this (context, null);
    }

    public ScreenImageView (Context context, AttributeSet attrs) {
        this (context, attrs, 0);
    }

    public ScreenImageView (Context context, AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);

    }

    @Override
    protected void onDraw (Canvas canvas) {
        Bitmap rawBitmap = getBitmap (getDrawable ());
        if (rawBitmap == null) {
            super.onDraw (canvas);
        }

        //获取要展示图像的着色器(Shader)
        int measuredHeight = getMeasuredHeight ();
        int measuredWidth = getMeasuredWidth ();
        int width = rawBitmap.getWidth ();
        int height = rawBitmap.getHeight ();
        //!rawBitmap.equals (mBitmap)这个判断是因为ImageView有一个缓存机制
        if (mShader == null || !rawBitmap.equals (mBitmap)) {
            mBitmap = rawBitmap;
            mShader = new BitmapShader (mBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        }
        mPaintBitmap.setShader (mShader);
        Rect clip;
        //裁剪出图片的显示区域
        if (measuredHeight > measuredWidth){
            //显示区域竖直
            if (width > height){
                //图片是横屏的，取正中间显示
                clip = new Rect (width/4,0,width*3/4,height);
            }else {
                //图片是竖直的，原样显示
                clip = new Rect (0,0,width, height);
            }
        }else {
            //显示区域横屏
            if (width > height){
                //图片是横屏的
                clip = new Rect (0,0,width,height);
            }else {
                //图片是竖直的
                clip = new Rect (0,0,width, 350 * width/height);
            }
        }
        Rect show = new Rect (0,0,measuredWidth,measuredHeight);
        canvas.drawBitmap (mBitmap,clip,show,mPaintBitmap);
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
}
