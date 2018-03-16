package org.easydarwin.demoeasy.push;


import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaCodec;

import java.lang.ref.WeakReference;

import dagger.Module;

/**
 * Created by Administrator on 2018/3/16.
 */

@Module
public class MediaStream {

    private static final boolean VERBOSE=true;
    private static final int SWITCH_CAMERA=11;
    private final boolean enanleVideo;
    Pusher mEasyPusher;
    static final String TAG="MediaStram";
    int width=640,height=480;
    int framerate,bitraate;
    int mCameraId= Camera.CameraInfo.CAMERA_FACING_BACK;
    MediaCodec mMediaCodec;
    WeakReference<SurfaceTexture> mSurfaceHolderRef;
    Camera mCamera;
    boolean pushStream=false;


}
