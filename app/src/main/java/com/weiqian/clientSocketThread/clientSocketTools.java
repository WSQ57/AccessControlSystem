package com.weiqian.clientSocketThread;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class clientSocketTools {
	/**
	 * get localhost IP address
	 * 
	 * @return IP address
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()&& !inetAddress.isLinkLocalAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;
	}	
	/**
	 * 获取屏幕的Density
	 * 
	 * @param context
	 *            上下文对象
	 * @return density
	 */
	public static float getScreenDensity(Context context) {
		try {
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager manager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			manager.getDefaultDisplay().getMetrics(dm);
			return dm.density;
		} catch (Exception ex) {

		}
		return 1.0f;
	}
	/** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
	/**
	 * 获取屏幕的DisplayMetrics
	 * 
	 * @param context
	 *            上下文对象
	 * @return DisplayMetrics
	 */
	public static DisplayMetrics getDisplayMetrics(Context context) {
		try {
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager manager = (WindowManager) context .getSystemService(Context.WINDOW_SERVICE);
			manager.getDefaultDisplay().getMetrics(dm);
			return dm;
		} catch (Exception ex) {

		}
		return null;
	}
    /**
     * java字节码转字符串
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b,int len) { //一个字节的数，
        // 转成16进制字符串
        String hs = "";
        String tmp = "";
        for (int n = 0; n < len; n++) {
            //整数转成十六进制表示
            tmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (tmp.length() == 1) {
                hs = hs + "0" + tmp;
            } else {
                hs = hs + tmp;
            }
        }
        tmp = null;
        return hs.toUpperCase(); //转成大写
    }
    /**
     * java字节码转字符串
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b,int start, int len) { //一个字节的数，
        // 转成16进制字符串
        String hs = "";
        String tmp = "";
        for (int n = start; n < len; n++) {
            //整数转成十六进制表示
 
            tmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (tmp.length() == 1) {
                hs = hs + "0" + tmp;
            } else {
                hs = hs + tmp;
            }
        }
        tmp = null;
        return hs.toUpperCase(); //转成大写
    }
    /** 
     * 字节转换为整型
     *  
     * @param b 字节（至少4个字节） 
     * @param index 开始位置 
     * @return 
     */  
    public static int byte2int(byte[] b, int index) {    
        int temp;                                             
        temp = b[index + 0];                                  
        temp &= 0xff;                                         
        temp |= (b[index + 1] << 8);                   
        temp &= 0xffff;                                       
        temp |= (b[index + 2] << 16);                  
        temp &= 0xffffff;                                     
        temp |= (b[index + 3] << 24);                  
        return temp;                    
    }  
    /** 
     * 字节转换为浮点 
     *  
     * @param b 字节（至少4个字节） 
     * @param index 开始位置 
     * @return 
     */  
    public static float byte2float(byte[] b, int index) {    
        int temp;                                             
        temp = b[index + 0];                                  
        temp &= 0xff;                                         
        temp |= ((long) b[index + 1] << 8);                   
        temp &= 0xffff;                                       
        temp |= ((long) b[index + 2] << 16);                  
        temp &= 0xffffff;                                     
        temp |= ((long) b[index + 3] << 24);                  
        return Float.intBitsToFloat(temp);                    
    }  
}
