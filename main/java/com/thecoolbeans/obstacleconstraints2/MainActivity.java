package com.thecoolbeans.obstacleconstraints2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    private MainView mainView;
    private FileOutputStream fileOutput;
    private FileOutputStream tasksTest;

    //contains the standard class information for the scene objects in the scene text file
    private String data = "class moveable { }\r\nclass stackable { } \r\n\r\nclass red     { color [1,0,0]; }\r\nclass green   { color [0,1,0]; }\r\nclass blue    { color [0,0,1]; }\r\n\r\n\r\nclass table {\r\n    shape box;\r\n    isa stackable;\r\n    color [.6, .3, .6];\r\n    alpha 0.5;\r\n}\r\n\r\nclass block {\r\n    shape box;\r\n    isa moveable;\r\n    color [0, 1, 0];\r\n    alpha 0.5;\r\n    dimension [.1, .1, .1];\r\n}\r\n\r\n////OBJECTS\r\n";
    private String objectsString;
    //writeToText = true means that the app has not outputted a scene file yet.
    private boolean writeToText = true;

    //tutorials that explain how to use each shape option
    private AlertDialog circTut;
    private AlertDialog rectTut;
    private AlertDialog ovalTut;

    public static RelativeLayout drawingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainView = (MainView) findViewById(R.id.drawing_canvas);

        drawingLayout = (RelativeLayout)findViewById(R.id.drawingview);

        //simply closes the dialog
        DialogInterface.OnClickListener okay = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        final AlertDialog.Builder circleTutorial = new AlertDialog.Builder(this);
        circleTutorial.setMessage("First, tap to set the center of the circle. Then, draw the tentative circle path you desire around the center.");
        circleTutorial.setPositiveButton("Okay", okay);
        circleTutorial.setTitle("Circle Tutorial");
        circTut = circleTutorial.create();

        final AlertDialog.Builder rectTutorial = new AlertDialog.Builder(this);
        rectTutorial.setMessage("In one motion (without picking your finger up) draw the tentative rectangle you would like. Pick up your finger when you are done. NOTE: All rectangles have a vertical orientation.");
        rectTutorial.setPositiveButton("Okay", okay);
        rectTutorial.setTitle("Rectangle Tutorial");
        rectTut = rectTutorial.create();


        final AlertDialog.Builder ovalTutorial = new AlertDialog.Builder(this);
        ovalTutorial.setMessage("In one motion (without picking your finger up) draw the tentative oval you would like. Pick up your finger when you are done. ");
        ovalTutorial.setPositiveButton("Okay", okay);
        ovalTutorial.setTitle("Oval Tutorial");
        ovalTut = ovalTutorial.create();

        final AlertDialog.Builder inaccessible = new AlertDialog.Builder(this);
        inaccessible.setMessage("To begin, designate which regions are inaccessible. Then, press 'Done'");
        inaccessible.setPositiveButton("Okay", okay);
        inaccessible.setTitle("Inaccessible Area Designation");

        //inflates the startup menu (which designates room/grid dimensions) so that you can reference the views in the dialog
        RelativeLayout startUpLayout = (RelativeLayout) findViewById(R.id.startupmenu);
        LayoutInflater linf = LayoutInflater.from(this);
        final View startUpView = linf.inflate(R.layout.startupmenu, startUpLayout, true);
        final AlertDialog.Builder startUpMenu = new AlertDialog.Builder(this);
        startUpMenu.setView(startUpView);
        final EditText columninput = (EditText)startUpView.findViewById(R.id.columninput);
        final EditText rowinput = (EditText) startUpView.findViewById(R.id.rowinput);
        final EditText roomheight = (EditText) startUpView.findViewById(R.id.roomheightinput);
        final EditText roomwidth = (EditText) startUpView.findViewById(R.id.roomwidthinput);

        DialogInterface.OnClickListener doneStartUp = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //This onclicklistener doesn't do anything because i just override its methods later
            }
        };
        startUpMenu.setPositiveButton("Done", doneStartUp);
        final AlertDialog startUp = startUpMenu.create();

        //using this on show listener prevents the Dialog from closing if user has not filled out room dimensions
        startUp.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                //reprogramming what the positive button in the AlertDialog does (so that it doesn't close when you press it)
                Button doneButton = startUp.getButton(AlertDialog.BUTTON_POSITIVE);
                doneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //if the dimensions aren't filled in, the app doesn't allow the user continue. shows an error notification
                        TextView errorNotification1 = (TextView) startUpView.findViewById(R.id.required_grid_column);
                        TextView errorNotification2 = (TextView) startUpView.findViewById(R.id.required_grid_row);
                        TextView errorNotification3 = (TextView) startUpView.findViewById(R.id.required_room_height);
                        TextView errorNotification4 = (TextView) startUpView.findViewById(R.id.required_room_width);

                        if (columninput.getText().toString().length() == 0) {
                            errorNotification1.setVisibility(View.VISIBLE);
                        }
                        else
                            errorNotification1.setVisibility(View.INVISIBLE);

                        if (rowinput.getText().toString().length() == 0){
                            errorNotification2.setVisibility(View.VISIBLE);
                        }
                        else
                            errorNotification2.setVisibility(View.INVISIBLE);

                        if (roomheight.getText().toString().length() == 0){
                            errorNotification3.setVisibility(View.VISIBLE);
                        }
                        else
                            errorNotification3.setVisibility(View.INVISIBLE);
                        if (roomwidth.getText().toString().length() == 0){
                            errorNotification4.setVisibility(View.VISIBLE);
                        }
                        else
                            errorNotification4.setVisibility(View.INVISIBLE);

                        //if the user fills in all the required dimensions
                        //the app will draw the indicated grid layout
                        //the app will also scale all drawn objects to the scale of the room's dimensions
                        if (columninput.getText().toString().length() != 0 && rowinput.getText().toString().length() != 0 && roomheight.getText().toString().length() != 0 && roomwidth.getText().toString().length() != 0) {
                            mainView.grid_width = Integer.parseInt(columninput.getText().toString());
                            mainView.grid_height = Integer.parseInt(rowinput.getText().toString());
                            mainView.roomHeight = Float.parseFloat(roomheight.getText().toString());
                            mainView.roomWidth = Float.parseFloat(roomwidth.getText().toString());
                            mainView.lengthScale = mainView.roomHeight / mainView.yMax;
                            mainView.widthScale = mainView.roomWidth / mainView.xMax;
                            mainView.drawCanvas.drawColor(Color.WHITE);
                            mainView.makeGridSquares(mainView.grid_width, mainView.grid_height, (int) mainView.xMax, (int) mainView.yMax);
                            for (ShapeDrawable x : mainView.gridSquares) {
                                x.draw(mainView.drawCanvas);
                            }
                            startUp.dismiss();
                            //show the tutorial that tells the user to designate the inaccessible regions
                            inaccessible.show();
                        }
                    }
                });
            }
        });
        startUp.show();


        Button undoButton = (Button)findViewById(R.id.undo_button);
        undoButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        if (mainView.isCircle) {
                            if (!mainView.circles.isEmpty()) {
                                Circle initialCircle = mainView.circles.get(mainView.circles.size() - 1);
                                Circle finalCircle = mainView.finalCircles.get(mainView.finalCircles.size() - 1);
                                mainView.circles.remove(mainView.circles.size() - 1);
                                mainView.finalCircles.remove(mainView.finalCircles.size() - 1);
                                drawingLayout.removeView(initialCircle.numberLabel);
                                drawingLayout.removeView(finalCircle.numberLabel);
                                mainView.circle_moveToFinalPos = false;
                                mainView.objectCount-=1;
                            }
                        }
                        else if (mainView.isRectangle) {
                                if (!mainView.initialRectangles.isEmpty()) {
                                    Rectangle initialRectangle = mainView.initialRectangles.get(mainView.initialRectangles.size() - 1);
                                    Rectangle finalRectangle = mainView.finalRectangles.get(mainView.finalRectangles.size() - 1);
                                    mainView.initialRectangles.remove(mainView.initialRectangles.size() - 1);
                                    mainView.finalRectangles.remove(mainView.finalRectangles.size() - 1);
                                    drawingLayout.removeView(initialRectangle.numberLabel);
                                    drawingLayout.removeView(finalRectangle.numberLabel);
                                    mainView.rectangle_moveToFinalPos = false; //prevents the move-to-rectangle from showing up
                                    mainView.objectCount-=1;
                                }
                        }
                        else if (mainView.isOval) {
                            if (!mainView.initialOvals.isEmpty()) {
                                Oval initialOval = mainView.initialOvals.get(mainView.initialOvals.size() - 1);
                                Oval finalOval = mainView.finalOvals.get(mainView.finalOvals.size() - 1);
                                mainView.initialOvals.remove(mainView.initialOvals.size() - 1);
                                mainView.finalOvals.remove(mainView.finalOvals.size() - 1);
                                drawingLayout.removeView(initialOval.numberLabel);
                                drawingLayout.removeView(finalOval.numberLabel);
                                mainView.oval_moveToFinalPos = false; //prevents the move-to-rectangle from showing up
                                mainView.objectCount -= 1;
                            }
                        }
                        mainView.invalidate();
                    }
                }
        );

        //code for the spinner menu which allows the user to choose a shape to draw
        Spinner shapesSpinner = (Spinner) findViewById(R.id.shapes_spinner);
        List<String> shapesList = new ArrayList<String>();
        shapesList.add("None");
        shapesList.add("Circle");
        shapesList.add("Rectangle");
        shapesList.add("Oval");
        ArrayAdapter<String> shapesadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, shapesList);
        shapesadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shapesSpinner.setAdapter(shapesadapter);
        shapesSpinner.setOnItemSelectedListener(this);

        //WRITE TO TXT FILE
        try {
            //creates the InitialScene file stored in the phone's internal memory
            fileOutput = openFileOutput("InitialScene.txt", Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fileOutput);
            try{
                osw.write(data);
                osw.flush();
                osw.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            //creates the FinalScene file stored in the phone's internal memory
            fileOutput = openFileOutput("FinalScene.txt", Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fileOutput);
            try{
                osw.write(data);
                osw.flush();
                osw.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //The done button has multiple functions, depending on what has already been done in the app
        //1. autofills the rest of the grid squares that were not desingated as being unaccessible
        //2. saves the scene drawn by the user, stored the scene files, calls the web server, receives solution from web server
        Button doneButton = (Button)findViewById(R.id.done_button);
        doneButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        //1. Auto fills the rest of the unrestricted squares to be green
                        if (mainView.isRestricted) {
                            mainView.isRestricted = false;
                            for (ShapeDrawable square : mainView.gridSquares){
                                if (square.getPaint().getColor() == Color.BLACK ) {
                                    square.getPaint().set(mainView.green);
                                    square.draw(mainView.drawCanvas);
                                }
                            }
                            Toast.makeText(getApplicationContext(), "Have fun drawing the Scene!", Toast.LENGTH_SHORT).show();

                        }
                        //2. outputs the scene to the txt file
                        else if (writeToText){
                            writeToText = false;

                            //writes the circles to txt file
                            for (Circle circle: mainView.circles){
                                    //objectstring contains the text template for each object drawn
                                objectsString = "frame "+ circle.name + " {\r\n" + "    translation " + "[" + (circle.CenterX*mainView.widthScale) + ", " + (circle.CenterY*mainView.lengthScale) + ", 0];\r\n" + "    geometry {\r\n" +"        isa " + circle.isa + ";\r\n" + "        radius [" + circle.radius*mainView.lengthScale + "];\r\n" + "    }\r\n" + "}\r\n";
                                try {
                                    fileOutput = openFileOutput("InitialScene.txt", Context.MODE_WORLD_READABLE | Context.MODE_APPEND);
                                    OutputStreamWriter osw = new OutputStreamWriter(fileOutput);
                                    try{
                                        osw.write(objectsString);
                                        osw.flush();
                                        osw.close();
                                    }catch(IOException e){
                                        e.printStackTrace();
                                    }
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            for (Circle circle: mainView.finalCircles){
                                objectsString = "frame "+ circle.name + " {\r\n" + "    translation " + "[" + (circle.CenterX*mainView.widthScale) + ", " + (circle.CenterY*mainView.lengthScale) + ", 0];\r\n" + "    geometry {\r\n" +"        isa " + circle.isa + ";\r\n" + "        radius [" + circle.radius*mainView.lengthScale + "];\r\n" + "    }\r\n" + "}\r\n";
                                try {
                                    fileOutput = openFileOutput("FinalScene.txt", Context.MODE_WORLD_READABLE | Context.MODE_APPEND);
                                    OutputStreamWriter osw = new OutputStreamWriter(fileOutput);
                                    try{
                                        osw.write(objectsString);
                                        osw.flush();
                                        osw.close();
                                    }catch(IOException e){
                                        e.printStackTrace();
                                    }
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }

                            //drawing the rectangles
                            for (Rectangle rectangle: mainView.initialRectangles){
                                objectsString = "frame "+ rectangle.name + " {\r\n" + "    translation " + "[" + (rectangle.midpointX*mainView.widthScale) + ", " + (rectangle.midpointY*mainView.lengthScale) + ", 0];\r\n" + "    geometry {\r\n" +"        isa " + rectangle.isa + ";\r\n" + "        dimension [" + Math.abs(((rectangle.right-rectangle.left)*mainView.widthScale)) + ", " + Math.abs(((rectangle.top-rectangle.bottom)*mainView.lengthScale)) + ", " + rectangle.height + "];\r\n" + "    }\r\n" + "}\r\n";
                                try {
                                    fileOutput = openFileOutput("InitialScene.txt", Context.MODE_WORLD_READABLE | Context.MODE_APPEND);
                                    OutputStreamWriter osw = new OutputStreamWriter(fileOutput);
                                    try{
                                        osw.write(objectsString);
                                        osw.flush();
                                        osw.close();
                                    }catch(IOException e){
                                        e.printStackTrace();
                                    }
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            for (Rectangle rectangle: mainView.finalRectangles){
                                objectsString = "frame "+ rectangle.name + " {\r\n" + "    translation " + "[" + (rectangle.midpointX*mainView.widthScale) + ", " + (rectangle.midpointY*mainView.lengthScale) + ", 0];\r\n" + "    geometry {\r\n" +"        isa " + rectangle.isa + ";\r\n" + "        dimension [" + Math.abs(((rectangle.right-rectangle.left)*mainView.widthScale)) + ", " + Math.abs(((rectangle.top-rectangle.bottom)*mainView.lengthScale)) + ", " + rectangle.height + "];\r\n" + "    }\r\n" + "}\r\n";
                                try {
                                    fileOutput = openFileOutput("FinalScene.txt", Context.MODE_WORLD_READABLE | Context.MODE_APPEND);
                                    OutputStreamWriter osw = new OutputStreamWriter(fileOutput);
                                    try{
                                        osw.write(objectsString);
                                        osw.flush();
                                        osw.close();
                                    }catch(IOException e){
                                        e.printStackTrace();
                                    }
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            for (Oval oval: mainView.initialOvals){
                                objectsString = "frame "+ oval.name + " {\r\n" + "    translation " + "[" + (oval.CenterX*mainView.widthScale) + ", " + (oval.CenterY*mainView.lengthScale) + ", 0];\r\n" + "    geometry {\r\n" +"        shape Oval;\r\n"+"        isa " + oval.isa + ";\r\n" + "        dimension [" + Math.abs(((oval.right-oval.left)*mainView.widthScale)) + ", " + Math.abs(((oval.top-oval.bottom)*mainView.lengthScale)) + ", " + oval.height + "];\r\n" + "    }\r\n" + "}\r\n";
                                try {
                                    fileOutput = openFileOutput("InitialScene.txt", Context.MODE_WORLD_READABLE | Context.MODE_APPEND);
                                    OutputStreamWriter osw = new OutputStreamWriter(fileOutput);
                                    try{
                                        osw.write(objectsString);
                                        osw.flush();
                                        osw.close();
                                    }catch(IOException e){
                                        e.printStackTrace();
                                    }
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            for (Oval oval: mainView.finalOvals){
                                objectsString = "frame "+ oval.name + " {\r\n" + "    translation " + "[" + (oval.CenterX*mainView.widthScale) + ", " + (oval.CenterY*mainView.lengthScale) + ", 0];\r\n" + "    geometry {\r\n" +"       shape Oval;\r\n"+"        isa " + oval.isa + ";\r\n" + "        dimension [" + Math.abs(((oval.right-oval.left)*mainView.widthScale)) + ", " + Math.abs(((oval.top-oval.bottom)*mainView.lengthScale)) + ", " + oval.height + "];\r\n" + "    }\r\n" + "}\r\n";
                                try {
                                    fileOutput = openFileOutput("FinalScene.txt", Context.MODE_WORLD_READABLE | Context.MODE_APPEND);
                                    OutputStreamWriter osw = new OutputStreamWriter(fileOutput);
                                    try{
                                        osw.write(objectsString);
                                        osw.flush();
                                        osw.close();
                                    }catch(IOException e){
                                        e.printStackTrace();
                                    }
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }

                            //the thread is needed to run all the web-server related code
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        //create socket connection
                                        Socket s = new Socket("10.117.76.152", 5015);
                                        s.setSendBufferSize(1024); //sets the buffer size

                                        //this part is used to inform the server how big the file is
                                        String filePath1 = getApplicationContext().getFilesDir() + "/" + "InitialScene.txt";
                                        File initialScene = new File(filePath1);
                                        long fileSize1 = initialScene.length();
                                        String fileSizeString1 = ""+fileSize1;
                                        byte[] bytebuffer1 = fileSizeString1.getBytes();
                                        s.getOutputStream().write(bytebuffer1);

                                        //this part sends the server the actual scene text information
                                        byte[] byteArray1 = new byte[(int)fileSize1];
                                        FileInputStream fis1 = getApplicationContext().openFileInput("InitialScene.txt");
                                        fis1.read(byteArray1); //store the text in the scene file as bytes in the byte array
                                        s.getOutputStream().write(byteArray1);

                                        //same thing but for the FinalScene text file
                                        String filePath2 = getApplicationContext().getFilesDir() + "/" + "FinalScene.txt";
                                        File finalScene = new File(filePath2);
                                        long fileSize2 = finalScene.length();
                                        String fileSizeString2 = ""+fileSize2;
                                        byte[] bytebuffer2 = fileSizeString2.getBytes();
                                        s.getOutputStream().write(bytebuffer2);

                                        byte[] byteArray2 = new byte[(int)fileSize2];
                                        FileInputStream fis2 = getApplicationContext().openFileInput("FinalScene.txt");
                                        fis2.read(byteArray2);
                                        s.getOutputStream().write(byteArray2);

                                        //READS THE SOLUTION SENT FROM THE SERVER

                                        //Receives the size of the solution file so that I know how big to make the byte array
                                        byte[] solutionSizeArray = new byte[4];
                                        s.getInputStream().read(solutionSizeArray);
                                        //this string is temporary because it receives numbers as well as random useless characters
                                        String string = new String(solutionSizeArray, "UTF-8");
                                        String numbersOnly = "";
                                        //filters just the numbers from the information initially received from server (gets rid of useless characters)
                                        for (int i = 0; i<string.length();i++){
                                            char number = string.charAt(i);
                                            if (number=='0'||number=='1'||number=='2'||number=='3'||number=='4'||number=='5'||number=='6'||number=='7'||number=='8'||number=='9'){
                                                numbersOnly = numbersOnly + number;
                                            }
                                        }
                                        int solutionSize = Integer.parseInt(numbersOnly);
                                        System.out.println("SOLUTION SIZE: " + solutionSize);

                                        byte[] solution = new byte[solutionSize];
                                        s.getInputStream().read(solution);

                                        //writes the solution received from the server into "simulation.txt"
                                        try {
                                            tasksTest = openFileOutput("simulation.txt", Context.MODE_PRIVATE);
                                            OutputStreamWriter osw = new OutputStreamWriter(tasksTest);
                                            String solutionString = new String(solution, "UTF-8");
                                            try{
                                                osw.write(solutionString);
                                                osw.flush();
                                                osw.close();
                                            }catch(IOException e){
                                                e.printStackTrace();
                                            }
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }

                                        s.close();
                                    } catch (IOException e) {
                                        System.out.println("COULDN'T CONNECT TO REMOTE WEB SERVER");
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                            while (true){
                                String filePath2 = getApplicationContext().getFilesDir() + "/" + "simulation.txt";
                                File simulationText = new File(filePath2);
                                if (simulationText.exists()){
                                    readSolution();
                                    break;
                                }
                            }
//                            readSolution();
                        }
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Conditions for which item from the shape spinner was selected (0 = first item, 3 = last item)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position==0){
                mainView.isCircle = false;
                mainView.isRectangle = false;
                mainView.isOval = false;
                mainView.circleShown = false;
            }
            else if (position==1) {
                mainView.isCircle = true;
                mainView.isRectangle = false;
                mainView.isOval = false;
                if (!mainView.circleShown)
                    circTut.show();
                    circTut.show();
                mainView.circleShown = true;
            }
            else if (position ==2) {
                mainView.isRectangle = true;
                mainView.isCircle = false;
                mainView.isOval = false;
                if (!mainView.rectShown)
                    rectTut.show();
                mainView.rectShown = true;
            }
            else if (position ==3) {
                mainView.isRectangle = false;
                mainView.isCircle = false;
                mainView.isOval = true;
                if (!mainView.ovalShown)
                    ovalTut.show();
                mainView.ovalShown = true;
            }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //required override method for the shape spinner
    }

    //Reads the solutions returned from the Web Server in the format that I created
    public void readSolution(){
        mainView.isRobot = true; //Flag which tells the MainView to start drawing the robot on the canvas
        Toast.makeText(getApplicationContext(), "Rendering Simulation", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() { //Creates a thread so that the MainView will update (invalidate) after each while loop iteration
                @Override
                public void run() {
                    String line = null;
                    FileInputStream fis = null;
                    BufferedReader buffreader = null;
                    try {
                        fis = openFileInput("simulation.txt"); //Reads from this file
                        buffreader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
                        while ((line = buffreader.readLine()) != null) { //while there are more lines to read, set line equal to the next line to read
                            if (line.substring(0, 6).equals("moveTo")) {
                                int count = 9; //starts at the first significant number (the x coordinate)
                                float x = -1; //initially sets these numbers to -1 to indicate they have not been set
                                float y = -1;
                                float z = -1;
                                String number = "";
                                while (count != line.length() - 1) {
                                    char character = line.charAt(count); //iterates character by character
                                    if (character == ')') { //if ')' that means this is the end of the coordinate, so set the z value
                                        z = Float.parseFloat(number);
                                        System.out.println("Move to: " + "(" + x + ", " + y + ", " + z + ")");
                                        mainView.moveRobotSquare(x, y, z); //This tells the canvas to draw the robotsquare at the position its supposed to move to
                                        x = -1; //resets the values to read more possible coordinate positions in moveTo command
                                        y = -1;
                                        z = -1;
                                        number = "";
                                    } else if (character != ',' && character != '(') //if the character is a number, add it to the "number" string
                                        number += character;
                                    else if (character == ',') { //a comma indicates the end of a number for a specific coordinate (x, y, or z)
                                        if (line.charAt(count + 2) == '(')
                                            count += 2; //+2 to count so that this pointer will jump to the next number
                                        else if (x == -1) { //if x isn't set, set x to equal the number
                                            x = Float.parseFloat(number);
                                            number = "";
                                        } else if (y == -1) { //if y isn't set, set y to equal the number
                                            y = Float.parseFloat(number);
                                            number = "";
                                        }
                                    }
                                    count++;
                                }

                            } else if (line.substring(0, 6).equals("pickUp")) {
                                int count = 8;
                                float x = -1;
                                float y = -1;
                                float z = -1;
                                String number = "";
                                String objectName = "";
                                while (line.charAt(count) != ',') {
                                    objectName += line.charAt(count);
                                    count++;
                                }
                                count += 3;
                                while (z == -1) {
                                    char character = line.charAt(count);
                                    if (character == ')' | character == ']')
                                        z = Float.parseFloat(number);
                                    else if (character != ',')
                                        number += character;
                                    else if (character == ',') {
                                        if (x == -1) {
                                            x = Float.parseFloat(number);
                                            number = "";
                                        } else if (y == -1) {
                                            y = Float.parseFloat(number);
                                            number = "";
                                        }
                                    }
                                    count++;
                                }
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Picking up object", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                try {
                                    TimeUnit.SECONDS.sleep(2);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                System.out.println("Pick Up: " + objectName + " at " + "(" + x + ", " + y + ", " + z + ")");
                            } else if (line.substring(0, 7).equals("putDown")) {
                                int count = 9;
                                float x = -1;
                                float y = -1;
                                float z = -1;
                                String number = "";
                                String objectName = "";
                                while (line.charAt(count) != ',') {
                                    objectName += line.charAt(count);
                                    count++;
                                }
                                count += 3;
                                while (z == -1) {
                                    char character = line.charAt(count);
                                    if (character == ')' | character == ']')
                                        z = Float.parseFloat(number);
                                    else if (character != ',')
                                        number += character;
                                    else if (character == ',') {
                                        if (x == -1) {
                                            x = Float.parseFloat(number);
                                            number = "";
                                        } else if (y == -1) {
                                            y = Float.parseFloat(number);
                                            number = "";
                                        }
                                    }
                                    count++;
                                }
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Putting down object", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                try {
                                    TimeUnit.SECONDS.sleep(2);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                System.out.println("Put Down: " + objectName + " at " + "(" + x + ", " + y + ", " + z + ")");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Simulation Complete", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
    }
}
