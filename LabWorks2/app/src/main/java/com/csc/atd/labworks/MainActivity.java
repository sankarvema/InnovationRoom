package com.csc.atd.labworks;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.csc.atd.labworks.text_recogn.NativeInterface;

import org.opencv.android.OpenCVLoader;
public class MainActivity extends ActionBarActivity {

    private static final String TAG = "DetectText::Activity";

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "Error loading OpenCV library");
        }
        System.loadLibrary("text_recogn");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        DetectTextNative dtn = new DetectTextNative(getAssets());
        NativeInterface ninterface = new NativeInterface(getAssets());
        TextView tv = new TextView(this);
        tv.setText(ninterface.pingLibrary());
        setContentView(tv);
        //setContentView(R.layout.activity_main);
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
}
