package com.example.myapplication.utils;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * good programmer.
 *
 * @date : 2019-08-13 15:30
 * @author: futia
 * @email : futianyi1994@126.com
 * @description :
 */
public class VideoEncoder {
    private final static String TAG = "VideoEncoder";
    private final static int CONFIGURE_FLAG_ENCODE = MediaCodec.CONFIGURE_FLAG_ENCODE;
    private final static int CACHE_BUFFER_SIZE = 8;

    private MediaCodec mMediaCodec;
    private MediaFormat mMediaFormat;
    private int mViewWidth;
    private int mViewHeight;

    private Handler mVideoEncoderHandler;
    private HandlerThread mVideoEncoderHandlerThread = new HandlerThread("VideoEncoder");

    /**
     * This video stream format must be I420
     */
    private final static ArrayBlockingQueue<byte[]> mInputDatasQueue = new ArrayBlockingQueue<byte[]>(CACHE_BUFFER_SIZE);
    /**
     * Cachhe video stream which has been encoded.
     */
    private final static ArrayBlockingQueue<byte[]> mOutputDatasQueue = new ArrayBlockingQueue<byte[]>(CACHE_BUFFER_SIZE);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private MediaCodec.Callback mCallback = new MediaCodec.Callback() {
        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec mediaCodec, int id) {
            ByteBuffer inputBuffer = mediaCodec.getInputBuffer(id);
            inputBuffer.clear();
            byte[] dataSources = mInputDatasQueue.poll();
            int length = 0;
            if (dataSources != null) {
                inputBuffer.put(dataSources);
                length = dataSources.length;
            }
            mediaCodec.queueInputBuffer(id, 0, length, 0, 0);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec mediaCodec, int id, @NonNull MediaCodec.BufferInfo bufferInfo) {
            ByteBuffer outputBuffer = mediaCodec.getOutputBuffer(id);
            MediaFormat outputFormat = mediaCodec.getOutputFormat(id);
            if (outputBuffer != null && bufferInfo.size > 0) {
                byte[] buffer = new byte[outputBuffer.remaining()];
                outputBuffer.get(buffer);
                boolean result = mOutputDatasQueue.offer(buffer);
                if (!result) {
                    Log.d(TAG, "Offer to queue failed, queue in full state");
                }
            }
            mediaCodec.releaseOutputBuffer(id, true);
        }

        @Override
        public void onError(@NonNull MediaCodec mediaCodec, @NonNull MediaCodec.CodecException e) {
            Log.d(TAG, "------> onError");
        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec mediaCodec, @NonNull MediaFormat mediaFormat) {
            Log.d(TAG, "------> onOutputFormatChanged");
        }
    };

    public VideoEncoder(String mimeType, int viewwidth, int viewheight) {
        try {
            mMediaCodec = MediaCodec.createEncoderByType(mimeType);
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            mMediaCodec = null;
            return;
        }

        this.mViewWidth = viewwidth;
        this.mViewHeight = viewheight;

        mVideoEncoderHandlerThread.start();
        mVideoEncoderHandler = new Handler(mVideoEncoderHandlerThread.getLooper());

        mMediaFormat = MediaFormat.createVideoFormat(mimeType, mViewWidth, mViewHeight);
        //指定编码器颜色格式
        mMediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        //指定比特率
        mMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1920 * 1280);
        //指定帧率
        mMediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        //指定关键帧时间间隔
        mMediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
    }

    /**
     * Input Video stream which need encode to Queue
     *
     * @param needEncodeData I420 format stream
     */
    public void inputFrameToEncoder(byte[] needEncodeData) {
        boolean inputResult = mInputDatasQueue.offer(needEncodeData);
        Log.d(TAG, "-----> inputEncoder queue result = " + inputResult + " queue current size = " + mInputDatasQueue.size());
    }

    /**
     * Get Encoded frame from queue
     *
     * @return a encoded frame; it would be null when the queue is empty.
     */
    public byte[] pollFrameFromEncoder() {
        return mOutputDatasQueue.poll();
    }

    /**
     * start the MediaCodec to encode video data
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startEncoder() {
        if (mMediaCodec != null) {
            mMediaCodec.setCallback(mCallback, mVideoEncoderHandler);
            mMediaCodec.configure(mMediaFormat, null, null, CONFIGURE_FLAG_ENCODE);
            mMediaCodec.start();
        } else {
            throw new IllegalArgumentException("startEncoder failed,is the MediaCodec has been init correct?");
        }
    }

    /**
     * stop encode the video data
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void stopEncoder() {
        if (mMediaCodec != null) {
            mMediaCodec.stop();
            mMediaCodec.setCallback(null);
        }
    }

    /**
     * release all resource that used in Encoder
     */
    public void release() {
        if (mMediaCodec != null) {
            mInputDatasQueue.clear();
            mOutputDatasQueue.clear();
            mMediaCodec.release();
        }
    }
}
