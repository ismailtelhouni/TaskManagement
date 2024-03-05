package com.example.taskmanagement.transformation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.squareup.picasso.Transformation;

public class BorderTransformation implements Transformation {

    private int borderColor;
    private int borderWidth;
    private int cornerRadius;

    public BorderTransformation(int borderColor, int borderWidth, int cornerRadius) {
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
        this.cornerRadius = cornerRadius;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();

        Bitmap result = Bitmap.createBitmap(width, height, source.getConfig());
        Canvas canvas = new Canvas(result);

        Paint borderPaint = new Paint();
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidth);

        Paint imagePaint = new Paint();
        imagePaint.setAntiAlias(true);

        canvas.drawRoundRect(new RectF(0, 0, width, height), cornerRadius, cornerRadius, imagePaint);
        canvas.drawBitmap(source, 0, 0, imagePaint);
        canvas.drawRoundRect(new RectF(0, 0, width, height), cornerRadius, cornerRadius, borderPaint);

        source.recycle();

        return result;
    }

    @Override
    public String key() {
        return "rounded_border";
    }
}
