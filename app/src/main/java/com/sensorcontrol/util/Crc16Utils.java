package com.sensorcontrol.util;

/**
 * Created by lizhe on 2017/11/15 0015.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class Crc16Utils {

    public static char Crc16Calc(byte[] data_arr, int data_len) {
        char crc16 = 0;
        int i;
        for(i =0; i < (data_len); i++) {
            crc16 = (char)(( crc16 >> 8) | (crc16 << 8));
            crc16 ^= data_arr[i]& 0xFF;
            crc16 ^= (char)(( crc16 & 0xFF) >> 4);
            crc16 ^= (char)(( crc16 << 8) << 4);
            crc16 ^= (char)((( crc16 & 0xFF) << 4) << 1);
        }
        return crc16;
    }


}
