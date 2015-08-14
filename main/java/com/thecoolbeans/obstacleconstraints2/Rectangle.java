package com.thecoolbeans.obstacleconstraints2;


import android.graphics.Color;
import android.graphics.Paint;
import android.widget.TextView;

public class Rectangle{
    public float left;
    public float top;
    public float right;
    public float bottom;
    public float height;
    public Paint color = new Paint();

    public float midpointY;
    public float midpointX;

    public String type = "Unspecified";
    public String shape = "Unspecified";
    public String isa = "Unspecified";
    public String name;

    public TextView numberLabel;

    public Rectangle(){
        color.setAntiAlias(true);
        color.setStrokeWidth(5f);
        color.setColor(Color.BLACK);
        color.setStyle(Paint.Style.STROKE);
        color.setStrokeJoin(Paint.Join.ROUND);
    }
}
