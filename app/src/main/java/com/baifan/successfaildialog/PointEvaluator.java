package com.baifan.successfaildialog;

import android.animation.TypeEvaluator;
import android.graphics.Point;

/**
 * Created by baifan on 16/5/12.
 */
public class PointEvaluator implements TypeEvaluator<Point> {
    @Override
    public Point evaluate(float fraction, Point startValue, Point endValue) {
        int startX = startValue.x;
        int startY = startValue.y;
        int endX = endValue.x;
        int endY = endValue.y;
        Point currentPoint = new Point();
        int currentX = (int) (startX + fraction * (endX - startX));
        int currentY = (int) (startY + fraction * (endY - startY));
        currentPoint.x = currentX;
        currentPoint.y = currentY;
        return currentPoint;
    }
}
