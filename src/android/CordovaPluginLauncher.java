package com.cordova.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;

import java.util.List;
import java.util.Iterator;


public class CordovaPluginLauncher extends CordovaPlugin {

	public static final String ACTION_REQUEST_PROVIDER = "request";
	
	private LocationManager locationManager = null;
	private LocationListener locationListener;
	
	private Activity thisAct;
	private CallbackContext callCtx;
	
	private long maximumAge;
	private long timeout;
	private boolean enableHighAccurancy;
	
	private Handler timeoutHandler = null;
	
	class timer implements Runnable {
          public void run() {
          		callCtx.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION, "TIMEOUT"));
          		locationManager.removeUpdates(locationListener);
          }
    }
    
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
		
		try {
			
			thisAct = this.cordova.getActivity();
			
			callCtx = callbackContext;
			
			maximumAge = args.getLong(0);
			timeout = args.getLong(1);
			enableHighAccurancy = args.getBoolean(2);
			
			if(ACTION_REQUEST_PROVIDER.equalsIgnoreCase(action)){
				PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
				r.setKeepCallback(true);
				callbackContext.sendPluginResult(r);
				requestLocationAccurancy();
				return true;
			}
			else{
				callbackContext.error("Call undefined action: "+action);
				return false;
			}
		
		} catch (JSONException e) {
			callbackContext.error("CordovaPlugin exception occured: "+e.toString());
			return false;
		}
		
		
	}

	private void requestLocationAccurancy(){
		
		locationListener = new LocationListener() {
	        @Override
	        public void onStatusChanged(String provider, int status, Bundle extras) {
	        }
	
	        @Override
	        public void onProviderEnabled(String provider) {
	        }
	
	        @Override
	        public void onProviderDisabled(String provider) {
	        }
	
	        @Override
	        public void onLocationChanged(Location location) {
	        
	        	if(isMaximumAgeIgnored() || (location.getElapsedRealtimeNanos()/1000000) < maximumAge){
	        	
					JSONObject jsonObj = getProviderResponseByLocation(location);
				
					PluginResult r = new PluginResult(PluginResult.Status.OK, jsonObj);
					callCtx.sendPluginResult(r);
				
					locationManager.removeUpdates(locationListener);
					
				}
				
	        }
	    };
    	
		locationManager = (LocationManager) thisAct.getSystemService(Context.LOCATION_SERVICE);
		
		Criteria c = new Criteria();
        c.setAccuracy(enableHighAccurancy ? Criteria.ACCURACY_FINE : Criteria.ACCURACY_COARSE);
        
        String bestProvider = locationManager.getBestProvider(c,true);
		locationManager.requestLocationUpdates(bestProvider, 0, 0, locationListener);
        
        if(!isTimeoutIgnored()){
        	timeoutHandler = new Handler();
	    	timeoutHandler.postDelayed( new timer(),timeout);
	    }
	    
	}
	
	private boolean isTimeoutIgnored(){
		return timeout == -1;
	}
	
	private boolean isMaximumAgeIgnored(){
		return maximumAge == -1;
	}
	
	private JSONObject getProviderResponseByLocation(Location location){
		
		try{
			
			// Aufbau einer Standortinformation 			
			JSONObject locationinfo = new JSONObject(); 
			JSONObject coords = new JSONObject(); 	         	        
			
			// Information zur Bestimmung der Genauigkeit 	        
			coords.put("latitude",location.getLatitude()); 	        
			coords.put("longitude",location.getLongitude()); 	        
			coords.put("accurancy", location.getAccuracy()); 
				         	        
			if(location.hasBearing()){ 	        	
			  coords.put("heading", location.getBearing());	 	        
			} 	        
			
			if(location.hasAltitude()){ 	        	
			  coords.put("altitude", location.getAltitude());	 	        
			} 	        
			
			if(location.hasSpeed()){ 	        	
			  coords.put("speed", location.getSpeed());	 	        
			} 	         			
			
			locationinfo.put("coords",coords); 			 			
			
			// Das Alter einer Standortkoordinate 			
			locationinfo.put("timestamp", location.getElapsedRealtimeNanos()/1000000);

			return locationinfo;
			
		}
		catch (JSONException e){
			callCtx.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
        }
        
        return new JSONObject();
        
	}
    
}