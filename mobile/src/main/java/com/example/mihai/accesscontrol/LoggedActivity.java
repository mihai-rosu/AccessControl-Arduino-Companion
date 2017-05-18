package com.example.mihai.accesscontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Mihai on 13.05.2017.
 */

public class LoggedActivity extends AppCompatActivity {

    @BindView(R.id.open_button)
    Button openButton;

    @BindView(R.id.pairedListView)
    ListView pairedListView;

    @BindView(R.id.logout_button)
    Button logoutButton;

    @BindView(R.id.logged_user)
    TextView loggedUserView;

    @BindView(R.id.connectedToTextView)
    TextView connectedToTextView;

    @BindView(R.id.refresh_button)
    Button refreshButton;

    private BluetoothAdapter mBtAdapter;
    private LoggedUser loggedUser;
    private BroadcastReceiver mReceiver;
    private ArrayAdapter<String> mDeviceListAdapter;
    private BluetoothSocket btSocket = null;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ConnectedThread mConnectedThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        ButterKnife.bind(this);
        this.loggedUser = new LoggedUser(this.getApplicationContext());
        try {
            User user = loggedUser.getLoggedUser();
            loggedUserView.setText(user.getUsername());
            checkBTState();
            populatePairedList();

        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.logout_button)
    public void logout_button(View view) {
        try {
            loggedUser.setLoggedUser(null);

        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException thrown");
        } catch (NullPointerException e) {
            Toast.makeText(getBaseContext(), "Logged out", Toast.LENGTH_SHORT).show();
        } finally

        {
            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(myIntent);
        }
    }

    @OnClick(R.id.refresh_button)
    public void refresh_button(View view) {
        populatePairedList();
    }


    @OnClick(R.id.open_button)
    public void open_button(View view) {
        try {
            User user = loggedUser.getLoggedUser();
            mConnectedThread.write(user.getUsername() + " " + user.getPassword());
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Not connected", Toast.LENGTH_SHORT).show();
        }
    }

    private void populatePairedList() {
        final Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        final ArrayList<String> listBT = new ArrayList<String>();
        for (BluetoothDevice bt : pairedDevices)
            listBT.add(bt.getName() + " - " + bt.getAddress());
        mDeviceListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listBT);
        pairedListView.setAdapter(mDeviceListAdapter);
        pairedListView.setOnItemClickListener(myListClickListener);
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int position, long id) {
            String device = pairedListView.getItemAtPosition(position).toString();
            //get mac address
            String address = device.split(" - ")[1];

            connectBluetooth(address);
        }
    };

    private void connectBluetooth(String address) {

        BluetoothDevice mBTdevice = mBtAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(mBTdevice);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                //insert code to deal with this
            }
        }

        String name = btSocket.getRemoteDevice().getName();
        if (name != null) {
            connectedToTextView.setText(name);
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return device.createInsecureRfcommSocketToServiceRecord(myUUID);
        //creates secure outgoing connection with BT device using UUID
    }

    private void checkBTState() {
        // Check device has Bluetooth and that it is turned on
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            Toast.makeText(getBaseContext(), "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (mBtAdapter.isEnabled()) {
//                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);

            }
        }
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
//                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}