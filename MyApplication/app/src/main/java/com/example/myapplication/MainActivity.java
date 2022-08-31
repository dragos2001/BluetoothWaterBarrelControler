package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.Manifest;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "MyActivity";
    private final int SCAN_REQUEST_CODE = 1001;
    private final int CONNECTION_REQUEST_CODE = 1002;




    private Button buttonen;
    private Button buttonsend;
    private Button buttonwater;
    private Button buttonsearch;
    private TextView status;
    private TextView level;


    private ConnectThread ConnectionThread;
    private BluetoothAdapter MyBluetoothAdapter;
    private ArrayList<BluetoothDevice> Bluetooth_Devices_ArrayList=new ArrayList<>() ;
    private LayoutListAdapter Layout_Adapter;
    private ListView ListForDiscoveredDevices;
    private SendReceiveThread  DataThread;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



      buttonen = findViewById(R.id.btn1);
      buttonwater = findViewById(R.id.btn2);
      buttonsearch = findViewById(R.id.btn3);
      buttonsend=findViewById(R.id.btn4);
      status = findViewById(R.id.txtw);
      level = findViewById(R.id.lvlw);


        Bluetooth_Devices_ArrayList = new ArrayList<>();

        MyBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

         ListForDiscoveredDevices = (ListView)findViewById(R.id.list_view);



        buttonen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                enableordisableBT();

            }
        });

        buttonsearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                SearchForBTdevices();
            }


        });


        buttonsend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                receive_water_level();

            }


        });


        buttonwater.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                fill_barrel();
            }


        });


        IntentFilter ConnectionState= new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(mBroadcastReceiver4, ConnectionState);
        ListForDiscoveredDevices.setOnItemClickListener(this);


    }


    @SuppressLint("MissingPermission")
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        MyBluetoothAdapter.cancelDiscovery();
        BluetoothDevice device = Bluetooth_Devices_ArrayList.get(i);
        @SuppressLint("MissingPermission")
        String device_name=device.getName();
        String mac_address=device.getAddress();
        Log.d(TAG, "You Clicked" +device_name + " " + mac_address );
        ConnectionThread= new ConnectThread(device,MyBluetoothAdapter);
        ConnectionThread.start();
        try {
            ConnectionThread.join();
        } catch (InterruptedException e) {
            Log.e(TAG,e.getMessage());
        }
        DataThread=new SendReceiveThread(ConnectionThread.getBTsocket(),level,this,buttonwater);
        DataThread.start();
    }






    //TODO: ENABLE BLUETOOTH

    public void enableordisableBT() {



       if (MyBluetoothAdapter == null) {
            Log.d(TAG,"Bluetooth not supported");
        } else if (!MyBluetoothAdapter.isEnabled()) {
            Log.d(TAG,"Bluetooth is currently not enabled");





            if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "You have already granted this permission!", Toast.LENGTH_SHORT).show();

                MyBluetoothAdapter.enable();
                IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                registerReceiver(mBroadcastReceiver1, BTIntent);

            }
            else
            {
                Toast.makeText(MainActivity.this, "You have been granted the permission!", Toast.LENGTH_SHORT).show();
                requestConnectPermission();
            }



            } else if (MyBluetoothAdapter.isEnabled()) {
                MyBluetoothAdapter.disable();


                IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                registerReceiver(mBroadcastReceiver1, BTIntent);

            }


        }


    private void requestConnectPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, CONNECTION_REQUEST_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, CONNECTION_REQUEST_CODE);
            Log.d(TAG," permisiune in curs de cerere ");
        }

    }


        //TODO: DISCOVER DEVICES



    public void SearchForBTdevices() {


        Log.d(TAG,"App is going to search for devices");

        if(MyBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "App searches for devices-1");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "App searches for devices-2");
                //TODO: START DISCOVERY
                if (MyBluetoothAdapter.isDiscovering()) {

                    MyBluetoothAdapter.cancelDiscovery();

                    checkBTPermissions();
                    Toast.makeText(MainActivity.this, "Searching for devices is restarted", Toast.LENGTH_LONG).show();

                    MyBluetoothAdapter.startDiscovery();
                    IntentFilter searchDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mBroadcastReceiver3, searchDevicesIntent);


                } else {

                    Toast.makeText(MainActivity.this, "Searching...", Toast.LENGTH_LONG).show();
                    checkBTPermissions();
                    MyBluetoothAdapter.startDiscovery();
                    IntentFilter searchDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mBroadcastReceiver3, searchDevicesIntent);

                }
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, SCAN_REQUEST_CODE);
            }


        }
        else Toast.makeText(MainActivity.this, "Enable Bluetooth", Toast.LENGTH_LONG).show();
    }



    //TODO: LOOK FOR BLUETOOTH ENABLING STATE
    public BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, MyBluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(MainActivity.this, "STATE OFF", Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Toast.makeText(MainActivity.this, "STATE TURNING OFF", Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(MainActivity.this, "STATE ON", Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Toast.makeText(MainActivity.this, "STATE TURNING ON", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
    };


    //TODO: List DEVICES

    public BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {



        @SuppressLint("MissingPermission")
        public void onReceive(Context context, Intent intent) {


            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                Log.d(TAG,"Device found ");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(!Bluetooth_Devices_ArrayList.contains(device)) {
                    Bluetooth_Devices_ArrayList.add(device);
                    Log.d(TAG, "Name is :" + device.getName());
                    LayoutListAdapter Layout_Adapter = new LayoutListAdapter(context, R.layout.list_layout, Bluetooth_Devices_ArrayList);
                    ListForDiscoveredDevices.setAdapter(Layout_Adapter);


                }
            }
        }
    };


    private BroadcastReceiver mBroadcastReceiver4= new BroadcastReceiver()
    {


        @SuppressLint("MissingPermission")
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {


                BluetoothDevice c_device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(MainActivity.this, "Connected to "+ c_device.getName(), Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(MainActivity.this, "Not connected", Toast.LENGTH_LONG).show();
            }

        }

    };


    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case CONNECTION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this, "Permission Granted,access to connection available", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied, access to connection unavailable, app requires BLUETOOTH_CONNECT permission in order to operate", Toast.LENGTH_LONG).show();

                }
                break;
            case SCAN_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this, "Permission Granted,access to scan available", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied, access to scan unavailable, app requires BLUETOOTH_SCAN permission in order to operate", Toast.LENGTH_LONG).show();

                }
                break;
        }

    }

    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }


    private void fill_barrel()
    {


        if(ConnectionThread!=null)
        {
            DataThread.on_off_valve();
        }


    }

    private void receive_water_level()
    {
        Log.d(TAG,"Request for receiving data ...");

        if(ConnectionThread!=null)
        {
            byte[]data_b={1};
            DataThread.write(data_b);
        }

    }






    @SuppressLint("MissingSuperCall")
    protected void onDestroy() {

        Log.d(TAG, "onDestroy: called.");

        DataThread.cancel();
        ConnectionThread.cancel();

        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);

    }
}













