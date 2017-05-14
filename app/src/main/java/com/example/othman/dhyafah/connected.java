package com.example.othman.dhyafah;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.bluetooth.BluetoothSocket;
import android.widget.ImageButton;
import android.widget.Toast;


import java.io.IOException;
import java.util.UUID;


public class connected extends AppCompatActivity {

    ImageButton  dbperpare ;

    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btsocket = null;
    String address = null;

    private ProgressDialog progress;
    private boolean isBtConnected = false;

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        final Intent newint = getIntent();
        address = newint.getStringExtra(welcome.EXTRA_ADDRESS); //receive the address of the bluetooth device
        setContentView(R.layout.connected);


        //call the widgtes
        dbperpare = (ImageButton) findViewById(R.id.bperpare);
        new ConnectBT().execute(); //Call the class to connect



        //commands to be sent to bluetooth
        dbperpare.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                making();      //method to making alghwah(coffee)
                Intent newintent =new Intent(getApplicationContext(),preparing.class);
                startActivities(new Intent[]{newintent});

            }
        });



    }

    private void making()
    {
        if (btsocket!=null)
        {
            try
            {
                btsocket.getOutputStream().write("0".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

//call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(connected.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btsocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btsocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btsocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}