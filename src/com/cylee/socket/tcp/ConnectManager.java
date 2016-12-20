package com.cylee.socket.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    public void registerClientChannel(DataChannel channel) {
        if (channel != null && channel.address != null) {
            mClients.put(channel.address.appid, channel);
        }
    }

    public void removeChannel(DataChannel channel) {
        if (channel != null && channel.address != null) {
            mClients.remove(channel.address.appid);
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
            try {
                TcpSocketReader reader = new TcpSocketReader(socket);
                reader.registerChannel(new DataChannel());
                new Thread(reader).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        mStoped = true;
    }
}
