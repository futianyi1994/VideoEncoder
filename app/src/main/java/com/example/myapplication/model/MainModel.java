package com.example.myapplication.model;

import com.example.myapplication.contract.MainContract;
import com.example.myapplication.utils.ImageFormatUtils;

import io.reactivex.Observable;

/**
 * good programmer.
 *
 * @date : 2019-08-14 17:12
 * @author: futia
 * @email : futianyi1994@126.com
 * @description :
 */
public class MainModel implements MainContract.Model<byte[]> {

    @Override
    public Observable<byte[]> loadData() {
        return null;
    }

    @Override
    public byte[] swapYV12toI420(byte[] yv12bytes, int width, int height) {
        return ImageFormatUtils.swapYV12toI420(yv12bytes, width, height);
    }

    @Override
    public byte[] swapNV12toI420(byte[] nv12bytes, int width, int height) {
        return ImageFormatUtils.swapNV12toI420(nv12bytes, width, height);
    }

    @Override
    public byte[] swapNV21toNV12(byte[] nv21bytes, int width, int height) {
        nv21bytes = ImageFormatUtils.rotateYUV420Degree90(nv21bytes, width, height);
        return ImageFormatUtils.swapNV21toNV12(nv21bytes, width, height);
    }

    @Override
    public byte[] swapYV12toNV12(byte[] yv12bytes, int width, int height) {
        return ImageFormatUtils.swapYV12toNV12(yv12bytes, width, height);
    }
}
