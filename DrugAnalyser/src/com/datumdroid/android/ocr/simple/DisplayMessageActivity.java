package com.datumdroid.android.ocr.simple;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;


public class DisplayMessageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);


        Bundle intent = getIntent().getExtras();
        //Bundle intent =getIntent().getExtras();
       
        String response_name = intent.getString("info_name");
        String response_msg = intent.getString("info_message");
        
        EditText patid = (EditText)findViewById(R.id.patient_id);
        EditText name= (EditText)findViewById(R.id.name);
       

        patid.setText(" ");
        name.setText(" ");
       

       

        patid.setText(response_name);
        name.setText(response_msg);
      
       /* TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(response);
        setContentView(textView);*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

