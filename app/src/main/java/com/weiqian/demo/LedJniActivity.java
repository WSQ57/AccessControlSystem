package com.weiqian.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class LedJniActivity extends Activity {
    /** Called when the activity is first created. */

    /* 相关变量声明 */
    private static int fd;
    private static ImageView[] mImageView = new ImageView[4];
    private static int [] LedState = new int[]{0,0,0,0};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Log.d("LedJniActivity","start");
        String path = new String("/dev/ledtest");
        fd = LedDeviceOpen(path);	//调用jni函数
        LedDeviceIoctl(0, 0);
        LedDeviceIoctl(0, 1);
        LedDeviceIoctl(0, 2);
        LedDeviceIoctl(0, 3);
        LedDeviceIoctl(1, 4);
        for(int i =0; i <= 3; i++) {
            LedDeviceIoctl(1, i);
            try{
            Thread.sleep(1000);}
            catch (InterruptedException e){
                e.printStackTrace();
            }
            LedDeviceIoctl(0, i);
        }
        for(int i =0; i <= 3; i++){
            LedDeviceIoctl(0, 4);
            try{
                Thread.sleep(500);}
            catch (InterruptedException e){
                e.printStackTrace();
            }
            LedDeviceIoctl(1, 4);
            try{
                Thread.sleep(500);}
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        finish();
    }

    /* 添加函数声明,告诉编译和链接器该方法在本地代码中实现 */
    public native int LedDeviceOpen(String path);
    public native void LedDeviceIoctl(int cmd, int arg);
    public native void LedDeviceClose();

    /* 加载JNI代码编译生成的共享库 */
    static {
        System.loadLibrary("led-jni");
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        LedDeviceClose();
        super.onDestroy();
    }

}