package com.android.launcher3.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;

/**
 * Created by 25077 on 2019/2/6.
 */

public class IconUtilities {
    /**
     * 判断图标是否为方的
     * @param bmp
     * @return
     */
    public static boolean isSquare(Bitmap bmp){
        int width = bmp.getWidth();//图片宽度
        int height = bmp.getHeight();//图片高度
        int color_1=bmp.getPixel(width/8, height/8);//左上角
        int color_2=bmp.getPixel(width/8*7, height/8);//右上角
        int color_3=bmp.getPixel(width/8*7, height/8*7);//右下角
        int color_4=bmp.getPixel(width/8, height/8*7);//左下角
        int alpha_1 = Color.alpha(color_1);
        int alpha_2 = Color.alpha(color_1);
        int alpha_3 = Color.alpha(color_1);
        int alpha_4 = Color.alpha(color_1);
        if(alpha_1==0||alpha_2==0||alpha_3==0||alpha_4==0){
            return false;
        }
        return true;
    }

    /**
     * 返回图片顶部中间往下第一个非透明像素。
     * @param bmp
     * @return
     */
    public static IconPoint getOpaque(Bitmap bmp){
        for(int i=1;i<165;i++){
            int color=bmp.getPixel(bmp.getWidth()/2, i);
            int alpha = Color.alpha(color);
            if(alpha!=0){
                IconPoint point=new IconPoint();
                point.color=color;
                point.hight=i;
                return point;
            }
        }
        return null;
    }
    /**
     * 合并两张bitmap为一张
     * @param foreground
     * @return Bitmap
     */
    public static Bitmap combineBitmap( Bitmap foreground) {
        Bitmap background = Bitmap.createBitmap(165, 165, Bitmap.Config.ARGB_8888);
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        int fgWidth = foreground.getWidth();
        int fgHeight = foreground.getHeight();
        Bitmap newmap = Bitmap
                .createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newmap);
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawBitmap(foreground, (bgWidth - fgWidth) / 2,
                (bgHeight - fgHeight) / 2, null);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return newmap;
    }
    public static Bitmap IconScale(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        // 设置想要的大小
        int newWidth = 149;
        int newHeight = 149;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap mbitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
        return mbitmap;
    }


    public static Bitmap IconFormat(Bitmap bmp){
        if(isSquare(bmp)){
            if(getOpaque(bmp).hight<5) {
                bmp = IconScale(bmp);
                bmp = combineBitmap(bmp);
            }
        }
        return bmp;
    }
    static class IconPoint{
        int hight=0;
        int color=0;
    }
}
