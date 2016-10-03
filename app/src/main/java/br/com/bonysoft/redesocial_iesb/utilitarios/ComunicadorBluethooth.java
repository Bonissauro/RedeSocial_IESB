package br.com.bonysoft.redesocial_iesb.utilitarios;

import android.bluetooth.*;
import android.util.Log;

import java.io.*;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Created by carlospanarello on 02/10/16.
 */

public class ComunicadorBluethooth {
    private static final String BLUETOOTH_NAME = "BluetoothCartao";

    private static final UUID BLUETOOTH_UUID = UUID.fromString("0001101-0000-1000-8000-00805F9B34FB");

    private final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    private BluetoothServerThread serverThread = null;
    private BluetoothClientThread clientThread = null;
    private BluetoothSocket communicationSocket = null;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private boolean running = false;
    private InputStream input = null;
    private OutputStream output = null;

    private static final int size = 1024;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    private int state;

    private static ComunicadorBluethooth instance;

    public static ComunicadorBluethooth getInstance() {
        if (instance == null) {
            instance = new ComunicadorBluethooth();
        }
        return instance;
    }

    public void start() {
        running = true;

        if (clientThread != null) {
            clientThread.cancel();
            clientThread = null;
        }

        state = STATE_LISTEN;

        if (serverThread == null) {
            serverThread = new BluetoothServerThread();
            serverThread.setName("SERVER THREAD");
            serverThread.start();
        }
    }

    public void disconnect(){
        if(clientThread != null){
            clientThread.cancel();
        }

        if(serverThread != null){
            serverThread.cancel();
        }

        clientThread = null;
        serverThread = null;
    }

    public void connect(String address) {
        Log.i("BLUETOOTH1","Criano uma nova Thread de clientThread" );
        clientThread = new BluetoothClientThread(address);
        clientThread.setName("CLIENT THREAD");
        clientThread.start();
    }

    public void send(String text) {
        Log.i("BLUETOOTH1","SEND Bruto--> " + text );
        Log.i("BLUETOOTH1","SEND UTF8--> " + encodeUTF8(text) );
        Log.i("BLUETOOTH1","SEND UTF8--> " + decodeUTF8(encodeUTF8(text)) );
        //clientThread.send(encodeUTF8(text));
        //Log.i("BLUETOOTH1","SEND CHAR--> " + (text + (char)3).getBytes() );
        if(clientThread != null) {
            clientThread.send(encodeUTF8(text));
        } else {
            Log.i("BLUETOOTH1","Cliente NULL--> ");

        }
    }

    public void connected(BluetoothSocket socket) {
        if (serverThread != null) {
            serverThread.cancel();
        }

        Log.d("BLUETOOTH1", "GET INPUT AND OUTPUT STREAM....");
        communicationSocket = socket;
        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("BLUETOOTH1", "CONNECTED!!!");

        state = STATE_CONNECTED;
    }



    private String decodeUTF8(byte[] bytes) {
        return new String(bytes, UTF8_CHARSET);
    }

    private byte[] encodeUTF8(String string) {
        return string.getBytes(UTF8_CHARSET);
    }

    /**
     * Server Thread
     */
    private class BluetoothServerThread extends Thread {
        private BluetoothServerSocket serverSocket = null;
        BluetoothSocket socket = null;

        public BluetoothServerThread() {
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(BLUETOOTH_NAME, BLUETOOTH_UUID);
            } catch (IOException e) {
                Log.d("BLUETOOTH1", "BluetoothServerThread ERRRO-->"+e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            setName("SERVER THREAD");
        }

        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Client Thread
     */
    private class BluetoothClientThread extends Thread {
        BluetoothSocket clientSocket = null;
        String clientDevAddress = null;

        public BluetoothClientThread(String btDevAddress) {
            this.clientDevAddress = btDevAddress;
        }

        public void run() {
            Log.d("BLUETOOTH1", "BluetoothClientThread Rodando");
            setName("CLIENT THREAD");
            try {
                BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(clientDevAddress);
                Log.i("BLUETOOTH1"," bluetoothDevice "+bluetoothDevice);

                if(bluetoothDevice!=null){
                    Log.i("BLUETOOTH1"," bluetoothDevice Name->"+bluetoothDevice.getName());
                    Log.i("BLUETOOTH1"," bluetoothDevice Add->"+bluetoothDevice.getAddress());
                }

                clientSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(BLUETOOTH_UUID);
                bluetoothAdapter.cancelDiscovery();

                if (clientSocket != null) {
                    clientSocket.connect();
                }else{
                    Log.i("BLUETOOTH1"," clientSocket NULL ");
                }

                connected(clientSocket);

                byte[] buffer = new byte[size];
                int bytes;
                while (running) {
                    try {
                        bytes = input.read(buffer);

                        Log.d("BLUETOOTH1", "message string bytes " + String.valueOf(bytes));
                        Log.d("BLUETOOTH1", "message buffer " + new String(buffer));
                        String s = decodeUTF8(buffer);
                        Log.d("BLUETOOTH1", "Decodificado " + s);
                        if(s.contains( Constantes.FIM_TRANSMISSAO)){
                            Log.d("BLUETOOTH1", "POSSUI FIM ");
                            running = false;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                Log.d("BLUETOOTH1", "BluetoothClientThread ERRO-->" + e.getMessage());
                e.printStackTrace();
                ComunicadorBluethooth.this.start();
            }
        }

        public void send(byte[] data) {
            if (output != null) {
                try {
                    Log.i("BLUETOOTH1","Data-->" + data);
                    Log.i("BLUETOOTH1","String-->" + decodeUTF8(data));
                    output.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void cancel() {
            try {
                running = false;
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            running = false;
        }
    }
}
