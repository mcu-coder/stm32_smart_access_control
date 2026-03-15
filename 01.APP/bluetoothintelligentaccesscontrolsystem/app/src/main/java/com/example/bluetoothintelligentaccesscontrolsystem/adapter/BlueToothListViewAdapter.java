package com.example.bluetoothintelligentaccesscontrolsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bluetoothintelligentaccesscontrolsystem.R;

import java.util.HashMap;
import java.util.List;

public class BlueToothListViewAdapter extends BaseAdapter {
    private List<HashMap<String, String>> data;
    private LayoutInflater layoutInflater;
    private Context context;

    /**
     * @param data    hashmap，key为设备名，value为设备地址
     * @param context
     */
    public BlueToothListViewAdapter(List<HashMap<String, String>> data, Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);//context :要使用当前的Adapter的界面对象 layoutInflater: 布局装载器对象
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;

        if (view == null) {
            viewHolder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.bluetooth_value, null);
            viewHolder.deviceName = (TextView) view.findViewById(R.id.btName);
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.btLocation);
            viewHolder.viewLayout = (RelativeLayout) view.findViewById(R.id.itemLayout);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.deviceName.setText(data.get(position).get("btName"));
        viewHolder.deviceAddress.setText("MAC地址：" + data.get(position).get("btAddress"));

        return view;
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        View viewLayout;
    }
}
