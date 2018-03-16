package org.easydarwin.demoeasy.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.net.rtp.AudioStream;
import android.os.Process;

import org.easydarwin.demoeasy.muxer.EasyMuxer;
import org.easydarwin.demoeasy.push.Pusher;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2018/3/16.
 */

public class AudioStram {
    private static AudioStram _this;
    EasyMuxer muxer;
    private int samplingRate=8000;
    private int bitRate=16000;
    private int BUFFER_SIZE = 1920;
    int mSamplingRateIndex = 0;
    AudioRecord mAudioRecord;
    MediaCodec mMediaCodec;
    private Thread mThread = null;
    String TAG = "AudioStream";

    protected MediaCodec.BufferInfo mBufferInfo=new MediaCodec.BufferInfo();
    protected ByteBuffer[] mBuffers=null;
    Set<Pusher> sets=new HashSet<>();

    public static final int[] AUDIO_SAMPLING_RATES={96000, // 0
                88200, // 1
                64000, // 2
                48000, // 3
                44100, // 4
                32000, // 5
                24000, // 6
                22050, // 7
                16000, // 8
                12000, // 9
                11025, // 10
                8000, // 11
                7350, // 12
                -1, // 13
                -1, // 14
                -1, // 15
    };
    private Thread mWriter;
    private MediaFormat newFormate;

    public AudioStream(){
        int i=0;
        for(;i<AUDIO_SAMPLING_RATES.length;i++){
            if(AUDIO_SAMPLING_RATES[i]==samplingRate){
                mSamplingRateIndex=i;
                break;
            }
        }
    }

    public void addPusher(Pusher pusher){
        boolean shouldStart=false;
        synchronized (this){
            if(sets.isEmpty())
                shouldStart=true;
            sets.add(pusher);
        }
        if (shouldStart)
            startRecord();
    }

    public void removePusher(Pusher pusher){
        boolean shouldStop=false;
        synchronized (this){
            sets.remove(pusher);
            if(sets.isEmpty())
                shouldStop=true;
        }
        if (shouldStop)
            stop();
    }

    /*
    编码
     */
    private void startRecord(){

        if(mThread!=null)
            return;
        mThread=new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
                int len=0,bufferIndex=0;
                try{
                    int bufferSize=AudioRecord.getMinBufferSize(samplingRate, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        })
    }

    private void stop(){

    }
}
