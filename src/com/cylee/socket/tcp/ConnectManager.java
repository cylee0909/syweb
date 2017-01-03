package com.cylee.socket.tcp;

import com.cylee.web.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
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

    public DataChannel getChannel(String id) {
        return mClients.get(id);
    }

    public void registerClientChannel(DataChannel channel) {
        if (channel != null && channel.address != null) {
            Log.d("register, name = "+channel.address.loginName+" id = "+channel.address.appid);
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

    public String getLoginId(String name, String passd) {
        if (mClients != null) {
            Collection<DataChannel> data = mClients.values();
            if (data != null) {
                for (DataChannel channel :
                        data) {
                    if (channel != null && channel.address != null && channel.address.matchLogin(name, passd)) {
                        return channel.address.appid;
                    }
                }
            }
        }
        return null;
    }
}
