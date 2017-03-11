package com.cylee.socket.tcp;

import com.cylee.web.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by cylee on 16/12/19.
 */
public class ConnectManager implements Runnable {
    private ServerSocket mSocket;
    private int mPort;
    private Map<String, DataChannel> mClients = Collections.synchronizedMap(new HashMap<>());
    private volatile boolean mStoped;
    private static ConnectManager manager;

    public static ConnectManager getInstance() {
        if (manager == null) {
            synchronized (ConnectManager.class) {
                manager = new ConnectManager();
            }
        }
        return manager;
    }

    public ConnectManager init(int port) {
        mPort = port;
        return this;
    }

    public DataChannel getChannel(String id) {
        return mClients.get(id);
    }

    public Map<String, DataChannel> allChannels() {
        return mClients;
    }

    public void registerClientChannel(String id, DataChannel channel) {
        if (channel != null) {
            Log.d("register, id = "+ id);
            mClients.put(id, channel);
        }
    }

    @Override
    public void run() {
        while (!mStoped)
            try {
                bind(mPort);
            } catch (Exception e) {
            }
    }

    public void bind(int port) throws IOException {
        mSocket = new ServerSocket(port);
        while (!mStoped) {
            Socket socket = mSocket.accept();
            TcpSocketReader reader = null;
            DataChannel channel = null;
            try {
                reader = new TcpSocketReader(socket);
                channel = new DataChannel();
                reader.registerChannel(channel);
                new Thread(reader).start();
            } catch (Exception e) {
                e.printStackTrace();
                if (reader != null) {
                    reader.stop();
                }
                if (channel != null) {
                    channel.closeChannel();
                }
            }
        }
    }

    public void stop() {
        mStoped = true;
        if (mClients != null) {
            for (DataChannel channel : mClients.values()) {
                if (channel != null) {
                    channel.closeChannel();
                }
            }
        }
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (Exception e) {
            }
        }
    }
}
