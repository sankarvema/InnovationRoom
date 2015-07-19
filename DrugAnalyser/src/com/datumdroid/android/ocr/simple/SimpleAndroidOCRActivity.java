package com.datumdroid.android.ocr.simple;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class SimpleAndroidOCRActivity extends Activity {
	
	public final static String info_Response = null,info_age=null,info_weight=null,info_name=null,info_sex=null;
    ProgressDialog prgDialog;
    EditText patid;

    public static int response_weight, response_age,info_id;
    public static String response_name,response_sex,response_bg,Drugname;
	public static final String PACKAGE_NAME = "com.datumdroid.android.ocr.simple";
	// service URL
   // private static final String SERVICE_URL = "http://ml.t.proxylocal.com/RESTFullWebService/drugservice/druganalysis?drugname";
    //private static final String SERVICE_URL = "http://localhost:5000/RestWebService/rest/patient";

	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/SimpleAndroidOCR/";
	
	// You should have the trained data file in assets folder
	// You can get them at:
	// http://code.google.com/p/tesseract-ocr/downloads/list
	public static final String lang = "eng";

	private static final String TAG = "SimpleAndroidOCR.java";

	protected Button _button;
	// protected ImageView _image;
	protected EditText _field;
	protected String _path;
	protected boolean _taken;

	protected static final String PHOTO_TAKEN = "photo_taken";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
					return;
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}

		}
		
		// lang.traineddata file with the app (in assets folder)
		// You can get them at:
		// http://code.google.com/p/tesseract-ocr/downloads/list
		// This area needs work and optimization
		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
				//GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(DATA_PATH
						+ "tessdata/" + lang + ".traineddata");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				//while ((lenf = gin.read(buff)) > 0) {
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				//gin.close();
				out.close();
				
				Log.v(TAG, "Copied " + lang + " traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
			}
		}

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		// _image = (ImageView) findViewById(R.id.image);
		_field = (EditText) findViewById(R.id.field);
		_button = (Button) findViewById(R.id.button);
		_button.setOnClickListener(new ButtonClickHandler());

		_path = DATA_PATH + "/ocr.jpg";
	}

	public class ButtonClickHandler implements View.OnClickListener {
		public void onClick(View view) {
			Log.v(TAG, "Starting Camera app");
			startCameraActivity();
		}
	}

	// Simple android photo capture:
	// http://labs.makemachine.net/2010/03/simple-android-photo-capture/

	protected void startCameraActivity() {
		File file = new File(_path);
		Uri outputFileUri = Uri.fromFile(file);

		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i(TAG, "resultCode: " + resultCode);

		if (resultCode == -1) {
			onPhotoTaken();
		} else {
			Log.v(TAG, "User cancelled");
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(SimpleAndroidOCRActivity.PHOTO_TAKEN, _taken);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i(TAG, "onRestoreInstanceState()");
		if (savedInstanceState.getBoolean(SimpleAndroidOCRActivity.PHOTO_TAKEN)) {
			onPhotoTaken();
		}
	}

	protected void onPhotoTaken() {
		_taken = true;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;

		Bitmap bitmap = BitmapFactory.decodeFile(_path, options);

		try {
			ExifInterface exif = new ExifInterface(_path);
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			Log.v(TAG, "Orient: " + exifOrientation);

			int rotate = 0;

			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			}

			Log.v(TAG, "Rotation: " + rotate);

			if (rotate != 0) {

				// Getting width & height of the given image.
				int w = bitmap.getWidth();
				int h = bitmap.getHeight();

				// Setting pre rotate
				Matrix mtx = new Matrix();
				mtx.preRotate(rotate);

				// Rotating Bitmap
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
			}

			// Convert to ARGB_8888, required by tess
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

		} catch (IOException e) {
			Log.e(TAG, "Couldn't correct orientation: " + e.toString());
		}

		// _image.setImageBitmap( bitmap );
		
		Log.v(TAG, "Before baseApi");

		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(DATA_PATH, lang);
		baseApi.setImage(bitmap);
		
		String recognizedText = baseApi.getUTF8Text();
		
		baseApi.end();

		// You now have the text in recognizedText var, you can do anything with it.
		// We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
		// so that garbage doesn't make it to the display.

		Log.d(TAG, "OCRED TEXT: " + recognizedText);

		if ( lang.equalsIgnoreCase("eng") ) {
			recognizedText = recognizedText.replaceAll("[^a-zA-Z]+", " ");
			
		}
	
		recognizedText= recognizedText.trim();
		Log.d("before trimming","" + recognizedText);
		
		if ( recognizedText.length() != 0 ) {
			_field.setText(recognizedText);
			_field.setSelection(_field.getText().toString().length());
		}
		try{
		//info_id = Integer.parseInt(recognizedText);
		Drugname = recognizedText;
		
		//int temp=info_id;
		Log.d("after trimming","" + recognizedText+" temp info="+Drugname);
		callWebService();
		}
		catch(Exception e)
		{
		     AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		      alertDialogBuilder.setMessage("Please capture the Image again");
		      alertDialogBuilder.setPositiveButton("OK", 
		      new DialogInterface.OnClickListener() {
				
		         @Override
		         public void onClick(DialogInterface arg0, int arg1) {
		            
					
		         }
		      });
		      alertDialogBuilder.setNegativeButton("Cancel", 
		      new DialogInterface.OnClickListener() {
					
		         @Override
		         public void onClick(DialogInterface dialog, int which) {
		           
				 }
		      });
			    
		      AlertDialog alertDialog = alertDialogBuilder.create();
		      alertDialog.show();
			    
		}
		
		

		// Cycle done.
	}


	    public void callWebService(){

	    	DrugDetails DD=new DrugDetails();
	    	String URL=DD.FetchURL(Drugname);
	        //String URL = SERVICE_URL + "/id?id=" + Drugname;
	        Log.d("URL",""+ URL+"");
	        WebServiceTask wst = new WebServiceTask(WebServiceTask.GET_TASK, this, "Getting data...");

	        wst.execute(new String[] { URL });
	        }

	    public void handleResponse(String response) {
	        try {
	        	if(response.contains("Patient ID not found"))
	        	{
	        		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	  		      	alertDialogBuilder.setMessage("Sorry This Patient ID is not found!!");
	  		      	alertDialogBuilder.setPositiveButton("OK", 
	  			      new DialogInterface.OnClickListener() {
	  					
	  			         @Override
	  			         public void onClick(DialogInterface arg0, int arg1) {
	  			            
	  						
	  			         }
	  			      });
	  		      AlertDialog alertDialog = alertDialogBuilder.create();
			      alertDialog.show();
			      Log.d("not found :","http response:"+response);
	        		
	        	}
	        	 
	        	else
	        	{
	        		Log.d("found","http response:"+response);
	        	JSONObject jso = new JSONObject(response);

	           
	            
	        	Drugname=jso.getString("Drug");
	            response_name=jso.getString("Messages");

	            callDisplayResponse();
	        	}
	        } catch (Exception e) {
	            Log.e(TAG, e.getLocalizedMessage(), e);
	        }
	        
	        Log.d("Response",""+ response+"");
	        
	        
	    }

	    public void callDisplayResponse()
	    {

	        Intent intent = new Intent(this, DisplayMessageActivity.class);

	       /* StringBuilder response = new StringBuilder();
	        response.append("Patient ID: " + response_id);
	        response.append(System.getProperty("line.separator"));
	        response.append("Age: " + response_age);
	        String output = response.toString();
	        intent.putExtra(info_Response,output);*/

	        Bundle bundle = new Bundle();

	      
	        bundle.putString("info_message" ," "+response_name);
	        bundle.putString("info_name" ," "+Drugname);
	        
	        intent.putExtras(bundle);
	        Log.d("bundle",""+ bundle+"");
	        startActivity(intent);
	        // intent.putExtra(info_age,""+age);
	    }
	    private class WebServiceTask extends AsyncTask<String, Integer, String> {


	        public static final int GET_TASK = 2;

	        private static final String TAG = "WebServiceTask";

	        // connection timeout, in milliseconds (waiting to connect)
	        private static final int CONN_TIMEOUT = 100000;

	        // socket timeout, in milliseconds (waiting for data)
	        private static final int SOCKET_TIMEOUT = 10000;

	        private int taskType = GET_TASK;
	        private Context mContext = null;
	        private String processMessage = "Processing...";



	        private ProgressDialog pDlg = null;

	        public WebServiceTask(int taskType, Context mContext, String processMessage) {

	            this.taskType = taskType;
	            this.mContext = mContext;
	            this.processMessage = processMessage;
	        }


	        private void showProgressDialog() {

	            pDlg = new ProgressDialog(mContext);
	            pDlg.setMessage(processMessage);
	            pDlg.setProgressDrawable(mContext.getWallpaper());
	            pDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	            pDlg.setCancelable(false);
	            pDlg.show();

	        }

	        @Override
	        protected void onPreExecute() {


	            showProgressDialog();

	        }

	        protected String doInBackground(String... urls) {

	            String url = urls[0];
	            String result = "";

	            HttpResponse response = doResponse(url);

	            if (response == null) {
	                return result;
	            } else {

	                try {

	                    try {
	                        result = inputStreamToString(response.getEntity().getContent());
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }

	                } catch (IllegalStateException e) {
	                    Log.e(TAG, e.getLocalizedMessage(), e);

	                }


	            }

	            return result;
	        }


	        @Override
	        protected void onPostExecute(String response) {

	            handleResponse(response);
	            pDlg.dismiss();

	        }

	        // Establish connection and socket (data retrieval) timeouts
	        private HttpParams getHttpParams() {

	            HttpParams http = new BasicHttpParams();

	            HttpConnectionParams.setConnectionTimeout(http, CONN_TIMEOUT);
	            HttpConnectionParams.setSoTimeout(http, SOCKET_TIMEOUT);

	            return http;
	        }

	        private HttpResponse doResponse(String url) {

	            // Use our connection and data timeouts as parameters for our
	            // DefaultHttpClient
	            HttpClient httpclient = new DefaultHttpClient(getHttpParams());

	            HttpResponse response = null;

	            try {
	                switch (taskType) {

	                    case GET_TASK:
	                        HttpGet httpget = new HttpGet(url);
	                        response = httpclient.execute(httpget);
	                        break;
	                }
	            } catch (Exception e) {

	                Log.e(TAG, e.getLocalizedMessage(), e);

	            }

	            return response;
	        }

	    }
	    private String inputStreamToString(InputStream is) {

	        String line = "";
	        StringBuilder total = new StringBuilder();

	        // Wrap a BufferedReader around the InputStream
	        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

	        try {
	            // Read response until the end
	            while ((line = rd.readLine()) != null) {
	                total.append(line);
	            }
	        } catch (IOException e) {
	            Log.e(TAG, e.getLocalizedMessage(), e);
	        }

	        // Return full string
	        return total.toString();
	    }

}