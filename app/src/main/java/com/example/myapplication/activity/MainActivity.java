package com.example.myapplication.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.TextureView;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.example.myapplication.R;
import com.example.myapplication.contract.MainContract;
import com.example.myapplication.presenter.MainP;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseUi<MainContract.View, MainP> implements MainContract.View {

    @BindView(R.id.camera)
    TextureView mCameraTexture;
    @BindView(R.id.decoder)
    TextureView mDecodeTexture;


    private final static String TAG = "MainActivity";

    private Camera mCamera;


    @Override
    protected MainP creatPresenter() {
        return new MainP();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.i("TEST", "Granted");
            mCameraTexture.setSurfaceTextureListener(mCameraTextureListener);
            mDecodeTexture.setSurfaceTextureListener(mDecodeTextureListener);
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                showToast("请到设置中开启摄像头权限");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        }*/
        if (PermissionUtils.isGranted(Manifest.permission.CAMERA)) {
            Log.i("TEST", "Granted");
            mCameraTexture.setSurfaceTextureListener(getPresenter().getCameraTextureListener());
            mDecodeTexture.setSurfaceTextureListener(getPresenter().getDecodeTextureListener());
        } else {
            PermissionUtils
                    .permission(PermissionConstants.CAMERA)
                    .rationale(shouldRequest -> shouldRequest.again(true))
                    .callback(new PermissionUtils.FullCallback() {
                        @Override
                        public void onGranted(List<String> permissionsGranted) {
                            mCameraTexture.setSurfaceTextureListener(getPresenter().getCameraTextureListener());
                            mDecodeTexture.setSurfaceTextureListener(getPresenter().getDecodeTextureListener());
                        }

                        @Override
                        public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                            if (permissionsDeniedForever.size() > 0) {
                                showToast("请在设置中开启摄像头权限");
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, 2);
                            } else {
                                showToast("开启摄像头权限失败");
                            }
                        }
                    })
                    .request();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CANCELED) {
            if (PermissionUtils.isGranted(Manifest.permission.CAMERA)) {
                mCameraTexture.setSurfaceTextureListener(getPresenter().getCameraTextureListener());
                mDecodeTexture.setSurfaceTextureListener(getPresenter().getDecodeTextureListener());
            }
        }
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (permissions[0].equals(Manifest.permission.CAMERA) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mCameraTexture.setSurfaceTextureListener(mCameraTextureListener);
                    mDecodeTexture.setSurfaceTextureListener(mDecodeTextureListener);
                } else {
                    showToast("获取摄像头权限失败！");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/

    @Override
    public void openCamera(SurfaceTexture texture, int width, int height) {
        if (texture == null) {
            Log.e(TAG, "openCamera need SurfaceTexture");
            return;
        }

        mCamera = Camera.open(0);
        try {
            mCamera.setPreviewTexture(texture);
            mCamera.setDisplayOrientation(90);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewFormat(ImageFormat.NV21);
            List<Camera.Size> list = parameters.getSupportedPreviewSizes();
            for (Camera.Size size : list) {
                System.out.println("----size width = " + size.width + " size height = " + size.height);
            }

            int mPreviewWidth = 640;
            int mPreviewHeight = 480;
            parameters.setPreviewSize(mPreviewWidth, mPreviewHeight);
            mCamera.setParameters(parameters);
            mCamera.setPreviewCallback(getPresenter().getPreviewCallBack(mPreviewWidth, mPreviewHeight));
            mCamera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            mCamera = null;
        }
    }

    @Override
    public void closeCamera() {
        if (mCamera == null) {
            Log.e(TAG, "Camera not open");
            return;
        }
        mCamera.stopPreview();
        mCamera.release();
    }
}
