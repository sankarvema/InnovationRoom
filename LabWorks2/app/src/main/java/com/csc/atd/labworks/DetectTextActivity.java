package com.csc.atd.labworks;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.csc.atd.labworks.text_recogn.*;

public class DetectTextActivity extends ActionBarActivity {

    private static final String TAG = "DetectText::Activity";

    public static final int VIEW_MODE_RGBA  = 0;
    public static int viewMode = VIEW_MODE_RGBA;

    private NativeInterface dtn;
    private AssetManager am;

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "Error loading OpenCV library");
        }
        System.loadLibrary("text_recogn");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_detect_text);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_detect_text);

        am = getAssets();

        dtn = new NativeInterface(am);

        TextView tv = (TextView) findViewById(R.id.msg);
        tv.setText(dtn.pingLibrary());

        Button buttonOne = (Button) findViewById(R.id.button);
        buttonOne.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                DetectView dt = (DetectView) findViewById(R.id.myView);
                Mat frame = dt.getRgbaFrame();
                Log.i(TAG, "buttonCallback");
                int [] boxes = dtn.getBoundingBoxes(frame.getNativeObjAddr());
                System.out.println(boxes);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detect_text, menu);
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
}
