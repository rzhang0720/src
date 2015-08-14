package com.thecoolbeans.obstacleconstraints2;

import android.graphics.Color;
import android.graphics.Paint;
import android.widget.TextView;


import java.util.List;

public class Circle{
    public Paint color = new Paint();
    public int CenterX;
    public int CenterY;
    public float radius;
    public float height;

    public String type = "Unspecified";
    public String shape = "Unspecified";
    public String isa = "Unspecified";
    public String name = "Unspecified";

    public TextView numberLabel;

    public Circle(){
        color.setAntiAlias(true);
        color.setStrokeWidth(5f);
        color.setColor(Color.BLACK);
        color.setStyle(Paint.Style.STROKE);
        color.setStrokeJoin(Paint.Join.ROUND);

        CenterX = 0;
        CenterY = 0;

        radius = 0;

    }

    public void setCenter(int x, int y){
        CenterX = x;
        CenterY = y;
    }

    public void setRadius(float number){
        radius = number;
    }

}
