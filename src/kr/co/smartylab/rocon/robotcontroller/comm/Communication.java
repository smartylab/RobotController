package kr.co.smartylab.rocon.robotcontroller.comm;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class Communication {
	
	private String url;
	private HttpClient client; 

	public Communication(String url) {
		// TODO Auto-generated constructor stub
		this.url = url;
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 1000);
		HttpConnectionParams.setSoTimeout(params, 3000);
		client = new DefaultHttpClient(params);
	}
	
	public boolean connect() {
		HttpGet request = new HttpGet(url+"?linear=0.0&angular=0.0");
		try {
			HttpResponse response = client.execute(request);
			int code = response.getStatusLine().getStatusCode();
			response.getEntity().consumeContent();
			if(200 <= code && code < 300) {
				return true;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void sendControlCommand(final String linear, final String angular) {
		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				HttpGet request = new HttpGet(url+"?linear="+linear+"&angular="+angular);
				try {
					HttpResponse response = client.execute(request);
					response.getEntity().consumeContent();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
}
