package com.phonegap.custom-camera;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

public class CameraPlugin extends CordovaPlugin{
	
	public static final String ACTION_CALL_CAMERA = "takePicture";
	
	private Activity activity = null;
	private CallbackContext callback;
	
    /** 
     * Override the plugin initialise method and set the Activity as an 
     * instance variable.
     */
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) 
    {
    	Log.i("init", "inited");
        super.initialize(cordova, webView);
        activity = cordova.getActivity();
    }
    
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		Log.i("test", action);
		if(ACTION_CALL_CAMERA.equals(action)){

			callback = callbackContext;

			Intent intent = new Intent(activity, CamActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("callbackId", callbackContext.getCallbackId());

			cordova.startActivityForResult((CordovaPlugin) this, intent, 456);

			PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
			r.setKeepCallback(true);
			callbackContext.sendPluginResult(r);

			return true;

		}

		callbackContext.error("action not found");
		return false;

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i("data", "data: "+data.getStringExtra("result"));
		callback.success(data.getStringExtra("result"));
		
	}
	
}
