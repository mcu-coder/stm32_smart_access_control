package com.example.bluetoothintelligentaccesscontrolsystem.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import com.example.bluetoothintelligentaccesscontrolsystem.utils.Common;

import java.io.IOException;
import java.util.UUID;

/**
 * 作为客户端去连接其它设备
 */
public class BTConnetThread extends Thread {
    private static final UUID My_UUID = UUID.fromString(Common.MY_UUID);
    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private BluetoothAdapter mAdapter;
    public BTDataThread btDataThread;
    private final Handler mHandler;

    public BTConnetThread(BluetoothDevice mDevice, BluetoothAdapter mAdapter, Handler mHandler) {
        this.mDevice = mDevice;
        this.mAdapter = mAdapter;
        this.mHandler = mHandler;
        BluetoothSocket tt = null;
        try {
            tt = mDevice.createRfcommSocketToServiceRecord(My_UUID);
        } catch (IOException ignored) {
        }
        mSocket = tt;
    }

    /**
     * 开启连接
     */
    public void run() {
        mAdapter.cancelDiscovery(); //停止搜索
        try {
            mSocket.connect();
            Log.e("蓝牙套接字", "连接");
            mHandler.sendMessage(mHandler.obtainMessage(Common.MSG_CONNET, ""));
        } catch (Exception e) {
            mHandler.sendMessage(mHandler.obtainMessage(Common.MSG_ERROR, e.toString()));
            try {
                mSocket.close();
                Common.ISBTCONNECT = false;
            } catch (IOException ignored) {
            }
            return;
        }
        Common.ISBTCONNECT = true;
        if (btDataThread != null) {
            btDataThread.cancel();
        }
        btDataThread = new BTDataThread(mSocket, mHandler);
        btDataThread.start();
    }

    /**
     * 发送消息
     *
     * @param data
     */
    public void sendData(String data) {
        btDataThread.write(data);
    }

    public void cancel() {
        try {
            btDataThread.cancel();
            mSocket.close();
            Common.ISBTCONNECT = false;
            Log.e("蓝牙", "关闭蓝牙客户端连接");
        } catch (Exception e) {
            Log.e("蓝牙", e.toString());
        }
    }

}
