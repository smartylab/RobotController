package kr.co.smartylab.rocon.robotcontroller.comm;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class Communication {
	private String url;
	private HttpClient client; 
	
	private class TransmissionTask extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			if(client == null) return 0;
			HttpGet request = new HttpGet(url+"?linear="+params[0]+"&angular="+params[1]);
			try {
				HttpResponse response = client.execute(request);
                response.getEntity().getContent().close();
				return 1;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 0;
		}
		
	}
	
	public Communication(String url) {
		// TODO Auto-generated constructor stub
		this.url = url;
	}
	
	public void connect() {
		client = new DefaultHttpClient();
		new TransmissionTask().execute("0.0", "0.0");
	}
	
	public void sendControlCommand(final String linear, final String angular) {
		new TransmissionTask().execute(linear, angular);
	}
}
