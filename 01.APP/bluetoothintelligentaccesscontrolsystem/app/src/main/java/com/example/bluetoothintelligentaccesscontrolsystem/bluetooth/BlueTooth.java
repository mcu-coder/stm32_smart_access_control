package com.example.bluetoothintelligentaccesscontrolsystem.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.bluetoothintelligentaccesscontrolsystem.utils.Common;
import com.example.bluetoothintelligentaccesscontrolsystem.utils.MToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class BlueTooth {
    public BluetoothAdapter mAdapter;
    private BluetoothDevice mDevice = null;
//    public BTConnetThread thread = null;
    private Context context;
    public BTServerThread serverThread = null;

    public BlueTooth(Context context) {
        this.context = context;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    /**
     * 蓝牙是否打开
     *
     * @return true - 打开  false -关闭
     */
    public boolean isEnabled() {
        return mAdapter.isEnabled();
    }

    /**
     * 蓝牙状态
     *
     * @return int STATE_OFF        蓝牙已经关闭
     * int STATE_ON        蓝牙已经打开
     * int STATE_TURNING_OFF      蓝牙处于关闭过程中 ，关闭ing
     * int STATE_TURNING_ON        蓝牙处于打开过程中 ，打开ing
     */
    public int isState() {
        return mAdapter.getState();
    }

    /**
     * 取消连接  --- 从最底层断开！
     */
    public void Cancel() {
        if (serverThread != null) {
            serverThread.cancel();
            serverThread = null;
        }
        if (Common.btConnetThread != null) {
            Common.btConnetThread.cancel();
            Common.btConnetThread = null;
        }
    }

    /**
     * 发送消息
     *
     * @param data 消息内容
     */
    public void SendMessage(String data) {
        if (Common.btConnetThread != null) {
        Common.btConnetThread.sendData(data);
        } else if (serverThread != null) {
            serverThread.sendData(data);
        } else {
            MToast.mToast(context, "发送失败");
        }
    }

    /**
     * 获取适配器
     *
     * @return
     */
    public BluetoothAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 扫描未配对设备
     *
     * @param broadcastReceiver 广播接收器
     */
    public void ScanBlueTooth(BroadcastReceiver broadcastReceiver) {
        if (mAdapter.isDiscovering()) { //是否在扫描中
            mAdapter.cancelDiscovery();  //取消扫描
        }
        // 注册Receiver来获取蓝牙设备相关的结果
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND); // 用BroadcastReceiver来取得搜索结果
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(broadcastReceiver, intent);
        mAdapter.startDiscovery(); //开启扫描    在注册广播之后
    }

    /**
     * 扫描已经配对的设备
     *
     * @return hashmap<String, String>  key：设备名称  value：Mac地址
     */
    public List<HashMap<String, String>> ScanPairBlueTooth() {
        List<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>();
        Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();  //获取与本机蓝牙所有绑定的远程蓝牙信息
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                HashMap<String, String> map = new HashMap<>();
                if (device.getName() != null && device.getName().length() > 0) {
                    map.put("btName", device.getName());
                    map.put("btAddress", device.getAddress());
                    mapList.add(map);
                }
            }
        }
        return mapList.size() > 0 ? mapList : null;
    }

    /**
     * 蓝牙服务/连接
     *
     * @param handler 传入一个handler
     * @param address 传入设备地址  如果为空则为启动服务器接收其它设备连接
     */
    public void btConnetOrServer(Handler handler, @Nullable String address) {
        new Thread(() -> {
            if (serverThread != null) {
                serverThread.cancel();
                serverThread = null;
            }
            if (Common.btConnetThread != null) {
                Common.btConnetThread.cancel();
                Common.btConnetThread = null;
            }
            if (address == null) {
                if (mAdapter != null) {
                    serverThread = new BTServerThread(mAdapter, handler);
                    serverThread.start();
                    Log.e("蓝牙配置", "尝试启动服务器");
                } else {
                    serverThread = null;
                    Log.e("蓝牙配置", "适配器为空");
                }
            } else {
                Log.e("测试地址", Common.deviceAddress);
                mDevice = mAdapter.getRemoteDevice(address); //要连接的设备的MAC地址
                Common.btConnetThread = new BTConnetThread(mDevice, mAdapter, handler);
                Common.btConnetThread.start();
                Log.e("蓝牙配置", "尝试去连接");
            }
        }).start();
    }

}
