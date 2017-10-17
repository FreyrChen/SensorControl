package com.sensorcontrol.util;

import android.content.Context;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class HexStrUtils {

	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase(Locale.getDefault());
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * 数组转换成十六进制字符串
	 * 
	 * @param bArray
	 * @return HexString
	 */
	public static final String bytesToHexString(byte[] bArray) {
		if (bArray == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase(Locale.getDefault()));
		}
		return sb.toString();
	}

	public static final String splitBytesString(String byteString) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < byteString.length(); i++) {
			sb.append(byteString.charAt(i));
			if (sb.length() % 3 == 0 && sb.charAt(sb.length() - 1) != ' ') {
				sb.insert(sb.length() - 1, ' ');
			}
		}
		return sb.toString();
	}

	/**
	 * 读取数据
	 * @param context
	 * @param uri
	 * @return
	 */
	public static byte[] readData(Context context, Uri uri){
		InputStream inputStream = null;
		try {
			inputStream = context.getContentResolver().openInputStream(uri);
			if(inputStream == null) return null;
			return toByteArray(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally {
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
		}
		return output.toByteArray();
	}
}