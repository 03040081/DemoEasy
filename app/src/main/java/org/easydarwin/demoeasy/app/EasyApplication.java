package org.easydarwin.demoeasy.app;

import android.app.Application;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import org.easydarwin.demoeasy.bus.StartRecord;
import org.easydarwin.demoeasy.bus.StopRecord;

/**
 * Created by Administrator on 2018/3/16.
 */

public class EasyApplication extends Application {

    private static EasyApplication application;

    public static final Bus BUS=new Bus(ThreadEnforcer.ANY);
    public long mRecordingBegin;//开始录制
    public boolean mRecording;

    public void onCreate(){
        super.onCreate();
        application=this;
        //--？？--暂不存在--初始化 rtmp 服务器地址，使用SharePreference 存储

        //去除初始化 SIM配置信息，设法去除KEY

        BUS.register(this);
    }

    public static EasyApplication getApplication(){
        return application;
    }

    //??通过SharePreferences保存appKEY信息

    //？？通过SharePreferences获取存储中的服务器 IP

    //??通过SharePreferences获取端口

    //???StreamID

    //Server URL


    public static boolean isRTMP(){
        return true;
    }

    @Subscribe
    public void onStartRecord(StartRecord sr){
        mRecording=true;
        mRecordingBegin=System.currentTimeMillis();
    }

    @Subscribe
    public void onStopRecord(StopRecord sr){
        mRecording=false;
        mRecordingBegin=0;
    }


}
