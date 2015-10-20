package com.example.valerio.bluetootharduino;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLED_BT = 1;

    private BluetoothAdapter meuBluetooth = null;
    private BluetoothSocket btSocket      = null;
    private OutputStream outputStream     = null;
    private String dadosEnvio;
    BluetoothDevice device;
    private String mac = "00:00:12:06:59:16";
    private static final UUID MEU_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        meuBluetooth = BluetoothAdapter.getDefaultAdapter();

        if (meuBluetooth != null && meuBluetooth.isEnabled()) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clickConectar(View view) {
//        for (BluetoothDevice device : meuBluetooth.getBondedDevices()) {
//            Log.i("Bluetooh", device.getAddress());
//        }
//
//        BluetoothDevice device = meuBluetooth.getRemoteDevice(mac);
//        try {
//            btSocket = device.createRfcommSocketToServiceRecord(MEU_UUID);
//            btSocket.connect();
//        } catch (IOException e) {
//            Log.e("Bluetooh ERRO", "ERRO");
//        }

        if(BluetoothAdapter.checkBluetoothAddress(mac)){

            // Get a BluetoothDevice object for the given Bluetooth hardware.
            // Valid Bluetooth hardware addresses must be upper case, in a format
            // such as "00:11:22:33:AA:BB"
            device = meuBluetooth.getRemoteDevice(mac);

        } else{
            Toast.makeText(getApplicationContext(), "Endereço MAC do dispositivo Bluetooth remoto não é válido", Toast.LENGTH_SHORT).show();
        }

        try{

            // Create an RFCOMM BluetoothSocket socket ready to start an insecure
            // outgoing connection to this remote device using SDP lookup of UUID.
            // The RFCOMM protocol emulates the serial cable line settings and
            // status of an RS-232 serial port and is used for providing serial data transfer
            btSocket = device.createInsecureRfcommSocketToServiceRecord(MEU_UUID);

            // Attempt to connect to a remote device.
            btSocket.connect();

            Toast.makeText(getApplicationContext(), "Conectado", Toast.LENGTH_SHORT).show();

        } catch(IOException e){

            Log.e("ERRO AO CONECTAR", "O erro foi" + e.getMessage());
            Toast.makeText(getApplicationContext(), "Conexão não foi estabelecida", Toast.LENGTH_SHORT).show();

        }
    }


    public void clickDesconectar(View view) {
        try {
            if (btSocket != null) {
                btSocket.close();
                btSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dadosEnvio(String letra) {
        try {
            outputStream = btSocket.getOutputStream();
            byte[] msg = letra.getBytes();
            outputStream.write(msg);
            Toast.makeText(getApplicationContext(), "Conectado", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("ERRO AO CONECTAR", "O erro foi" + e.getMessage());
            Toast.makeText(getApplicationContext(), "Conexão não foi estabelecida", Toast.LENGTH_SHORT).show();
        }
    }

    public void clickLigar(View view) {
        if (btSocket != null) {
            dadosEnvio("a");
            Log.i("Brenda", "TE AMO");
        }
    }

    public void clickDesligar(View view) {
        dadosEnvio("b");
    }


}
