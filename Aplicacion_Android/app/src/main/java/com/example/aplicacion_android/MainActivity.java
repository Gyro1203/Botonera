package com.example.aplicacion_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button btnAXIS, btn1, btn2, btn3, btn4, btn5, btnPLUS, btnSUB;
    TextView tv1, tv2, tv3, tv4;
    boolean disabled = false;
    char group = 'A';
    int speedA = 50, speedB = 50, speedL = 50, move = 0, moveL = 0, resta = 0;
    String mode;
    private boolean pressed = false;
    private Handler repeatUpdateHandler = new Handler();

    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;
    // Identificador unico de servicio - SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la direccion MAC
    private static String address = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String Message = (String) msg.obj;
                    Log.d("BT", "Message: "+Message);

                    tv1.setText(Message);

                }
            }
        };

//        class RptUpdater implements Runnable {
//            public void run() {
//                if( pressed ){
//                    enviar();
//                    repeatUpdateHandler.postDelayed( new RptUpdater(), 500 );
//                }
//            }
//        }

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        VerificarEstadoBT();

        btnAXIS = findViewById(R.id.btnAXIS);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btnPLUS = findViewById(R.id.btnPLUS);
        btnSUB = findViewById(R.id.btnSUB);

        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);

        mode = getString(R.string.joints);


        btnAXIS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button)view;
                String text = b.getText().toString();
                state(text);
            }
        });
    }


