package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.Manifest;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyActivity";
    private final int SCAN_REQUEST_CODE = 1001;
    private final int CONNECTION_REQUEST_CODE = 1002;


    private Button buttonen;
    private Button buttoncon;
    private Button buttonsearch;
    private TextView status;
    private TextView level;
    private ListView ListForDiscoveredDevices;


    private BluetoothAdapter MyBluetoothAdapter;
    private ArrayList<BluetoothDevice> Bluetooth_Devices_ArrayList = new ArrayList<>();
    public LayoutListAdapter Layout_Adapter;
    private ListView Devices_ListView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonen = findViewById(R.id.btn1);
        Button buttoncon = findViewById(R.id.btn2);
        Button buttonsearch = findViewById(R.id.btn3);
        TextView status = findViewById(R.id.txtw);
        TextView level = findViewById(R.id.lvlw);
        ListView ListForDiscoveredDevices = findViewById(R.id.list_view);
        ListView Devices_ListView = findViewById(R.id.list_view);

        MyBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


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


    }


    // TODO: LOOK FOR BLUETOOTH ENABLING STATE
    private BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {

                //TODO: START DISCOVERY
                if (MyBluetoothAdapter.isDiscovering()) {

                    MyBluetoothAdapter.cancelDiscovery();

                    checkBTPermissions();
                    Toast.makeText(MainActivity.this, "Searching for devices is restarted", Toast.LENGTH_LONG).show();

                    MyBluetoothAdapter.startDiscovery();
                    IntentFilter searchDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mBroadcastReceiver3, searchDevicesIntent);


                } else {

                    checkBTPermissions();
                    MyBluetoothAdapter.startDiscovery();
                    IntentFilter searchDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mBroadcastReceiver3, searchDevicesIntent);

                }
            } else {
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN}, SCAN_REQUEST_CODE);
            }

        }


    }


    //TODO: List DEVICES

    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Bluetooth_Devices_ArrayList.add(device);

                Layout_Adapter = new LayoutListAdapter(context, R.layout.list_layout, Bluetooth_Devices_ArrayList);
                ListForDiscoveredDevices.setAdapter(Layout_Adapter);
            }
        }
    };

    /*
    TODO: NOT NECESSARY FOR THIS APP !!!!!!

    //ENABLE DISCOVERABILITY
    public void enableordisableDiscoverability() {

        Intent Discoverintent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(Discoverintent);

        IntentFilter DiscoverIntent = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2, DiscoverIntent);


    }

    //TODO: LOOK FOR DISCOVERABILITY STATE
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };
*/


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

    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver3);


    }
}