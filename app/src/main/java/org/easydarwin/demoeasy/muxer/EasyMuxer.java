package org.easydarwin.demoeasy.muxer;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.util.Log;

import org.easydarwin.demoeasy.app.EasyApplication;
import org.easydarwin.demoeasy.bus.StartRecord;
import org.easydarwin.demoeasy.bus.StopRecord;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2018/3/16.
 */

public class EasyMuxer {
    private static final boolean VERBOSE=true;
    private static final String TAG=EasyMuxer.class.getSimpleName();
    private final String mFilePath;
    private MediaMuxer mMuxer;
    private final long durationMillis;//持续时间
    private int index=0;
    private int mVideoTrackIndex=-1;
    private int mAudioTrackIndex=-1;
    private long mBeginMillis;
    private MediaFormat mViewoFormat;
    private MediaFormat mAudioFormat;

    public EasyMuxer(String path,long durationMillis){
        this.mFilePath=path;
        this.durationMillis=durationMillis;

        Object mux=null;

        try{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR2){
                mux=new MediaMuxer(path+"-"+index++ +".map4",MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            mMuxer=(MediaMuxer)mux;
            EasyApplication.BUS.post(new StartRecord());
        }
    }

    public synchronized void addTrack(MediaFormat format,boolean isVideo){
        if(mAudioTrackIndex!=-1&&mVideoTrackIndex!=-1){
            throw new RuntimeException("already add all tracks");
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR2){
            int track=mMuxer.addTrack(format);
            if(VERBOSE)
                Log.i(TAG,String.format("addTrack %s result %d",isVideo?"video":"audio",track));
            if (isVideo){
                mViewoFormat=format;
                mVideoTrackIndex=track;
                if(mAudioTrackIndex!=1){
                    if(VERBOSE)
                        Log.i(TAG,"both audio and added,and muxer is started");
                    mMuxer.start();
                    mBeginMillis=System.currentTimeMillis();
                }
            }else{
                mAudioFormat=format;
                mAudioTrackIndex=track;
                if(mVideoTrackIndex!=-1){
                    mMuxer.start();
                    mBeginMillis=System.currentTimeMillis();
                }
            }
        }
    }

    public synchronized void pumpStram(ByteBuffer outputBuffer, MediaCodec.BufferInfo bufferInfo,boolean isVideo){
        if(mAudioTrackIndex==-1||mVideoTrackIndex==-1){
            Log.i(TAG,String.format("pumpStram[%s] but muxer is not start.i gonre..",isVideo?"video":"audio"));
            return;
        }
        if((bufferInfo.flags&MediaCodec.BUFFER_FLAG_CODEC_CONFIG)!=0){
            //当获取去状态 INFO_OUTPUT_FORMAT_CHANGED 编码数据已经推送至muxer
        }else if(bufferInfo.size!=0){
            if(isVideo&&mVideoTrackIndex==-1){
                throw new RuntimeException("muxer hasn't started");
            }
            outputBuffer.position(bufferInfo.offset);
            outputBuffer.limit(bufferInfo.offset+bufferInfo.size);

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR2){
                mMuxer.writeSampleData(isVideo?mVideoTrackIndex:mAudioTrackIndex,outputBuffer,bufferInfo);
            }
            if(VERBOSE)
                Log.d(TAG,String.format("send %s ["+bufferInfo.size+"] with timestamp:[%d] to muxer",isVideo?"video":"audio",bufferInfo.presentationTimeUs/1000));
        }

        if((bufferInfo.flags&MediaCodec.BUFFER_FLAG_END_OF_STREAM)!=0){
            if(VERBOSE)
                Log.i(TAG,"BUFFER_FLAG_END_OF_STREAM received");
        }

        if(System.currentTimeMillis()-mBeginMillis>=durationMillis){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR2){
                if (VERBOSE)
                    Log.i(TAG, String.format("record file reach expiration.create new file:" + index));
                mMuxer.stop();
                mMuxer.release();
                mMuxer=null;
                mVideoTrackIndex=mAudioTrackIndex=-1;
                try {
                    mMuxer=new MediaMuxer(mFilePath+"-"+ ++index+".mp4",MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                    addTrack(mViewoFormat,true);
                    addTrack(mAudioFormat,false);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void release(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR2){
            if(mMuxer!=null){
                if(mAudioTrackIndex!=-1&&mVideoTrackIndex!=-1){
                    if (VERBOSE)
                        Log.i(TAG, String.format("muxer is started. now it will be stoped."));
                    try {
                        mMuxer.stop();
                        mMuxer.release();
                    } catch (IllegalStateException ex) {
                        ex.printStackTrace();
                    }

                    if (System.currentTimeMillis() - mBeginMillis <= 1500){
                        new File(mFilePath + "-" + index + ".mp4").delete();
                    }
                    mAudioTrackIndex = mVideoTrackIndex = -1;
                    EasyApplication.BUS.post(new StopRecord());
                }
            }
        }
    }
}
