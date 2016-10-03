package br.com.bonysoft.redesocial_iesb.utilitarios;

import android.bluetooth.*;
import android.util.Log;

import java.io.*;
import java.util.UUID;

/**
 * Created by carlospanarello on 02/10/16.
 */

public class ComunicadorBluethooth {
    private static final String BLUETOOTH_NAME = "BluetoothCartao";
    private static final UUID BLUETOOTH_UUID = UUID.fromString("0001101-0000-1000-8000-00805F9B34FB");

    private BluetoothServerThread serverThread = null;
    private BluetoothClientThread clientThread = null;
    private BluetoothSocket communicationSocket = null;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private boolean running = false;
    private InputStream input = null;
    private OutputStream output = null;

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
        clientThread = new BluetoothClientThread(address);
        clientThread.setName("CLIENT THREAD");
        clientThread.start();
    }

    public void send(String text) {
        clientThread.send((text + (char)3).getBytes());
    }

    public void connected(BluetoothSocket socket) {
        if (serverThread != null) {
            serverThread.cancel();
        }

        Log.d("BLUETOOHT", "GET INPUT AND OUTPUT STREAM....");
        communicationSocket = socket;
        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("BLUETOOHT", "CONNECTED!!!");

        state = STATE_CONNECTED;
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
            setName("CLIENT THREAD");
            try {
                BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(clientDevAddress);
                clientSocket = bluetoothDevice.createRfcommSocketToServiceRecord(BLUETOOTH_UUID);
                bluetoothAdapter.cancelDiscovery();

                if (clientSocket != null) {
                    clientSocket.connect();
                }

                connected(clientSocket);

                byte[] buffer = new byte[1024];
                int bytes;
                while (running) {
                    try {
                        bytes = input.read(buffer);
                        Log.d("BLUETOOTH", "message string bytes " + String.valueOf(bytes));
                        Log.d("BLUETOOTH", "message buffer " + new String(buffer));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                ComunicadorBluethooth.this.start();
            }
        }

        public void send(byte[] data) {
            if (output != null) {
                try {
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
