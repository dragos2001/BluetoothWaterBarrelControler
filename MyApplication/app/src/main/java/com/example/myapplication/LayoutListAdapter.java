package com.example.myapplication;

import android.Manifest;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class LayoutListAdapter extends ArrayAdapter<BluetoothDevice> {

    //private final int CONNECTION_REQUEST_CODE = 1002;
    private LayoutInflater mLayoutInflater;
    private ArrayList<BluetoothDevice> mDevices;
    private int mViewResourceId;
    private Context mContext;


    public LayoutListAdapter(Context context, int tvResourceId, ArrayList<BluetoothDevice> devices) {
        super(context, tvResourceId, devices);
        mDevices = devices;
        mViewResourceId = tvResourceId;
        mContext=context;
        Log.d("LayoutListAdapter","Object constructed");
    }


    @SuppressLint("MissingPermission")
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d("LayoutListAdapter","Listing device in listview");

        mLayoutInflater=LayoutInflater.from(mContext);
        convertView = mLayoutInflater.inflate(mViewResourceId,parent,false);

        BluetoothDevice device = mDevices.get(position);

        if (device != null) {

            TextView deviceName = (TextView) convertView.findViewById(R.id.Device_name);
            TextView deviceAddress = (TextView) convertView.findViewById(R.id.MAC_address);




                    if (deviceName != null) {

                        deviceName.setText(device.getName());
                        Log.d("MyActivity","DeviceName is :"+ device.getName());
                    }

                    if (deviceAddress != null) {

                        deviceAddress.setText(device.getAddress());
                        Log.d("MyActivity","DeviceMAC is :"+ device.getAddress());
                    }


            }



        return convertView;
    }
}
