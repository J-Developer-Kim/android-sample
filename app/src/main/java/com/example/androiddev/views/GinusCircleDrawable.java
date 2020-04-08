package com.example.androiddev.views;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class GinusCircleDrawable extends Drawable {
    private final Paint o_paint;
    private       int   i_Radius = 0;

    public GinusCircleDrawable(final int color) {
        this.o_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.o_paint.setColor(color);
    }
    public void setColor(final int color){
        this.o_paint.setColor(color);
    }

    @Override
    public void draw(final Canvas canvas) {
        final Rect bounds = getBounds();
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), i_Radius, o_paint);
    }

    @Override
    protected void onBoundsChange(final Rect bounds) {
        super.onBoundsChange(bounds);
        i_Radius = Math.min(bounds.width(), bounds.height()) / 2;
    }

    @Override
    public void setAlpha(final int alpha) {
        o_paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(final ColorFilter cf) {
        o_paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}