package com.weiqian.demo;

import com.weiqian.demo.R;
import com.weiqian.clientSocketThread.clientSocketTools;
import com.weiqian.clientSocketThread.ClientSocketThread;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class Socket_demoActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	private Button motor_left,motor_right,motor_stop;
	private ClientSocketThread clientSocketThread;
	//buffer中存放着 server帧，通过修改 buffer中的内容来实现对步进电机的控制
	//控制MOTOR运行时需要在 V DAT中附带一个值，用于控制 MOTOR的旋转方向
	//0x32对应步进电机运行
	private byte[]buffer={(byte)0xFE,(byte)0xE0,0x08,0x32,0x72,0x00,0x02,0x0A};
	
	@Override 
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); setContentView(R.layout.main); 
		// 获取按钮 
		motor_left = (Button) findViewById(R.id.motor_left); 
		motor_right = (Button) findViewById(R.id.motor_right); 
		motor_stop = (Button) findViewById(R.id.motor_stop);
		motor_left.setOnClickListener(this);
		motor_right.setOnClickListener(this);
		motor_stop.setOnClickListener(this);



		//实例化客户端 server线程
		//利用 ClinetSocketThread类的getClientSocket()方法创建一个线程与 server进行通信
		new Thread(new Runnable(){
			public void run() {
				//server里定义port固定为6109
				clientSocketThread=ClientSocketThread.getClientSocket(clientSocketTools.getLocalIpAddress(),6109);				
			}	
		}).start();
	}

	//
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			//更改不同button的buffer里的server帧来控制步进电机不同的操作
			case R.id.motor_left:
				buffer[6]=0x02;
				motor_left.setEnabled(false);
				motor_stop.setEnabled(true);
				motor_right.setEnabled(false);
				break;
			case R.id.motor_right:
				buffer[6]=0x03;
				motor_left.setEnabled(false);
				motor_stop.setEnabled(true);
				motor_right.setEnabled(false);
				break;
			case R.id.motor_stop:
				buffer[6]=0x01;
				motor_left.setEnabled(true);
				motor_stop.setEnabled(false);
				motor_right.setEnabled(true);
				break;
		}
		try {
			clientSocketThread.getOutputStream().write(buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}