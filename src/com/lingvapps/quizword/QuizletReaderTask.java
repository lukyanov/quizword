

package com.lingvapps.quizword;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import android.os.AsyncTask;

public class QuizletReaderTask extends AsyncTask<String, Void, JSONObject> {
	
    public interface Callback {
        void onComplete(JSONObject JSON);
        void onFailure();
    }
    
    private Set<Callback> callbacks = new HashSet<Callback>();
    
    public void setObserver(Callback cb) {
    	callbacks.clear();
    	callbacks.add(cb);
    }
    
    @Override
    protected JSONObject doInBackground(String... URLS) {
    		URL url;
    		try {
    			url = new URL(URLS[0]);
    			BufferedReader in = new BufferedReader(
    					new InputStreamReader(url.openStream()));
    			StringBuilder response = new StringBuilder();
    			String line;
    			while ((line = in.readLine()) != null)
    				response.append(line);
    			in.close();
    			JSONObject JSON = new JSONObject(response.toString());
    			return JSON;
    		} catch (Exception e) {
    			this.cancel(true);
    			e.printStackTrace();
    			return null;
    		}
    }
	
	protected void onPostExecute(JSONObject JSON) {
	     if (null != JSON) {
	    	 for (Callback cb: callbacks) {
	    		 cb.onComplete(JSON);
	    	 }
	     } else {
	         for (Callback cb: callbacks) {
	             cb.onFailure();
	         }       
	     }
	}
}
