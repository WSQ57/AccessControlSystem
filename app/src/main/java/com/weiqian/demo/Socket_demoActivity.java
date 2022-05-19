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
	//buffer�д���� server֡��ͨ���޸� buffer�е�������ʵ�ֶԲ�������Ŀ���
	//����MOTOR����ʱ��Ҫ�� V DAT�и���һ��ֵ�����ڿ��� MOTOR����ת����
	//0x32��Ӧ�����������
	private byte[]buffer={(byte)0xFE,(byte)0xE0,0x08,0x32,0x72,0x00,0x02,0x0A};
	
	@Override 
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); setContentView(R.layout.main); 
		// ��ȡ��ť 
		motor_left = (Button) findViewById(R.id.motor_left); 
		motor_right = (Button) findViewById(R.id.motor_right); 
		motor_stop = (Button) findViewById(R.id.motor_stop);
		motor_left.setOnClickListener(this);
		motor_right.setOnClickListener(this);
		motor_stop.setOnClickListener(this);



		//ʵ�����ͻ��� server�߳�
		//���� ClinetSocketThread���getClientSocket()��������һ���߳��� server����ͨ��
		new Thread(new Runnable(){
			public void run() {
				//server�ﶨ��port�̶�Ϊ6109
				clientSocketThread=ClientSocketThread.getClientSocket(clientSocketTools.getLocalIpAddress(),6109);				
			}	
		}).start();
	}

	//
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			//���Ĳ�ͬbutton��buffer���server֡�����Ʋ��������ͬ�Ĳ���
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