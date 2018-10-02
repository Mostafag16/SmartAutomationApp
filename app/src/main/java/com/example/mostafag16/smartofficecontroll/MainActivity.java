package com.example.mostafag16.smartofficecontroll;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private BluetoothSocket mBluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private BluetoothAdapter bluetoothAdapter;
    private String selectedDeviceAddress;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    Switch door_switch,garage_switch,Fan_switch,Light1_switch,Light2_switch, Light3_switch,fan_switch2,fan_switch3;
    Button exit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectedDeviceAddress = (String) getIntent().getSerializableExtra("selectedDeviceAddress");
        int code;
        code = 353;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Toast.makeText(getApplicationContext(), "Sending " + code + " | Selected address : " + selectedDeviceAddress, Toast.LENGTH_SHORT).show();
        findSelectedDevice();
        Toast.makeText(getApplicationContext(), "Sending " + code + " | Selected address : " + bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();

        door_switch = findViewById(R.id.door_switch);
        garage_switch =  findViewById(R.id.garage_switch);
        Fan_switch =  findViewById(R.id.Fan_switch);
        Light1_switch =  findViewById(R.id.Light1_switch);
        Light2_switch =  findViewById(R.id.Light2_switch);
        Light3_switch =  findViewById(R.id.Light3_switch);
        fan_switch2 = findViewById(R.id.fan_switch2);
        fan_switch3 = findViewById(R.id.fan_switch3);


        exit = findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**set back to Login Page**/
              // startActivity(new Intent(MainActivity.this, Login.class));
               // set to exit

                finish();
                System.exit(0);
            }
        });

        door_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){

                    connectedThread.write("0");
                    //Toast.makeText(getApplicationContext(), "Door Unlocked" , Toast.LENGTH_SHORT).show();

                }else{

                    //Close the door
                    connectedThread.write("1");
                    //Toast.makeText(getApplicationContext(), "Door Locked" , Toast.LENGTH_SHORT).show();

                }
            }


        });

        garage_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    connectedThread.write("3");
                  //  Toast.makeText(getApplicationContext(), "Garage is open" , Toast.LENGTH_SHORT).show();

                }else{
                    //Close the door
                    connectedThread.write("4");
                    //  Toast.makeText(getApplicationContext(), "Garage is closed" , Toast.LENGTH_SHORT).show();

                }
            }


        });

        Fan_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    //Open
                    connectedThread.write("D");
                }else{
                    //Close
                    connectedThread.write("d");
                }
            }


        });

        Light1_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    connectedThread.write("A");
                }else{

                    //Close
                    connectedThread.write("a");

                }
            }


        });

        Light2_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){

                    connectedThread.write("B");
                }else{

                    //Close
                    connectedThread.write("b");
                }
            }


        });

        Light3_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    connectedThread.write("C");

                }else {
                 //close
                    connectedThread.write("c");

                }
            }


        });

        fan_switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){

                    connectedThread.write("H");
                }else{

                    //Close
                    connectedThread.write("h");
                }
            }


        });

        fan_switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){

                    connectedThread.write("J");
                }else{

                    //Close
                    connectedThread.write("j");
                }
            }


        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        this.connectToOtherDevice();
    }

    public void findSelectedDevice(){

        for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {

            if(device.getAddress().equals(selectedDeviceAddress)){

                this.bluetoothDevice = device;

                break;

            }

        }

    }

    public void connectToOtherDevice(){
//CONNECT TO ANY
        connectThread = new ConnectThread(bluetoothDevice, bluetoothAdapter);
        connectThread.start();
        mBluetoothSocket = connectThread.getMmSocket();
        connectedThread = new ConnectedThread(mBluetoothSocket);
        connectedThread.start();
    }

    @Override
    public void onClick(View v) {

    }


    //create new class for connect thread
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
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        /**write method  and if can't close the application and back to connect page**/
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                finish();

            }
        }

    }

}
