package com.weiqian.demo;

import com.weiqian.demo.R;
import com.weiqian.clientSocketThread.MessageListener;
import com.weiqian.clientSocketThread.clientSocketTools;
import com.weiqian.clientSocketThread.ClientSocketThread;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class nfcActivity extends Activity implements OnClickListener{
	private byte[] buffer = {(byte)0xFE,(byte)0xE0,0x08,0x00,0x00,0x00,0x02,0x0A};
	private Button nfc_open, nfc_read;
	private ClientSocketThread clientSocketThread;
	private Handler handler;
	private TextView cardTextShow;
	
	
	
	@Override 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.nfc); 
		// 获取按钮 
		nfc_open = (Button) findViewById(R.id.nfc_open); 
		nfc_read = (Button) findViewById(R.id.nfc_read); 
		nfc_open.setOnClickListener(this);
		nfc_read.setOnClickListener(this);

		cardTextShow = (TextView) findViewById(R.id.show_cardid);		
		cardTextShow.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		handler= new Handler(){
			public void handleMessage(Message msg) {
//				cardTextShow.setTextColor(4);
				cardTextShow.append((String)msg.obj);
//				System.out.println((String)msg.obj);
			}
		};
		
		//实例化客户端 server线程
		//利用 ClinetSocketThread类的getClientSocket()方法创建一个线程与 server进行通信
		new Thread(new Runnable(){
			public void run() {
				//server里定义port固定为6109
				clientSocketThread=ClientSocketThread.getClientSocket(clientSocketTools.getLocalIpAddress(),6109);
				clientSocketThread.setListener(new MessageListener() {
					public void Message(byte[] message, int message_len) {
						//System.arraycopy(message, 1, data, 0, 4);
						handler.sendMessage(handler.obtainMessage(1, "\n" + clientSocketTools.byte2hex(message, message_len)));
					}
				});
			}
		}).start();
		
	}

	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.nfc_open:
				buffer[3] = 0x51;
				buffer[4] = 0x72;
				break;
			case R.id.nfc_read:
				buffer[3] = 0x55;
				buffer[4] = 0x72;
				break;
	}
	try {
		clientSocketThread.getOutputStream().write(buffer);
	} catch (Exception e) {
		e.printStackTrace();
	}
	if(v.getId() == R.id.nfc_read){
		// 模拟读取后调用数据库比对信息
		Intent intent = new Intent(nfcActivity.this, SQLiteActivity.class);
		intent.putExtra("password","1111");
		startActivityForResult(intent,1);
	}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch (requestCode){
			case 1:
				if(resultCode == RESULT_OK){
					String returnedData = data.getStringExtra("visitor");
					Log.d("returnedData",returnedData);
				}
				break;
			default:
		}
	}
}
