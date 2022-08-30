package com.example.myapplication;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

class ConnectThread extends Thread {


    private final String TAG = "ConnectionThread";
    private BluetoothDevice connected_device;
    private BluetoothSocket connected_socket=null;
    private final UUID MyUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter MyBluetoothAdapter;






    public synchronized BluetoothSocket getBTsocket()
    {
        return connected_socket;
    }

    @SuppressLint("MissingPermission")
    ConnectThread(BluetoothDevice device, BluetoothAdapter adapter) {
        Log.d(TAG, "Instantiating ConnectThread");
        MyBluetoothAdapter = adapter;
        BluetoothSocket temp_socket = null;
        connected_device = device;
        try {
            temp_socket = connected_device.createRfcommSocketToServiceRecord(MyUUID);

        } catch (IOException e) {

            Log.d(TAG, e.getMessage());
        }
        connected_socket = temp_socket;


    }


    @SuppressLint("MissingPermission")
    public void run() {

        MyBluetoothAdapter.cancelDiscovery();


        try {

            connected_socket.connect();
            Log.d(TAG, "Initialize connection");
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());

            try {
                connected_socket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }

            return;

        }

        if (connected_socket.isConnected()) {
            Log.d(TAG, "Successfully connected to " + connected_socket.getRemoteDevice().getName());


        }
        else
            Log.d(TAG, "NOT connected to " + connected_device.getName());
    }


    @SuppressLint("MissingPermission")
    public void cancel() {
        try {
            connected_socket.close();
            Log.d(TAG, "Closing the Connection" + connected_device.getName());
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }


}