//        btnPLUS.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MyConexionBT.write("1");
//            }
//        });
//
//        btnSUB.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                pressed = true;
//                repeatUpdateHandler.post( new RptUpdater() );
//                return false;
//            }
//        });
//
//        button20.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if( (motionEvent.getAction()==MotionEvent.ACTION_UP || motionEvent.getAction()==MotionEvent.ACTION_CANCEL)
//                        && pressed ){
//                    pressed = false;
//                }
//                return false;
//            }
//        });
//    }
//
//
//    public void enviar(){
//        MyConexionBT.write("q");
//    }

    public void state(String text){
        if(text.equals(getString(R.string.joints)) && tv3.getText().toString().equals("")){
            tv1.setText("");
            mode = getString(R.string.xyz);
            tv4.setText(String.format("group:%s     ax:%s       %s", group, getString(R.string.voidAxis), mode));
            btnAXIS.setText(getString(R.string.xyz));
            btn1.setText(getString(R.string.x));
            btn2.setText(getString(R.string.y));
            btn3.setText(getString(R.string.z));
            btn4.setText(getString(R.string.p));
            btn5.setText(getString(R.string.r));
            btnAXIS.setBackgroundColor(getResources().getColor(R.color.blue));
            btn1.setBackgroundColor(getResources().getColor(R.color.blue));
            btn2.setBackgroundColor(getResources().getColor(R.color.blue));
            btn3.setBackgroundColor(getResources().getColor(R.color.blue));
            btn4.setBackgroundColor(getResources().getColor(R.color.blue));
            btn5.setBackgroundColor(getResources().getColor(R.color.blue));
        }else{
            tv1.setText("");
            mode = getString(R.string.joints);
            tv4.setText(String.format("group:%s     ax:%s       %s", group, getString(R.string.voidAxis), mode));
            btnAXIS.setText(getString(R.string.joints));
            btn1.setText(getString(R.string.uno));
            btn2.setText(getString(R.string.dos));
            btn3.setText(getString(R.string.tres));
            btn4.setText(getString(R.string.cuatro));
            btn5.setText(getString(R.string.cinco));
            btnAXIS.setBackgroundColor(getResources().getColor(R.color.gray));
            btn1.setBackgroundColor(getResources().getColor(R.color.gray));
            btn2.setBackgroundColor(getResources().getColor(R.color.gray));
            btn3.setBackgroundColor(getResources().getColor(R.color.gray));
            btn4.setBackgroundColor(getResources().getColor(R.color.gray));
            btn5.setBackgroundColor(getResources().getColor(R.color.gray));
        }
    }

    public void speed(View view){
        tv1.setText("");
        resta = 0;
        if(mode.equals("XYZ")){
            state("XYZ");
            tv2.setText(String.format("Speed%s %s%% ", "L", speedL));
            tv3.setText(String.format("Speed%s (%s%%) ", "L", speedL));
        }else if(group == 'A'){
            tv2.setText(String.format("Speed%s %s%% ", group, speedA));
            tv3.setText(String.format("Speed%s (%s%%) ", group, speedA));
        }else if(group == 'B'){
            tv2.setText(String.format("Speed%s %s%% ", group, speedB));
            tv3.setText(String.format("Speed%s (%s%%) ", group, speedB));
        }
    }

    public void move(View view){
        tv1.setText("");
        resta = 0;
        if(mode.equals("XYZ")){
            state("XYZ");
            tv3.setText(String.format("%s", "moveL "));
        }else{
            tv3.setText(String.format("%s", "MOVE "));
        }

    }

    public void run(View view){
        tv1.setText("");
        resta = 0;
        if(mode.equals("XYZ")){
            state("XYZ");
        }
        tv3.setText(String.format("%s", "run "));
    }

    public void record(View view){
        tv1.setText("");
        resta = 0;
        if(mode.equals("XYZ")){
            state("XYZ");
        }
        tv3.setText(String.format("%s", "Here "));
    }

    public void control(View view){
        tv1.setText("");
        resta = 0;
        String s3 = tv3.getText().toString();
        if(s3.equals("") || s3.equals("Con All <enter>")){
            tv3.setText(String.format("%s", "Coff All <enter>"));
        }else{
            tv3.setText(String.format("%s", "Con All <enter>"));
        }
    }

    public void abort(View view){
        tv1.setText(String.format("%s", "ALL PROGRAMS ABORTED "));
        resta = 0;
    }


    public void sendNumber(View view){

        Button b = (Button)view;
        String text = b.getText().toString();

        if(tv3.getText().toString().equals("")){
            // Si no se esta usando speed simplemente muestra el numero por pantalla
            if(!text.matches("X|Y|Z|P|R")){
                mode = getString(R.string.joints);
                if(text.equals("7")) group='B';
                else group ='A';
            }else{
                mode = getString(R.string.xyz);
                group ='A';
            }

            tv1.setText(String.format("Axis %s  Selected", text));
            tv4.setText(String.format("group:%s     ax:%s       %s", group, text, mode));

        }else if(tv3.getText().toString().matches("Speed.*")){

            // se añade el numero al final de string
            tv3.setText(String.format("%s%s", tv3.getText().toString(), text));
            resta++;

        }else if(tv3.getText().toString().matches("MOVE.*|moveL.*")){

            // se añade el numero al final de string
            tv3.setText(String.format("%s%s", tv3.getText().toString(), text));
            resta++;

        }else if(tv3.getText().toString().matches("run.*")){

            // se añade el numero al final de string
            tv3.setText(String.format("%s%s", tv3.getText().toString(), text));
            resta++;

        }else if(tv3.getText().toString().matches("Here.*")){

            // se añade el numero al final de string
            tv3.setText(String.format("%s%s", tv3.getText().toString(), text));
            resta++;

        }
    }

    public void enter(View view){
        String s3 = tv3.getText().toString();

        if(s3.matches("Speed.*")){

            String speed = String.format("%s", s3.substring(s3.length()-resta, s3.length()));
            resta = 0;

            if(!speed.equals("")) {
                int num = Integer.parseInt(speed);

                if (num > 100 || num < 1) {
                    // marcar error
                    tv1.setText(String.format("%s", "INVALID DATA"));
                } else {
                    tv1.setText("");
                    if(s3.matches("SpeedL.*")){
                        speedL = num;
                    }else if (group == 'A') {
                        speedA = num;
                    } else if (group == 'B') {
                        speedB = num;
                    }
                }

                if(s3.matches("SpeedL.*")){
                    tv2.setText(String.format("Speed%s %s%% ", "L", num));
                }else {
                    tv2.setText(String.format("Speed%s %s%% ", group, num));
                }
            }

        }else if(s3.matches("MOVE.*")){

            String movement = String.format("%s", s3.substring(s3.length()-resta, s3.length()));
            resta = 0;

            if(!movement.equals("")) {
                int num = Integer.parseInt(movement);

                if (num > 10 || num < 0) {
                    // marcar error
                    tv1.setText(String.format("%s", "POS NOT FOUND"));
                } else {
                    tv1.setText(String.format("%s", "MOVEMENT ABORTED"));
                    if(s3.matches("moveL.*")){
                        moveL = num;
                    }else if (group == 'A') {
                        move = num;
                    }
                }

                if(s3.matches("moveL.*")){
                    tv2.setText(String.format("moveL %s", num));
                }else {
                    tv2.setText(String.format("MOVE %s", num));
                }
            }

        }else if(s3.matches("run.*")){

            String run = String.format("%s", s3.substring(s3.length()-resta, s3.length()));
            resta = 0;

            if(!run.equals("")) {
                int num = Integer.parseInt(run);

                if (num==0){
                    tv1.setText(String.format("%s", "Homing..."));
                }else if (num > 10 || num < 0) {
                    // marcar error
                    tv1.setText(String.format("%s", "PRG NOT FOUND"));
                }else {
                    tv1.setText(String.format("%s", "Homing complete"));
                }

                tv2.setText(String.format("Run %s", num));
            }

        }else if(s3.matches("Here.*")){

            String record = String.format("%s", s3.substring(s3.length()-resta, s3.length()));
            resta = 0;

            if(!record.equals("")) {
                int num = Integer.parseInt(record);
                tv1.setText(String.format("%s", "DONE"));
                tv2.setText(String.format("Here %s", num));
            }

        }else if(s3.equals("Coff All <enter>")){
            resta = 0;
            disabled = true;
            tv1.setText(String.format("%s", "CONTROL DISABLED"));
            tv2.setText(String.format("%s", s3));

        }else if(s3.equals("Con All <enter>")){
            resta = 0;
            disabled = false;
            tv1.setText(String.format("%s", "CONTROL ENABLED"));
            tv2.setText(String.format("%s", s3));

        }else {
            tv1.setText("");
        }
        String GetData = String.format("%s",tv3.getText().toString());
        MyConexionBT.write(GetData);
        tv3.setText("");

    }

    public void clr(View view){
        tv3.setText("");
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
        }
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        address = intent.getStringExtra(Dispositivos.EXTRA_DEVICE_ADDRESS);
        //Setea la direccion MAC
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Establece la conexión con el socket Bluetooth.
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                btSocket.connect();
                //Toast.makeText(getBaseContext(), "CONEXION EXITOSA", Toast.LENGTH_SHORT).show();

                //return;
            }
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
            }
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();
    }




    @Override
    public void onPause() {
        super.onPause();
        try { // Cuando se sale de la aplicación esta parte permite que no se deje abierto el socket
            btSocket.close();
        } catch (IOException e2) {
        }
    }

    //Comprueba que el dispositivo Bluetooth
    //está disponible y solicita que se active si está desactivado
    private void VerificarEstadoBT() {

        if (btAdapter == null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    startActivityForResult(enableBtIntent, 1);
                    //return;
                }

            }
        }
    }

    //Crea la clase que permite crear el evento de conexion
    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            byte[] buffer = new byte[1024];
            int bytes;
            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String recieved = new String(buffer, 0, bytes);
                    bluetoothIn.obtainMessage(handlerState, recieved).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        //Envio de trama
        public void write(String input)
        {
            try {
                mmOutStream.write(input.getBytes());
            }
            catch (IOException e)
            {
                //si no es posible enviar datos se cierra la conexión
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}