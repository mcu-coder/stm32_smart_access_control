package com.example.bluetoothintelligentaccesscontrolsystem.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import com.example.bluetoothintelligentaccesscontrolsystem.utils.Common;

import java.io.IOException;
import java.util.UUID;

/**
 * 在蓝牙主动向其它设备连接前，建立服务器线程
 */
public class BTServerThread extends Thread {
    private static final UUID My_UUID = UUID.fromString(Common.MY_UUID);
    private BluetoothServerSocket mServerSocket = null;
    private final BluetoothAdapter mAdapter;
    private Handler handler;
    private BTDataThread dataThread;

    public BTServerThread(BluetoothAdapter mAdapter, Handler handler) {
        this.handler = handler;
        this.mAdapter = mAdapter;
        try {
            mServerSocket = mAdapter.listenUsingRfcommWithServiceRecord("MyBlueToothSDP", My_UUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 运行
     */
    public void run() {
        BluetoothSocket socket;
        while (true) {
            try {
                socket = mServerSocket.accept();
                Common.ISBTCONNECT = true;
                Log.e("蓝牙服务器", "蓝牙服务器启动");
                handler.sendMessage(handler.obtainMessage(Common.MSG_CONNET, ""));
            } catch (IOException e) {
                Log.e("蓝牙服务器", "accept()执行失败", e);
                break;
            }
            if (socket != null) {
                if (Common.ISBTCONNECT) {
                    dataThread = new BTDataThread(socket, handler);
                    dataThread.start();
                } else {
                    try {
                        dataThread.cancel();
                        socket.close();
                        Log.e("蓝牙服务器", "关闭服务器socket");
                    } catch (IOException e) {
                        Log.e("蓝牙服务器", "无法关闭socket", e);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 发送消息
     *
     * @param data
     */
    public void sendData(String data) {
        dataThread.write(data);
    }

    /**
     * 关闭服务器
     */
    public void cancel() {
        Log.d("蓝牙服务器", "连接取消 ");
        try {
            mServerSocket.close();
        } catch (IOException e) {
            Log.e("蓝牙服务器", "服务器关闭失败", e);
        }
        Common.ISBTCONNECT = false;
    }
}
