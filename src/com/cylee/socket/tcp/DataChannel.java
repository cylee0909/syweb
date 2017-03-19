package com.cylee.socket.tcp;

import com.cylee.web.Log;
import push.AndroidNotification;
import push.PushClient;
import push.android.AndroidCustomizedcast;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by cylee on 16/9/25.
 */
public class DataChannel implements TcpSocketReader.ReadListener{
    private ExecutorService mExecutor = Executors.newCachedThreadPool();
    private static final int DEFAULT_TIMEOUT = 6000; // 5s
    private static final int ERROR_DATA_INVALID = -1;
    private static final int ERROR_SEND_ERROR = -2;
    private static final int ERROR_TIME_OUT = -3;
    int mTimeOut = DEFAULT_TIMEOUT;

    TcpSocketReader reader;
    TcpSocketWriter writer;

    private volatile boolean mStoped;
    public Map<String, PacketBindData> mBindDataMap = Collections.synchronizedMap(new HashMap<String, PacketBindData>());
    private int mId;

    public DataChannel() {
    }

    public void setReader(TcpSocketReader reader) {
        this.reader = reader;
    }

    public void sendString(String data, SendDataListener listener) {
        if (data == null) {
            if (listener != null) {
                listener.onError(ERROR_DATA_INVALID);
            }
            return;
        }
        PacketBindData oldData = mBindDataMap.get(data);
        if (oldData == null) {
            String id = createRequestId();
            oldData = new PacketBindData();
            oldData.senTime = System.currentTimeMillis();
            oldData.mSendId = id;
            oldData.mListener = listener;
            mBindDataMap.put(id, oldData);
            data = correctLength(data, id);
            oldData.mSendData = data;
        } else {
            oldData.senTime = System.currentTimeMillis();
            data = oldData.mSendData;
        }

        try {
            writer.offerData(data);
        } catch (Exception e) {
            mBindDataMap.remove(oldData.mSendId);
            if (listener != null) {
                listener.onError(ERROR_SEND_ERROR);
            }
        }
    }

    private String correctLength(String rawData, String id) {
        return "#"+id+rawData;
    }

    public void closeChannel() {
        if (writer != null) {
            writer.stop();
        }
        if (reader != null) {
            reader.stop();
        }
        mStoped = true;
    }

    @Override
    public void onReadClose() {
        closeChannel();
    }

    @Override
    public void onReceive(String receiveData) {
        if (receiveData != null) {
            if (receiveData.startsWith("INIT")) {
                String addressDataWithId = receiveData.substring(4);
                if (addressDataWithId.length() >= 2) {
                    String id = addressDataWithId.substring(0, 2);
                    // 老的连接还在,但是客户端再次初始化了,我们关闭之前的连接
                    DataChannel oldChannel = ConnectManager.getInstance().getChannel("INIT");
                    if (oldChannel != null) {
                        oldChannel.closeChannel();
                    }
                    try {
                        writer = new TcpSocketWriter(reader.getSocket());
                        new Thread(writer).start();
                        new Thread(new TimeOutChecker()).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                        closeChannel();
                        return;
                    }
                    ConnectManager.getInstance().registerClientChannel("INIT", this);
                    writer.offerData(id + "OK^");
                }
            } else if (receiveData.startsWith("#") && receiveData.length() > 3) {
                String id = receiveData.substring(1, 3);
                PacketBindData pb = mBindDataMap.get(id);
                if (pb != null) {
                    if (pb.mListener != null) {
                        String result = receiveData.substring(3);
                        pb.mListener.onSuccess(result);
                    }
                    mBindDataMap.remove(id);
                }
            } else if (receiveData.startsWith("PUSH")) {
                String id = receiveData.substring(4,6);
                String tokens = receiveData.substring(6);
                Log.d("push id = "+id+" tokens = "+tokens);
                if (tokens != null) {
                    String[] tokenItem = tokens.split(":");
                    if (tokenItem != null) {
                        for (String token : tokenItem) {
                            mExecutor.submit(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Log.d("start push to token "+token);
                                        AndroidCustomizedcast customizedcast = new AndroidCustomizedcast("58ce7460f29d984a1100065f", "qjyas7bhncphrtqpnemwn5p4autfphsn");
                                        customizedcast.setAlias(token, "SELF_ALIAS");
                                        customizedcast.setTicker("警报!");
                                        customizedcast.setTitle("警报");
                                        customizedcast.setText("有设备出现异常,请及时处理!");
                                        customizedcast.goAppAfterOpen();
                                        customizedcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
                                        customizedcast.setProductionMode();
                                        PushClient client = new PushClient(token);
                                        client.send(customizedcast);
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                }
                writer.offerData(id + "OK^");
            }
        }
    }

    private synchronized String createRequestId() {
        mId ++;
        mId %= 0xFF;
        return String.format("%02x", mId);
    }

    static class PacketBindData {
        public long senTime;
        public String mSendId;
        public int mRetryCount;
        public String mSendData;
        public SendDataListener mListener;
    }

    class TimeOutChecker implements Runnable {
        private List<String> removeIds = new ArrayList<>();
        @Override
        public void run() {
            while (!mStoped) {
                try {
                    Thread.sleep(mTimeOut / 2);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                removeIds.clear();
                if (!mBindDataMap.isEmpty()) {
                    Iterator<String> iterator = mBindDataMap.keySet().iterator();
                    while (iterator.hasNext()) {
                        String id = iterator.next();
                        PacketBindData pb = mBindDataMap.get(id);
                        if (pb.senTime + mTimeOut <= System.currentTimeMillis()) { // 超时
                            if (pb.mRetryCount >= 0) {
                                if (pb.mListener != null) {
                                    pb.mListener.onError(ERROR_TIME_OUT);
                                }
                                removeIds.add(id);
                            } else {
                                pb.mRetryCount++;
                                sendString(pb.mSendId, pb.mListener);
                            }
                        }
                    }
                    for (String id : removeIds) {
                        mBindDataMap.remove(id);
                    }
                    removeIds.clear();
                }
            }
        }
    }
 }
