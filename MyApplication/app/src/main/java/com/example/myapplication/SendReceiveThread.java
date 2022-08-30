package com.example.myapplication;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class SendReceiveThread extends Thread {


    private final String TAG = "SendReceiveThread";
    private BluetoothSocket BtDeviceSocket;
    private OutputStream OutStream;
    private InputStream InStream;
    private TextView LevelTextview;

    public SendReceiveThread(BluetoothSocket socket,TextView textview) {
        Log.d(TAG, "Instantiating communication thread...");
        BtDeviceSocket = socket;
        InputStream istream = null;
        OutputStream ostream = null;
        LevelTextview=textview;

        try {
            istream = BtDeviceSocket.getInputStream();
            ostream = BtDeviceSocket.getOutputStream();
        } catch (IOException e) {
            Log.d(TAG, "communication thread failed...");
            e.printStackTrace();
        }

        InStream = istream;
        OutStream = ostream;


    }

    public void write(byte[] bytes) {
        try {
            OutStream.write(bytes);
            Log.d(TAG, "Send bytes to the remote device.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        Log.d(TAG, "Reading");
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            // Read from the InputStream
            try {




                    bytes = InStream.read();

                    Log.d(TAG, "InputStream: " + bytes);
                    LevelTextview.setText(bytes+"cm");

            } catch (IOException e) {
                {
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
                    break;
                }
            }
        }


    }
}