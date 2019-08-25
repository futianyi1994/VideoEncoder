package com.example.myapplication.presenter;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Surface;
import android.view.TextureView;

import com.bracks.mylib.base.basemvp.BasePresenter;
import com.example.myapplication.contract.MainContract;
import com.example.myapplication.model.MainModel;
import com.example.myapplication.utils.MediaCodecUtils;
import com.example.myapplication.utils.VideoDecoder;
import com.example.myapplication.utils.VideoEncoder;

/**
 * good programmer.
 *
 * @date : 2019-08-14 16:06
 * @author: futia
 * @email : futianyi1994@126.com
 * @description :
 */
public class MainP extends BasePresenter<MainContract.View> implements MainContract.Presenter {
    /**
     * upport h.264
     */
    private final static String MIME_FORMAT = MediaFormat.MIMETYPE_VIDEO_AVC;

    private VideoDecoder mVideoDecoder;
    private VideoEncoder mVideoEncoder;

    private int mPreviewWidth;
    private int mPreviewHeight;

    private MainModel model;

    public MainP() {
        model = new MainModel();
    }

    @Override
    public Camera.PreviewCallback getPreviewCallBack(final int viewwidth, final int viewheight) {
        mPreviewWidth = viewwidth;
        mPreviewHeight = viewheight;
        return (bytes, camera) -> {
            if (mVideoEncoder != null) {
                mVideoEncoder.inputFrameToEncoder(model.swapNV21toNV12(bytes, viewwidth, viewheight));
            }
        };
    }

    @Override
    public TextureView.SurfaceTextureListener getCameraTextureListener() {
        return new TextureView.SurfaceTextureListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                if (getView() != null) {
                    getView().openCamera(surfaceTexture, i, i1);
                    MediaCodecInfo mediaCodecInfo = MediaCodecUtils.selectCodec(MIME_FORMAT);
                    if (mediaCodecInfo == null) {
                        getView().showToast("不支持的MIME_TYPE");
                        return;
                    }
                    mVideoEncoder = new VideoEncoder(mediaCodecInfo.getSupportedTypes()[0], mPreviewHeight, mPreviewWidth);
                    mVideoEncoder.startEncoder();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                if (mVideoEncoder != null) {
                    mVideoEncoder.release();
                }
                if (getView() != null) {
                    getView().closeCamera();
                }
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }
        };
    }

    @Override
    public TextureView.SurfaceTextureListener getDecodeTextureListener() {
        return new TextureView.SurfaceTextureListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                MediaCodecInfo mediaCodecInfo = MediaCodecUtils.selectCodec(MIME_FORMAT);
                if (mediaCodecInfo == null) {
                    if (getView() != null) {
                        getView().showToast("不支持的MIME_TYPE");
                    }
                    return;
                }
                mVideoDecoder = new VideoDecoder(mediaCodecInfo.getSupportedTypes()[0], new Surface(surfaceTexture), mPreviewHeight, mPreviewWidth);
                mVideoDecoder.setEncoder(mVideoEncoder);
                mVideoDecoder.startDecoder();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                mVideoDecoder.stopDecoder();
                mVideoDecoder.release();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }
        };
    }
}
