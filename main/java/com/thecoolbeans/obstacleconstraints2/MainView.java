package com.thecoolbeans.obstacleconstraints2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MainView extends View {
    //paint variable is the paint used to draw
    public Paint paint = new Paint();
    private Paint red = new Paint();
    public Paint green = new Paint();
    private Paint point = new Paint();
    public Paint black = new Paint();

    //paths that store the respective shape being drawn
    private Path restrictedPath = new Path();
    private Path rectPath = new Path();
    private Path circlePath = new Path();
    private Path ovalPath = new Path();

    //Tutorial Flags
    public boolean rectShown = false;
    public boolean ovalShown = false;
    public boolean circleShown = false;

    private Bitmap bitmap;
    public Canvas drawCanvas;

    //stores the number of rows/columns that will be made in the initial grid
    public int grid_width;
    public int grid_height;

    //variables needed for the dialogs that allow the user to indicate a shape's properties
    private final AlertDialog.Builder rectangleProperties = new AlertDialog.Builder(getContext());
    private final AlertDialog.Builder circleProperties = new AlertDialog.Builder(getContext());
    private final AlertDialog.Builder ovalProperties = new AlertDialog.Builder(getContext());
    private final AlertDialog rp; //(rectangleproperties)
    private final AlertDialog cp;//(circleproperties)
    private final AlertDialog op; //(ovalproperties)
    private final EditText rectanglelength;
    private final EditText rectanglewidth;
    private final EditText rectangleheight;
    private final EditText circleradius;
    private final EditText circleheight;
    private CheckBox rectangle_isMoveable;
    private CheckBox circle_isMoveable;
    private CheckBox oval_isMoveable;
    private RadioButton rectangle_blockRadioButton;
    private RadioButton rectangle_tableRadioButton;
    private RadioButton circle_blockRadioButton;
    private RadioButton circle_tableRadioButton;
    private RadioButton oval_blockRadioButton;
    private RadioButton oval_tableRadioButton;
    private EditText oval_horizAxis;
    private EditText oval_vertAxis;
    private EditText oval_height;
    private DecimalFormat twodecimals = new DecimalFormat("#.##");

    //Circle Stuff
    public boolean isCircle = false;
    public boolean isCenter = true;
    public boolean circleDrawn = false;
    public List<Float> xCord = new ArrayList<Float>();
    public List<Float> yCord = new ArrayList<Float>();
    private float radius;
    public List<Circle> circles = new ArrayList<Circle>();
    public List<Circle> finalCircles = new ArrayList<Circle>();
    private Circle circle = new Circle();
    private Circle finalCircle = new Circle();
    public boolean circle_moveToFinalPos;
    private float xCenter;
    private float yCenter;

    //Rectangle Stuff
    public boolean isRectangle = false;
    public boolean rectDrawn = false;
    public List<Float> xRect = new ArrayList<Float>();
    public List<Float> yRect = new ArrayList<Float>();
    private float leftSide;
    private float topSide;
    private float rightSide;
    private float bottomSide;
    public List<Rectangle> initialRectangles = new ArrayList<Rectangle>();
    public List<Rectangle> finalRectangles = new ArrayList<Rectangle>();
    public Rectangle rectangle = new Rectangle();
    private Rectangle finalRectangle = new Rectangle();
    public boolean rectangle_moveToFinalPos = false;
    private int blockCount = 1;
    private int tableCount = 1;

    //Oval Stuff
    public Oval oval = new Oval();
    public Oval finalOval = new Oval();
    public boolean isOval = false;
    public boolean ovalDrawn = false;
    public List<Float> xOval = new ArrayList<Float>();
    public List<Float> yOval = new ArrayList<Float>();
    private float leftBound;
    private float topBound;
    private float rightBound;
    private float bottomBound;
    public List<Oval> initialOvals = new ArrayList<Oval>();
    public List<Oval> finalOvals = new ArrayList<Oval>();
    public boolean oval_moveToFinalPos = false;

    //Stores the maximum number of pixels horizontally and vertically
    public float xMax;
    public float yMax;

    //Drawing the square grid layout
    public boolean isRestricted = true;
    public boolean isDimensionsSet = false;
    public List<ShapeDrawable> gridSquares = new ArrayList<ShapeDrawable>();

    //Set shape property booleans
    public boolean currentCircle = false;
    public boolean currentRectangle = false;
    public boolean currentOval = false;

    //Holds real world room dimensions
    public float roomWidth;
    public float roomHeight;
    public float lengthScale; //the scale of how many meters each pixel is vertically (total length / total pixels)
    public float widthScale; //"" width

    //robot
    public boolean isRobot = false;
    public Rectangle robotRect = new Rectangle();

    //textbox that contains the number reference of an object
    public int objectCount = 0;

    //table and block button on shape properties menu
    private RadioGroup radgroup;

    public MainView(Context context, AttributeSet attrs){
        super(context, attrs);

        //Sets the characteristics for each paint color (brush size, brush type, color)
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5f);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);

        point.setAntiAlias(true);
        point.setStrokeWidth(20);
        point.setColor(Color.BLACK);
        point.setStyle(Paint.Style.STROKE);
        point.setStrokeJoin(Paint.Join.ROUND);

        red.setColor(Color.RED);
        red.setStyle(Paint.Style.FILL);

        black.setColor(Color.BLACK);
        black.setStyle(Paint.Style.FILL);

        xMax = 20;
        yMax = 20;

        green.setColor(0xff16b610);
        green.setStyle(Paint.Style.FILL);


        bitmap = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(bitmap);

        LayoutInflater linf = LayoutInflater.from(getContext());
        final View rectangleView = linf.inflate(R.layout.objectdimensions, null);
        rectangleProperties.setView(rectangleView);

        LayoutInflater linf2 = LayoutInflater.from(getContext());
        final View circleView = linf2.inflate(R.layout.circledimensions, null);
        circleProperties.setView(circleView);

        LayoutInflater linf3 = LayoutInflater.from(getContext());
        final View ovalView = linf3.inflate(R.layout.ovaldimensions, null);
        ovalProperties.setView(ovalView);

        //INITIALIZING VARIABLES from each shape's respective shapeDimension view
        rectanglelength = (EditText)rectangleView.findViewById(R.id.objectlengthinput);
        rectanglewidth = (EditText)rectangleView.findViewById(R.id.objectwidthinput);
        rectangleheight = (EditText)rectangleView.findViewById(R.id.objectheightinput);
        rectangle_isMoveable = (CheckBox)rectangleView.findViewById(R.id.moveable);
        rectangle_blockRadioButton = (RadioButton)rectangleView.findViewById(R.id.blockbutton);
        rectangle_tableRadioButton = (RadioButton)rectangleView.findViewById(R.id.tablebutton);

        circle_blockRadioButton = (RadioButton)circleView.findViewById(R.id.blockbutton);
        circle_tableRadioButton = (RadioButton)circleView.findViewById(R.id.tablebutton);
        circle_isMoveable = (CheckBox)circleView.findViewById(R.id.moveable);
        circleradius = (EditText)circleView.findViewById(R.id.objectradiusinput);
        circleheight = (EditText)circleView.findViewById(R.id.objectheightinput);

        oval_horizAxis = (EditText)ovalView.findViewById(R.id.objectlengthinput);
        oval_vertAxis = (EditText)ovalView.findViewById(R.id.objectwidthinput);
        oval_height = (EditText)ovalView.findViewById(R.id.objectheightinput);
        oval_blockRadioButton = (RadioButton)ovalView.findViewById(R.id.blockbutton);
        oval_tableRadioButton = (RadioButton)ovalView.findViewById(R.id.tablebutton);
        oval_isMoveable = (CheckBox)ovalView.findViewById(R.id.moveable);

        rectangle_blockRadioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                rectangle_isMoveable.setVisibility(View.VISIBLE);
            }
        });
        rectangle_tableRadioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                rectangle_isMoveable.setVisibility(View.INVISIBLE);
            }
        });
        circle_blockRadioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                circle_isMoveable.setVisibility(View.VISIBLE);
            }
        });
        circle_tableRadioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                circle_isMoveable.setVisibility(View.INVISIBLE);
            }
        });
        oval_blockRadioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                oval_isMoveable.setVisibility(View.VISIBLE);
            }
        });
        oval_tableRadioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                oval_isMoveable.setVisibility(View.INVISIBLE);
            }
        });

        DialogInterface.OnClickListener doneShapeProperties = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Doesn't do anything because this method is overrided from the onShowListener later.
            }
        };

        rectangleProperties.setPositiveButton("Done", doneShapeProperties);
        circleProperties.setPositiveButton("Done",doneShapeProperties);
        ovalProperties.setPositiveButton("Done",doneShapeProperties);

        rp = rectangleProperties.create();
        cp = circleProperties.create();
        op = ovalProperties.create();

        //OnShowListener followed by onClickListener is used to prevent the shapeProperties dialog from closing
        //after the user presses the POSITIVE BUTTON (in case the user did not fill out a segment, an error will appear)
        rp.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button doneButton = rp.getButton(AlertDialog.BUTTON_POSITIVE);
                doneButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        radgroup = (RadioGroup)rectangleView.findViewById(R.id.radiogroup);
                        int selectedId = radgroup.getCheckedRadioButtonId();
                        TextView errorNotification = (TextView) rectangleView.findViewById(R.id.required_object_type);
                        //If neither table nor block is selected, show the error message
                        if (selectedId == -1) {
                            errorNotification.setVisibility(View.VISIBLE);
                        }
                        else if(currentRectangle){
                            errorNotification.setVisibility(View.INVISIBLE);
                            float length = Float.parseFloat(rectanglelength.getText().toString());
                            float width = Float.parseFloat(rectanglewidth.getText().toString());
                            float height = Float.parseFloat(rectangleheight.getText().toString());
                            length /= 2;
                            width /= 2;

                            //adjust the rectangle's dimensions to whatever the user specifies
                            rectangle.left = rectangle.midpointX - (width/widthScale);
                            rectangle.right = rectangle.midpointX + (width/widthScale);
                            rectangle.top = rectangle.midpointY - (length/lengthScale);
                            rectangle.bottom = rectangle.midpointY + (length/lengthScale);
                            rectangle.height = height;
                            currentRectangle = false;

                            switch(selectedId){
                                case R.id.blockbutton:
                                    rectangle.color.setStyle(Paint.Style.FILL);
                                    rectangle.type = "Block";
                                    rectangle.name = "Block" + blockCount;
                                    blockCount++;
                                    rectangle.shape = "Rectangle";
                                    rectangle.isa = "Block";
                                    break;
                                case R.id.tablebutton:
                                    rectangle.color.setStyle(Paint.Style.FILL);
                                    rectangle.type = "Table";
                                    rectangle.name = "Table" + tableCount;
                                    tableCount++;
                                    rectangle.shape = "Rectangle";
                                    rectangle.isa = "Table";
                                    rectangle_isMoveable.setChecked(false);
                                    break;
                            }
                            //if the user designates the block as being moveable, create a new instance of a rectangle for the user to move.
                            if (rectangle_isMoveable.isChecked()){
                                Toast.makeText(getContext(), "Drag where you want to move the Block", Toast.LENGTH_LONG).show();
                                rectangle_moveToFinalPos = true;
                                isRectangle = false;
                            }
                            //if the rectangle isn't moveable, then the rectangle is represented the same in the Initial and Final scene files
                            else
                                finalRectangles.add(rectangle);

                            invalidate();
                            rp.dismiss();
                            rp.cancel();

                        }
                    }
                });
            }
        });

        cp.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button doneButton = cp.getButton(AlertDialog.BUTTON_POSITIVE);
                doneButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        radgroup = (RadioGroup) circleView.findViewById(R.id.radiogroup);
                        int selectedId = radgroup.getCheckedRadioButtonId();
                        TextView errorNotification = (TextView) circleView.findViewById(R.id.required_object_type);

                        //if neither table nor block is selected, show the error message
                        if (selectedId == -1) {
                            errorNotification.setVisibility(View.VISIBLE);
                        }
                        else if (currentCircle) {
                            errorNotification.setVisibility(View.INVISIBLE);
                            float radius = Float.parseFloat(circleradius.getText().toString());
                            float height = Float.parseFloat(circleheight.getText().toString());

                            //adjust the circle's dimensions to whatever the user specifies
                            circle.radius = radius / lengthScale;
                            circle.height = height;
                            currentRectangle = false;

                            switch (selectedId) {
                                case R.id.blockbutton:
                                    circle.color.setStyle(Paint.Style.FILL);
                                    circle.type = "Block";
                                    circle.name = "Block" + blockCount;
                                    blockCount++;
                                    circle.shape = "Circle";
                                    circle.isa = "Block";
                                    break;
                                case R.id.tablebutton:
                                    circle.color.setStyle(Paint.Style.FILL);
                                    circle.type = "Table";
                                    circle.name = "Table" + tableCount;
                                    tableCount++;
                                    circle.shape = "Circle";
                                    circle.isa = "Table";
                                    circle_isMoveable.setChecked(false);
                                    break;
                            }

                            //if the circle is moveable, create a separate instance of the circle for the user to move
                            if (circle_isMoveable.isChecked()) {
                                Toast.makeText(getContext(), "Drag where you want to move the Block", Toast.LENGTH_LONG).show();
                                circle_moveToFinalPos = true;
                                isCircle = false;
                            }
                            //if the circle isn't moveable, then it's represented the same in both the initial and final scene files
                            else
                                finalCircles.add(circle);

                            invalidate();
                            cp.dismiss();
                            cp.cancel();

                        }
                    }
                });
            }
        });

        op.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button doneButton = op.getButton(AlertDialog.BUTTON_POSITIVE);
                doneButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        radgroup = (RadioGroup)ovalView.findViewById(R.id.radiogroup);
                        int selectedId = radgroup.getCheckedRadioButtonId();
                        TextView errorNotification = (TextView) ovalView.findViewById(R.id.required_object_type);
                        //if neither table nor block is selected, then show the error message
                        if (selectedId == -1) {
                            errorNotification.setVisibility(View.VISIBLE);
                        }
                        else if(currentOval){
                            errorNotification.setVisibility(View.INVISIBLE);
                            float horizAxis = Float.parseFloat(oval_horizAxis.getText().toString());
                            float vertAxis = Float.parseFloat(oval_vertAxis.getText().toString());
                            float height = Float.parseFloat(oval_height.getText().toString());

                            horizAxis /= 2;
                            vertAxis /=2;
                            //change the oval's dimensions to reflect whatever the user specifies
                            oval.left = oval.CenterX - (horizAxis/widthScale);
                            oval.right = oval.CenterX + (horizAxis/widthScale);
                            oval.top = oval.CenterY - (vertAxis/lengthScale);
                            oval.bottom = oval.CenterY + (vertAxis/lengthScale);
                            oval.height = height;
                            currentOval = false;

                            switch(selectedId){
                                case R.id.blockbutton:
                                    oval.color.setStyle(Paint.Style.FILL);
                                    oval.type = "Block";
                                    oval.name = "Block" + blockCount;
                                    blockCount++;
                                    oval.shape = "Oval";
                                    oval.isa = "Block";
                                    break;
                                case R.id.tablebutton:
                                    oval.color.setStyle(Paint.Style.FILL);
                                    oval.type = "Table";
                                    oval.name = "Table" + tableCount;
                                    tableCount++;
                                    oval.shape = "Rectangle";
                                    oval.isa = "Table";
                                    oval_isMoveable.setChecked(false);
                                    break;
                            }

                            //if the oval is moveable, then create a new instance of the oval for the user to move.
                            if (oval_isMoveable.isChecked()){
                                Toast.makeText(getContext(), "Drag where you want to move the Block", Toast.LENGTH_LONG).show();
                                oval_moveToFinalPos = true;
                                isOval = false;
                            }
                            else
                            //if the oval is not moveable, then it is represented the same in both the initial and final scene files
                                finalOvals.add(oval);

                            invalidate();
                            op.dismiss();
                            op.cancel();

                        }
                    }
                });
            }
        });

        //Buttons that change a shape's color
        Button rectangle_purpleButton = (Button)rectangleView.findViewById(R.id.purple_button);
        rectangle_purpleButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        if (isRectangle)
                            rectangle.color.setColor(0xffdf3dff);
                    }
                }
        );
        Button circle_purpleButton = (Button)circleView.findViewById(R.id.purple_button);
        circle_purpleButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        if (isCircle)
                            circle.color.setColor(0xffdf3dff);
                    }
                }
        );
        Button rectangle_greenButton = (Button)rectangleView.findViewById(R.id.green_button);
        rectangle_greenButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        if (isRectangle)
                            rectangle.color.setColor(Color.GREEN);
                    }
                }
        );
        Button circle_greenButton = (Button)circleView.findViewById(R.id.green_button);
        circle_greenButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        if (isCircle)
                            circle.color.setColor(Color.GREEN);
                    }
                }
        );
        Button rectangle_blueButton = (Button)rectangleView.findViewById(R.id.blue_button);
        rectangle_blueButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        if (isRectangle)
                            rectangle.color.setColor(0xff000dff);
                    }
                }
        );
        Button circle_blueButton = (Button)circleView.findViewById(R.id.blue_button);
        circle_blueButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        if (isCircle)
                            circle.color.setColor(0xff000dff);
                    }
                }
        );
        Button oval_purpleButton = (Button)ovalView.findViewById(R.id.purple_button);
        oval_purpleButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        if (isOval)
                            oval.color.setColor(0xffdf3dff);
                    }
                }
        );
        Button oval_greenButton = (Button)ovalView.findViewById(R.id.green_button);
        oval_greenButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        if (isOval)
                            oval.color.setColor(Color.GREEN);
                    }
                }
        );
        Button oval_blueButton = (Button)ovalView.findViewById(R.id.blue_button);
        oval_blueButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        if (isOval)
                            oval.color.setColor(0xff000dff);
                    }
                }
        );
    }

    @Override
    protected void onDraw(Canvas canvas){
        //need the dimensions of the canvas so that this app can work on all screens of various sizes.
        if (!isDimensionsSet){
            xMax = canvas.getWidth();
            yMax = canvas.getHeight();
            bitmap = Bitmap.createBitmap((int)xMax, (int)yMax, Bitmap.Config.ARGB_8888);
            drawCanvas = new Canvas(bitmap);
            isDimensionsSet = true;
        }
        canvas.drawBitmap(bitmap, 0, 0, paint);

        if (isCircle)
            canvas.drawPoint(xCenter, yCenter, point);
        //paths that tentatively contain a shape's outline before it's corrected
        canvas.drawPath(circlePath, paint);
        canvas.drawPath(ovalPath, paint);
        canvas.drawPath(rectPath, paint);

        if(circleDrawn){
            for (Circle circle: finalCircles)
                canvas.drawCircle(circle.CenterX, circle.CenterY, circle.radius, circle.color);
            for(Circle circle: circles)
                canvas.drawCircle(circle.CenterX, circle.CenterY, circle.radius, circle.color);
        }
        if (rectDrawn){
            for (Rectangle rectangle: finalRectangles)
                canvas.drawRect(rectangle.left, rectangle.top, rectangle.right, rectangle.bottom, rectangle.color);
            for (Rectangle rectangle: initialRectangles)
                canvas.drawRect(rectangle.left, rectangle.top, rectangle.right, rectangle.bottom, rectangle.color);
        }
        if (ovalDrawn){
            for (Oval oval: finalOvals)
                canvas.drawOval(oval.left, oval.top, oval.right, oval.bottom, oval.color);
            for (Oval oval: initialOvals)
                canvas.drawOval(oval.left, oval.top, oval.right, oval.bottom, oval.color);
         }

        //draws the "moveto" shapes while the user is dragging it across the screen
        if(rectangle_moveToFinalPos)
            canvas.drawRect(finalRectangle.left, finalRectangle.top, finalRectangle.right, finalRectangle.bottom, finalRectangle.color);
        if (circle_moveToFinalPos)
            canvas.drawCircle(finalCircle.CenterX, finalCircle.CenterY, finalCircle.radius, finalCircle.color);
        if (oval_moveToFinalPos)
            canvas.drawOval(finalOval.left, finalOval.top, finalOval.right, finalOval.bottom, finalOval.color);
        //draws the robot square for the simulation of the received solution
        if (isRobot) {
            canvas.drawRect(robotRect.left, robotRect.top, robotRect.right, robotRect.bottom, black);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (isRestricted) //startup when user designates which areas are inaccessible
                    restrictedPath.moveTo(eventX, eventY);
                //circles have two parts: the part where the user designates the center, and the part where user draws the circle's outline
                else if (isCircle) {
                    if (isCenter) {
                        circle = new Circle();
                        circle.setCenter((int) eventX, (int) eventY);
                        xCenter = eventX;
                        yCenter = eventY;
                        objectCount++;
                        circle.numberLabel = new TextView(getContext());
                    }
                    else
                        circlePath.moveTo(eventX, eventY);
                }
                else if (isRectangle){
                    rectangle = new Rectangle();
                    rectPath.moveTo(eventX, eventY);
                    xRect.add(eventX);
                    yRect.add(eventY);

                    objectCount++;
                    rectangle.numberLabel = new TextView(getContext());
                }
                else if (isOval){
                    oval = new Oval();
                    ovalPath.moveTo(eventX, eventY);
                    xOval.add(eventX);
                    yOval.add(eventY);
                    oval.numberLabel = new TextView(getContext());
                    objectCount++;
                }
                else if (rectangle_moveToFinalPos){
                    //basically creates a copy of its parent rectangle
                    finalRectangle = new Rectangle();
                    finalRectangle.name = rectangle.name;
                    finalRectangle.left = eventX - ((rectangle.right-rectangle.left)/2);
                    finalRectangle.right = eventX + ((rectangle.right-rectangle.left)/2);
                    finalRectangle.top = eventY - ((rectangle.bottom-rectangle.top)/2);
                    finalRectangle.bottom = eventY + ((rectangle.bottom-rectangle.top)/2);
                    finalRectangle.height = rectangle.height;
                    finalRectangle.midpointX = (finalRectangle.right + finalRectangle.left)/2;
                    finalRectangle.midpointY = (finalRectangle.top + finalRectangle.bottom)/2;
                    finalRectangle.color.setColor(rectangle.color.getColor());
                    finalRectangle.color.setStyle(Paint.Style.FILL);
                    finalRectangle.color.setAlpha(80);
                    finalRectangle.type = "Block";
                    finalRectangle.shape = "Rectangle";
                    finalRectangle.isa = "Block";
                }
                else if (circle_moveToFinalPos){
                    //creates copy of its parent circle
                    finalCircle = new Circle();
                    finalCircle.name = circle.name;
                    finalCircle.radius = circle.radius;
                    finalCircle.height = circle.height;
                    finalCircle.color.setColor(circle.color.getColor());
                    finalCircle.color.setStyle(Paint.Style.FILL);
                    finalCircle.color.setAlpha(80);
                    finalCircle.type = "Block";
                    finalCircle.isa = circle.isa;
                }
                else if (oval_moveToFinalPos){
                    //creates copy of its parent oval
                    finalOval = new Oval();
                    finalOval.name = oval.name;
                    finalOval.left = oval.left;
                    finalOval.top = oval.top;
                    finalOval.right = oval.right;
                    finalOval.bottom = oval.bottom;
                    finalOval.color.setColor(oval.color.getColor());
                    finalOval.color.setStyle(Paint.Style.FILL);
                    finalOval.color.setAlpha(80);
                    finalOval.type = "Block";
                    finalOval.isa = oval.isa;
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                //changes the grid squares to red if the user marks it as being inaccessible
                if (isRestricted){
                    restrictedPath.lineTo(eventX, eventY);
                    for(int count1 = 1; count1 < (grid_height+1); count1++){
                        if (eventY < (count1*yMax/grid_height)){
                            for(int count2 = 1; count2<(grid_width+1);count2++){
                                if(eventX < (count2*xMax/grid_width)){
                                    int index = ((count1-1)*grid_width)+(count2-1);
                                    ShapeDrawable square = gridSquares.get(index);
                                    square.getPaint().set(red);
                                    square.draw(drawCanvas);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }

                else if (isCircle) {
                    circlePath.lineTo(eventX, eventY);
                    xCord.add(eventX);
                    yCord.add(eventY);
                }
                else if (isRectangle){
                    rectPath.lineTo(eventX, eventY);
                    xRect.add(eventX);
                    yRect.add(eventY);
                }
                else if (isOval){
                    ovalPath.lineTo(eventX, eventY);
                    xOval.add(eventX);
                    yOval.add(eventY);
                }

                //the following three if statements allows the application to constantly update the shapes as the user drags their finger
                //this gives the appearance of the user dragging the shapes across the screen
                else if (rectangle_moveToFinalPos) {
                    finalRectangle.left = eventX - ((rectangle.right - rectangle.left) / 2);
                    finalRectangle.right = eventX + ((rectangle.right - rectangle.left) / 2);
                    finalRectangle.top = eventY - ((rectangle.bottom - rectangle.top) / 2);
                    finalRectangle.bottom = eventY + ((rectangle.bottom - rectangle.top) / 2);
                }
                else if (circle_moveToFinalPos){
                    finalCircle.setCenter((int)eventX, (int)eventY);
                }
                else if (oval_moveToFinalPos){
                    finalOval.left = eventX - ((oval.right - oval.left) / 2);
                    finalOval.right = eventX + ((oval.right - oval.left) / 2);
                    finalOval.top = eventY - ((oval.bottom - oval.top) / 2);
                    finalOval.bottom = eventY + ((oval.bottom - oval.top) / 2);
                    finalOval.CenterX = (finalOval.left + finalOval.right) / 2;
                    finalOval.CenterY = (finalOval.top + finalOval.bottom) / 2;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (isCircle) {
                    if (isCenter)
                        isCenter = false;
                    else {
                        //dimensions of the circle are found by averaging the distance between the points the user draws and the center
                        radius = averageDistance(xCord, yCord);
                        circle.setRadius(radius);
                        circles.add(circle);
                        circleDrawn = true;
                        circlePath.reset();
                        xCord.clear();
                        yCord.clear();
                        isCenter = true;
                        currentCircle = true;

                        circleradius.setText(""+twodecimals.format((circle.radius*lengthScale)));
                        circleheight.setText("0");

                        //sets up the shape numbering system
                        circle.numberLabel.setText("" + objectCount);
                        circle.numberLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float)(circle.radius));
                        circle.numberLabel.setTextColor(Color.WHITE);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins((int) (circle.CenterX-circle.radius), (int) (circle.CenterY-circle.radius), (int) (circle.CenterX+circle.radius), (int) (circle.CenterY + circle.radius));
                        circle.numberLabel.setLayoutParams(layoutParams);
                        circle.numberLabel.setWidth((int) (circle.radius * 2));
                        circle.numberLabel.setHeight((int) (circle.radius * 2));
                        circle.numberLabel.setGravity(Gravity.CENTER);
                        MainActivity.drawingLayout.addView(circle.numberLabel);

                        cp.show();
                    }
                }
                else if (isRectangle){
                    //the sides of the rectangle are given by the extreme coordinates that the user draws
                    leftSide = Collections.min(xRect);
                    topSide = Collections.min(yRect);
                    rightSide = Collections.max(xRect);
                    bottomSide = Collections.max(yRect);

                    rectangle.left = leftSide;
                    rectangle.top = topSide;
                    rectangle.right = rightSide;
                    rectangle.bottom = bottomSide;

                    rectanglelength.setText("" + twodecimals.format((rectangle.bottom - rectangle.top) * lengthScale));
                    rectanglewidth.setText("" + twodecimals.format((rectangle.right - rectangle.left) * widthScale));
                    rectangleheight.setText("0");

                    rp.show();

                    rectangle.midpointX = (rightSide + leftSide) / 2;
                    rectangle.midpointY = (topSide + bottomSide) / 2;

                    initialRectangles.add(rectangle);

                    rectDrawn = true;
                    currentRectangle = true;
                    rectPath.reset();

                    //sets up the shape numbering system
                    rectangle.numberLabel.setText("" + objectCount);
                    rectangle.numberLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) ((rectangle.bottom - rectangle.top) / 1.5));
                    rectangle.numberLabel.setTextColor(Color.WHITE);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins((int) rectangle.left, (int) rectangle.top, (int) (xMax - rectangle.right), (int) (yMax - rectangle.bottom));
                    rectangle.numberLabel.setLayoutParams(layoutParams);
                    rectangle.numberLabel.setWidth((int) (rectangle.right - rectangle.left));
                    rectangle.numberLabel.setHeight((int) (rectangle.bottom - rectangle.top));
                    rectangle.numberLabel.setGravity(Gravity.CENTER);
                    MainActivity.drawingLayout.addView(rectangle.numberLabel);

                    xRect.clear();
                    yRect.clear();

                }
                else if (isOval){
                    //the boundaries of the oval are given by the extreme coordinates that the user draws
                    leftBound = Collections.min(xOval);
                    topBound = Collections.min(yOval);
                    rightBound = Collections.max(xOval);
                    bottomBound = Collections.max(yOval);

                    oval.left = leftBound;
                    oval.top = topBound;
                    oval.right = rightBound;
                    oval.bottom = bottomBound;

                    oval_horizAxis.setText(""+twodecimals.format((oval.right-oval.left)*widthScale));
                    oval_vertAxis.setText(""+twodecimals.format((oval.bottom-oval.top)*lengthScale));
                    oval_height.setText("0");

                    op.show();
                    initialOvals.add(oval);

                    //calculates where the center of the oval is
                    oval.CenterX = (leftBound + rightBound) / 2;
                    oval.CenterY = (topBound + bottomBound) / 2;
                    ovalDrawn = true;
                    ovalPath.reset();

                    xOval.clear();
                    yOval.clear();

                    //sets up the shape numbering system
                    oval.numberLabel.setText("" + objectCount);
                    oval.numberLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) ((oval.bottom - oval.top) / 1.5));
                    oval.numberLabel.setTextColor(Color.WHITE);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins((int) oval.left, (int)oval.top, (int) (xMax - oval.right), (int) (yMax - oval.bottom));
                    oval.numberLabel.setLayoutParams(layoutParams);
                    oval.numberLabel.setWidth((int) (oval.right - oval.left));
                    oval.numberLabel.setHeight((int) (oval.bottom - oval.top));
                    oval.numberLabel.setGravity(Gravity.CENTER);
                    MainActivity.drawingLayout.addView(oval.numberLabel);

                    currentOval = true;
                }

                else if (rectangle_moveToFinalPos) {
                    finalRectangles.add(finalRectangle);
                    rectangle_moveToFinalPos = false;
                    isRectangle = true;

                    finalRectangle.numberLabel = new TextView(getContext());
                    finalRectangle.numberLabel.setText("" + objectCount);
                    finalRectangle.numberLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) ((finalRectangle.bottom - finalRectangle.top) / 1.5));
                    finalRectangle.numberLabel.setTextColor(Color.WHITE);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins((int) finalRectangle.left, (int) finalRectangle.top, (int) (xMax - finalRectangle.right), (int) (yMax - finalRectangle.bottom));
                    finalRectangle.numberLabel.setLayoutParams(layoutParams);
                    finalRectangle.numberLabel.setWidth((int) (finalRectangle.right - finalRectangle.left));
                    finalRectangle.numberLabel.setHeight((int) (finalRectangle.bottom - finalRectangle.top));
                    finalRectangle.numberLabel.setGravity(Gravity.CENTER);
                    finalRectangle.numberLabel.setAlpha(80);
                    MainActivity.drawingLayout.addView(finalRectangle.numberLabel);
                }
                else if (circle_moveToFinalPos){
                    finalCircles.add(finalCircle);
                    circle_moveToFinalPos = false;
                    isCircle = true;

                    finalCircle.numberLabel = new TextView(getContext());
                    finalCircle.numberLabel.setText("" + objectCount);
                    finalCircle.numberLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (finalCircle.radius));
                    finalCircle.numberLabel.setTextColor(Color.WHITE);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins((int) (finalCircle.CenterX-finalCircle.radius), (int) (finalCircle.CenterY-finalCircle.radius), (int) (finalCircle.CenterX+finalCircle.radius), (int) (finalCircle.CenterY + finalCircle.radius));
                    finalCircle.numberLabel.setLayoutParams(layoutParams);
                    finalCircle.numberLabel.setWidth((int) (finalCircle.radius * 2));
                    finalCircle.numberLabel.setHeight((int) (finalCircle.radius * 2));
                    finalCircle.numberLabel.setGravity(Gravity.CENTER);
                    finalCircle.numberLabel.setAlpha(80);
                    MainActivity.drawingLayout.addView(finalCircle.numberLabel);
                }
                else if (oval_moveToFinalPos) {
                    finalOvals.add(finalOval);
                    oval_moveToFinalPos = false;
                    isOval = true;

                    finalOval.numberLabel = new TextView(getContext());
                    finalOval.numberLabel.setText("" + objectCount);
                    finalOval.numberLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) ((finalOval.bottom - finalOval.top) / 1.5));
                    finalOval.numberLabel.setTextColor(Color.WHITE);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins((int) finalOval.left, (int) finalOval.top, (int) (xMax - finalOval.right), (int) (yMax - finalOval.bottom));
                    finalOval.numberLabel.setLayoutParams(layoutParams);
                    finalOval.numberLabel.setWidth((int) (finalOval.right - finalOval.left));
                    finalOval.numberLabel.setHeight((int) (finalOval.bottom - finalOval.top));
                    finalOval.numberLabel.setGravity(Gravity.CENTER);
                    finalOval.numberLabel.setAlpha(80);
                    MainActivity.drawingLayout.addView(finalOval.numberLabel);
                }
                invalidate();
                break;

            default:
                return false;
        }
        //Makes our view repaint and call onDraw
        invalidate();
        return true;
    }

    public double findDistance(float x1, float y1, float x2, float y2){
        //Implements the basic distance formula. Used for circles.
        double xPart = x2 - x1;
        double yPart = y2 - y1;
        xPart = java.lang.Math.pow(xPart, 2.0);
        yPart = java.lang.Math.pow(yPart, 2.0);
        double distance = xPart + yPart;
        distance = java.lang.Math.sqrt(distance);
        return distance;
    }

    //finds the average distance between points surrounding center and the center
    private float averageDistance(List<Float> xVals, List<Float> yVals){
        int count = 0;
        float x;
        float y;
        float sum = 0;
        double distance;
        while (count < xVals.size()) {
            x = xVals.get(count);
            y = yVals.get(count);
            distance = findDistance(x, y, xCenter, yCenter);
            sum += distance;
            count++;
        }
        return (sum / xVals.size());
    }

    //Makes the initial grid squares
    public void makeGridSquares(int horizontal, int vertical, int maxWidth, int maxHeight){
        //horizontal = how many columns
        //vertical = how many rows
        gridSquares.clear();
        int width = maxWidth / horizontal;
        int height = maxHeight / vertical;

        for(int count1 = 0; count1 < vertical; count1++){
            for(int count2 = 0; count2 < horizontal; count2++){
                ShapeDrawable tempSquare = new ShapeDrawable(new RectShape());
                tempSquare.getPaint().set(paint);
                tempSquare.setBounds((count2 * width), (count1 * height), ((count2 + 1) * width), ((count1 + 1) * height));
                gridSquares.add(tempSquare);
            }
        }
    }

    public void moveRobotSquare(float x, float y, float z){
        //moves a square on the drawing view to illustrate the solution obtained from the web server
        robotRect.left = x - 20;
        robotRect.right = x + 20;
        robotRect.top = y - 20;
        robotRect.bottom = y + 20;
        postInvalidate();
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
