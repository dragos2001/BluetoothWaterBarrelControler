package com.example.myapplication;

import android.Manifest;


import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
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

    private final int CONNECTION_REQUEST_CODE = 1002;
    private LayoutInflater mLayoutInflater;
    private ArrayList<BluetoothDevice> mDevices;
    private int mViewResourceId;
    private Context cont;


    public LayoutListAdapter(Context context, int tvResourceId, ArrayList<BluetoothDevice> devices) {
        super(context, tvResourceId, devices);
        mDevices = devices;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = tvResourceId;
        cont = context;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        BluetoothDevice device = mDevices.get(position);

        if (device != null) {
            TextView deviceName = convertView.findViewById(R.id.Device_name);
            TextView deviceAdress = convertView.findViewById(R.id.MAC_address);


            if (ActivityCompat.checkSelfPermission(cont, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                {
                    deviceName.setText(device.getName());

                    if (deviceAdress != null) {
                        deviceAdress.setText(device.getAddress());
                    }
                }
            } else {
                ActivityCompat.requestPermissions((Activity) cont, new String[]{"Manifest.permission.BLUETOOTH_CONNECT"}, CONNECTION_REQUEST_CODE);

            }



        }
        return convertView;
    }
}
