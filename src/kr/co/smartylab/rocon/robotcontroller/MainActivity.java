package kr.co.smartylab.rocon.robotcontroller;

import java.util.HashMap;

import kr.co.smartylab.rocon.robotcontroller.comm.Communication;
import kr.co.smartylab.rocon.robotcontroller.movement.SensorMovementHandler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private ProgressDialog connectionDialog;
	private Button btConnect;
	private EditText etAddr;
	private HashMap<String, TextView> tvs;
	private SensorMovementHandler sensorMovementHandler;
	
	private Handler handler;
	
	private boolean isConnected = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btConnect = (Button) findViewById(R.id.bt_connect);
		btConnect.setOnClickListener(this) ;
		
		etAddr = (EditText) findViewById(R.id.et_addr);

		tvs = new HashMap<String, TextView>();
		tvs.put("linear", (TextView) findViewById(R.id.tv_linear));
		tvs.put("angular", (TextView) findViewById(R.id.tv_angular));
		
		// Initialize the movement handler
		sensorMovementHandler = new SensorMovementHandler(this);
		
		handler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				Bundle data = msg.getData();
				tvs.get(data.get("type")).setText((String) data.get("value"));
			}
		};
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sensorMovementHandler.startMonitoring();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		sensorMovementHandler.stopMonitoring();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_main);
		Log.i("MainActivity", "Config changed.");
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.bt_connect:
			if(!isConnected) {
				String addr = etAddr.getText().toString();
				Log.d("mkk", addr);
				btConnect.setText("Disconnect");
				etAddr.setEnabled(false);
				isConnected = true;
				sensorMovementHandler.setCommunication(new Communication((addr == null || addr.equals(""))?"http://192.168.2.1:8889":addr));
			} else {
				btConnect.setText("Connect");
				etAddr.setEnabled(true);
				isConnected = false;
				sensorMovementHandler.setCommunication(null);
			}
			break;
		default:
			break;
		}
	}
	
	public void updateTextView(String type, String value) {
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("type", type);
		data.putString("value", value);
		msg.setData(data);
		handler.sendMessage(msg);
	}
	
	private void showConnectionProgressDialog()	{
		connectionDialog = new ProgressDialog(MainActivity.this);
		connectionDialog.setTitle("Waiting Detection.");
		connectionDialog.setMessage("Waiting...");
		connectionDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		connectionDialog.setIndeterminate(true);
		connectionDialog.setCancelable(true);
		connectionDialog.show();
		new Thread(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(5000);
				} catch(Throwable ex) {
					ex.printStackTrace();	
				}
				connectionDialog.dismiss();
			}
		}).start();
	}
	
}
