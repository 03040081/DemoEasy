package org.easydarwin.demoeasy.push;

import android.content.Context;

/**
 * Created by Administrator on 2018/3/16.
 */

public interface Pusher {

    public void stop();
    public void initPush(final String serverIP, final String serverPort, final String streamName, final Context context);
    public void initPush(final String url,final Context context,int pts);
    public void initPush(final String url,final Context context);

    public void push(byte[] data,int offset,int length,long timestamp,int type);

    public void push(byte[] data,long timestamp,int type);

}
