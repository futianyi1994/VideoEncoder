package com.example.myapplication.contract;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.TextureView;

import com.bracks.mylib.base.basemvp.BaseModel;
import com.bracks.mylib.base.basemvp.BasePresenterInter;
import com.bracks.mylib.base.basemvp.BaseView;

/**
 * good programmer.
 *
 * @date : 2019-08-14 16:08
 * @author: futia
 * @email : futianyi1994@126.com
 * @description :
 */
public interface MainContract {
    interface View extends BaseView {
        void openCamera(SurfaceTexture surfaceTexture, int i, int i1);

        void closeCamera();
    }

    interface Presenter extends BasePresenterInter<View> {
        Camera.PreviewCallback getPreviewCallBack(final int viewwidth, final int viewheight);

        TextureView.SurfaceTextureListener getCameraTextureListener();

        TextureView.SurfaceTextureListener getDecodeTextureListener();
    }

    interface Model<M> extends BaseModel<M> {
        byte[] swapYV12toI420(byte[] bytes, int width, int height);

        byte[] swapNV12toI420(byte[] nv12bytes, int width, int height);

        byte[] swapNV21toNV12(byte[] nv21bytes, int width, int height);

        byte[] swapYV12toNV12(byte[] yv12bytes, int width, int height);
    }
}
