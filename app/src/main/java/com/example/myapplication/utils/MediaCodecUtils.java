package com.example.myapplication.utils;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;

/**
 * good programmer.
 *
 * @date : 2019-08-21 17:03
 * @author: futia
 * @email : futianyi1994@126.com
 * @description :
 */
public class MediaCodecUtils {
    /**
     * selectCodec
     *
     * @param mimeType The mime type of the content.
     * @return MediaCodecInfo
     */
    public static MediaCodecInfo selectCodec(String mimeType) {
        //获取所有支持编解码器数量
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            //编解码器相关性信息存储在MediaCodecInfo中
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            //判断是否为编码器
            if (!codecInfo.isEncoder()) {
                continue;
            }
            //获取编码器支持的MIME类型，并进行匹配
            String[] types = codecInfo.getSupportedTypes();
            for (String type : types) {
                if (type.equalsIgnoreCase(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }
}
