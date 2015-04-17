package com.bipedrobot.ajithvalappil.bipedrobot;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.*;
import android.view.MenuItem;
import android.widget.*;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.content.*;
import android.app.AlertDialog;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import java.io.InputStream;
import java.util.*;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import java.io.IOException;
import java.io.OutputStream;
import android.speech.tts.TextToSpeech;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private Spinner spinner;
    static List<String> items = new ArrayList<String>();
    static final String[]blueDevices = {"item 1", "item 2", "item 3"};
    ArrayAdapter<String> adapter;

    BluetoothController aBluetoothController = new BluetoothController();
    public BluetoothAdapter btAdapter = null;
    public BluetoothSocket btSocket = null;
    public OutputStream outStream = null;
    public InputStream inStream = null;
    public static String address = "88:C9:D0:94:DE:3F";
    static boolean isDevicesConnected = false;
    //ReadData aReadData = new ReadData();
    Button connectBlu;
    static final int REQUEST_ENABLE_BT = 0;
    HashMap<String, String> sndMessage = new HashMap<String, String>();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();

            if (bundle.containsKey("connected")){
                String msgData  = bundle.getString("connected");
                System.out.println("Complete.....>> " + msgData);
                Button connectBlu=(Button)findViewById(R.id.button);
                if (msgData!=null && msgData.equalsIgnoreCase("Connected")){
                    connectBlu.setText("Disconnect");
                    btSocket = aBluetoothController.getBtSocket();
                    outStream = aBluetoothController.getOutStream();
                    inStream = aBluetoothController.getInStream();
                    /*aReadData.setHandler(handler);
                    aReadData.setBtSocket(btSocket);
                    aReadData.setInStream(inStream);
                    aReadData.start();*/
                }else if (msgData!=null && msgData.equalsIgnoreCase("Disconnected")){
                    connectBlu.setText("Connect");
                }
                System.out.println("Complete.....");
            }
            if (bundle.containsKey("message")){
                String msgData  = bundle.getString("message");
                System.out.println("Complete.....>> " + msgData);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (Spinner)findViewById(R.id.spinner);
        connectBlu=(Button)findViewById(R.id.button);
        items.add("Select Bluetooth");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        sndMessage.put("welcome","a");
        sndMessage.put("salute","b");
        sndMessage.put("sit dance","c");
        sndMessage.put("stay still","d");
        sndMessage.put("get up","e");
        sndMessage.put("sit","f");
        sndMessage.put("go left","g");
        sndMessage.put("walk","h");
        sndMessage.put("go right","i");
        sndMessage.put("left turn","j");
        sndMessage.put("stop","k");
        sndMessage.put("right turn","l");
        sndMessage.put("stand up","m");
        sndMessage.put("lay down","n");
        sndMessage.put("fight","o");
        sndMessage.put("push up","p");
        sndMessage.put("push up 1","q");
        sndMessage.put("hand (d)","r");
        sndMessage.put("dance1","s");
        sndMessage.put("dance2","t");
        sndMessage.put("back","u");
        sndMessage.put("<----","v");
        sndMessage.put("---->","w");


        try {
            System.out.println("Starting.....");
            aBluetoothController.setProcessType("init");
            System.out.println("init.....");
            System.out.println("Thread started.....");
            aBluetoothController.start();
            System.out.println("wait for complete started.....");
            aBluetoothController.join();
            System.out.println("Complete.....");
            System.out.println("aBluetoothController.isDeviceHasBluetooth() >>" + aBluetoothController.isDeviceHasBluetooth());
            System.out.println("aBluetoothController.isDeviceBluetoothIsOn() >>" + aBluetoothController.isDeviceBluetoothIsOn());
            if (aBluetoothController.isDeviceHasBluetooth()){
                if (!aBluetoothController.isDeviceBluetoothIsOn()){
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    btAdapter = aBluetoothController.getBtAdapter();
                }else{
                    btAdapter = aBluetoothController.getBtAdapter();
                }
                aBluetoothController = new BluetoothController();
                aBluetoothController.setBtAdapter(btAdapter);
                aBluetoothController.setProcessType("getlist");
                System.out.println("init.....");
                System.out.println("Thread started.....");
                aBluetoothController.start();
                System.out.println("wait for complete started.....");
            }else{
                finish();
            }
        }catch (Exception ee){
            ee.printStackTrace();
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

    public void connectBluetooth(View view) {
        if (btAdapter!=null){
            try {
                String selectedDevice  = spinner.getSelectedItem().toString();
                System.out.println("selectedDevice: " + selectedDevice);
                if (selectedDevice!=null && selectedDevice.contains("\n")) {
                    String data[] = selectedDevice.split("\n");
                    String deviceName = "";
                    String deviceAddress = "";
                    if (data.length == 2) {
                        deviceName = data[0];
                        deviceAddress = data[1];
                    }
                    address = deviceAddress;
                }
                if (!isDevicesConnected){
                    aBluetoothController = new BluetoothController();
                    aBluetoothController.setBtAdapter(btAdapter);
                    aBluetoothController.setProcessType("setup");
                    aBluetoothController.setHandler(handler);
                    aBluetoothController.address =  MainActivity.address;
                    System.out.println("init.....");
                    System.out.println("Thread started.....");
                    //aReadData.setKeepRunning(true);
                    aBluetoothController.start();
                    //readVoiceFromText();
                    Toast.makeText(this, "Connecting...", Toast.LENGTH_SHORT).show();
                }else{
                    connectBlu.setText("Connect");
                    //aReadData.setKeepRunning(false);
                    try {
                        if (btSocket!=null)
                            btSocket.close();
                        isDevicesConnected = false;
                    } catch (Exception e2) {
                        System.out.println("Fatal Error In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                        isDevicesConnected = false;
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                try {
                    if (btSocket!=null)
                        btSocket.close();
                    isDevicesConnected = false;
                } catch (IOException e2) {
                    System.out.println("Fatal Error In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                    isDevicesConnected = false;
                }
                finish();
            }
        });
        builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }


    public void sendMessage(String message){

        System.out.println("message: " + message);
        try {
            byte[] msgBuffer = message.getBytes("UTF-8");
            if (outStream!=null) {
                Toast.makeText(this, "Sending..." + message, Toast.LENGTH_SHORT).show();
                outStream.write(msgBuffer);
                outStream.flush();
            }else{
                Toast.makeText(this, "Please connect to a device...", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            System.out.println("In onResume() and an exception occurred during write: " + e.getMessage());
        }
    }

    public void stand(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }

    public void getup(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }

    public void rest(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }

    public void welcome(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }


    public void sit(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }

    public void moveleft(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }

    public void walk(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }

    public void moveright(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }

    public void Salute(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }


    public void fight(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }

    public void turnleft(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }

    public void stop(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }

    public void rightturn(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }

    public void laydown(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }

    public void pushup(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }

    public void pushup1(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }


    public void handdance(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }

    public void dance1(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }

    public void dance2(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }

    public void backwalk(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }

    public void seeleft(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }


    public void seeright(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        if (buttonText!=null){
            buttonText = buttonText.toLowerCase();
            String dd = (String)sndMessage.get(buttonText);
            if (dd!=null){
                sendMessage(dd);
            }
        }
    }

}
