package com.phonegap.customcamera;
/**
 * @author Jose Davis Nidhin
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream.PutField;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaWebView;

import android.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CamActivity extends Activity {
	private static final String TAG = "CamTestActivity";
	Preview preview;
	Button buttonClick;
	Button buttonClick2;
	Camera camera;
	CamActivity act;
	Context ctx;
	private String callbackId;
	private boolean black_white_button_clicked;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ctx = this;
		act = this;
		
		black_white_button_clicked = false;
		
		Bundle extras = act.getIntent().getExtras();
		callbackId = extras.getString("callbackId");
		Log.i(TAG, "cameraId"+callbackId);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(this.getResource("main", "layout"));

		preview = new Preview(this, (SurfaceView)findViewById(this.getResource("surfaceView", "id")));
		preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		preview.setKeepScreenOn(true);
/*
		preview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				camera.takePicture(shutterCallback, rawCallback, jpegCallback);
			}
		});*/

		//Toast.makeText(ctx, getString(this.getResource("take_photo_help", "string")), Toast.LENGTH_LONG).show();

		buttonClick = (Button) findViewById(this.getResource("btnCapture", "id"));
		
		buttonClick.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
				Log.i("camera", "picture taken");
				Log.i("camera", camera);
				camera.takePicture(shutterCallback, rawCallback, jpegCallback);
				
				/* return data to plugin
					ComponentName name = act.getCallingActivity();
				Intent returnIntent = new Intent(act, name.getClass());
				returnIntent.putExtra("result","some data");
				setResult(RESULT_OK,returnIntent);
				finish();
				*/
				
				
			}
		});
		
		buttonClick.setOnLongClickListener(new OnLongClickListener(){
			@Override
			public boolean onLongClick(View arg0) {
				camera.autoFocus(new AutoFocusCallback(){
					@Override
					public void onAutoFocus(boolean arg0, Camera arg1) {
						Log.i("camera", "picture taken");
						camera.takePicture(shutterCallback, rawCallback, jpegCallback);
					}
				});
				return true;
			}
		});
		
		
		// black/white button
		
		buttonClick2 = (Button) findViewById(this.getResource("btnCapture2", "id"));
		
		buttonClick2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
				Log.i("camera", "picture taken");
				black_white_button_clicked = true;
				
				camera.takePicture(shutterCallback, rawCallback, jpegCallback);
				
				/* return data to plugin
					ComponentName name = act.getCallingActivity();
				Intent returnIntent = new Intent(act, name.getClass());
				returnIntent.putExtra("result","some data");
				setResult(RESULT_OK,returnIntent);
				finish();
				*/
				
				
			}
		});
		
		buttonClick2.setOnLongClickListener(new OnLongClickListener(){
			@Override
			public boolean onLongClick(View arg0) {
				camera.autoFocus(new AutoFocusCallback(){
					@Override
					public void onAutoFocus(boolean arg0, Camera arg1) {
						
						Log.i("camera", "picture taken");
						black_white_button_clicked = true;
						
						camera.takePicture(shutterCallback, rawCallback, jpegCallback);
					}
				});
				return true;
			}
		});
		
	}
	
	private int getResource(String name, String type){
		String package_name = getApplication().getPackageName();
		Resources resources = getApplication().getResources();
		return resources.getIdentifier(name, type, package_name);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		int numCams = Camera.getNumberOfCameras();
		if(numCams > 0){
			try{
				camera = Camera.open(0);
				camera.startPreview();
				preview.setCamera(camera);
			} catch (RuntimeException ex){
				//Toast.makeText(ctx, getString(this.getResource("camera_not_found", "string")), Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	protected void onPause() {
		if(camera != null) {
			camera.stopPreview();
			preview.setCamera(null);
			camera.release();
			camera = null;
		}
		super.onPause();
	}

	private void resetCam() {
		camera.startPreview();
		preview.setCamera(camera);
	}

	private void refreshGallery(File file) {
		Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mediaScanIntent.setData(Uri.fromFile(file));
		sendBroadcast(mediaScanIntent);
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			//			 Log.d(TAG, "onShutter'd");
		}
	};

	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			//			 Log.d(TAG, "onPictureTaken - raw");
		}
	};

	PictureCallback jpegCallback = new PictureCallback() {

import android.util.Log;
		public void onPictureTaken(byte[] data, Camera camera) {

			Log.i("jpeg2", "called");
			ByteArrayInputStream inStream = new ByteArrayInputStream(data);
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(inStream);
			
			if(black_white_button_clicked){
				
				int height = bitmap.getHeight();
				int width = bitmap.getWidth();
				
				double redFactor = 0.33;
				double greenFactor = 0.59;
				double blueFactor = 0.11;
				
				Bitmap bitmapClone = Bitmap.createBitmap(width, height, bitmap.getConfig());
				
				for (int h = 0; h < height; h++) {
					for (int w = 0; w < width; w++) {
						
						int pixel = bitmap.getPixel(w,h);

						int redValue = (int)(Color.red(pixel)*redFactor);
						int greenValue = (int)(Color.green(pixel)*greenFactor);
						int blueValue = (int)(Color.blue(pixel)*blueFactor);
						
						int combinedValue = redValue+greenValue+blueValue;
						bitmapClone.setPixel(w, h, Color.rgb(combinedValue, combinedValue, combinedValue));
						
					}
				}
				
				bitmap = bitmapClone;
				
			}

			Log.i("jpeg3", "called");
		    bitmap.compress(CompressFormat.JPEG, 70, outStream);

			Log.i("jpeg4", "called");
			// get the base 64 string
			String imgString = Base64.encodeToString(outStream.toByteArray(), Base64.NO_WRAP);

			Log.i("jpeg5", "called");
			ComponentName name = act.getCallingActivity();
			Intent returnIntent = new Intent(act, name.getClass());

			Log.i("jpeg6", "called");
			returnIntent.putExtra("result","data:image/jpeg;base64,"+imgString);
			setResult(RESULT_OK,returnIntent);
			finish();
			/*
			new SaveImageTask().execute(data);
			resetCam();
			Log.d(TAG, "onPictureTaken - jpeg");
			*/
		}
	};

	private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

		@Override
		protected Void doInBackground(byte[]... data) {
			FileOutputStream outStream = null;

			// Write to SD Card
			try {
				File sdCard = Environment.getExternalStorageDirectory();
				File dir = new File (sdCard.getAbsolutePath() + "/camtest");
				dir.mkdirs();				

				String fileName = String.format("%d.jpg", System.currentTimeMillis());
				File outFile = new File(dir, fileName);

				outStream = new FileOutputStream(outFile);
				outStream.write(data[0]);
				outStream.flush();
				outStream.close();

				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());

				refreshGallery(outFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			return null;
		}

	}
}


