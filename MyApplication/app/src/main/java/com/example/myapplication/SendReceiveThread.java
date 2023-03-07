package com.example.myapplication;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



// Thread for sending and receiving data from microcontroller
class SendReceiveThread extends Thread {



    static volatile boolean exit = false;
    private byte ValveON=101;
    private byte ValveOFF=100;
    boolean water_flow=false;
    private final String TAG = "SendReceiveThread";
    private BluetoothSocket BtDeviceSocket;
    private OutputStream OutStream;
    private InputStream InStream;
    private TextView LevelTextview;
    private Context myContext;
    private Button mybtn;

    public SendReceiveThread(BluetoothSocket socket, TextView textview, Context context,Button btn) {
        Log.d(TAG, "Instantiating communication thread...");
        BtDeviceSocket = socket;
        InputStream istream = null;
        OutputStream ostream = null;
        LevelTextview=textview;
        myContext=context;
        mybtn=btn;

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

    public void on_off_valve()
    {
        Log.d(TAG, "Sending water valve command");



            if (water_flow == false) {
                Log.d(TAG, "Send bytes to the remote device to start the valve.");
                water_flow = true;


                byte[]data={ValveON};
                try {
                    OutStream.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mybtn.setText("Stop Valve");
            }

            else if (water_flow == true) {
                Log.d(TAG, "Send bytes to the remote device to stop the valve.");
                water_flow = false;


                byte[]data={ValveOFF};
                try {
                    OutStream.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mybtn.setText("Start Valve");
            }



        }




    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        Log.d(TAG, "Reading");
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (!exit) {
            // Read from the InputStream
            try {




                    bytes = InStream.read();
                    Log.d(TAG, "InputStream: " + bytes);
                    LevelTextview.setText(bytes+"cm");

            } catch (IOException e) {
                {
                    Log.e(TAG, "write: Error reading Input Stream. ");
                    break;
                }
            }
        }


    }

    public void cancel() {


        Log.d(TAG, "water_flow is " + water_flow);
        if(water_flow==true) {
            Log.d(TAG, "yes" );
            on_off_valve();

        }
        else Log.d(TAG, "no" );



        try {
            BtDeviceSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        exit=true;


    }

}