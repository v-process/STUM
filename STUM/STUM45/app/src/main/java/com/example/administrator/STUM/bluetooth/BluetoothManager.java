package com.example.administrator.STUM.bluetooth;

/**
 * Created by Administrator on 2015-05-22.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothManager {

    // Debugging
    private static final String TAG = "BluetoothManager";

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    // Message types sent from the BluetoothManager to Handler  //이건 추가되었음..
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // 이것도 추가되었음
    public static final String SERVICE_HANDLER_MSG_KEY_DEVICE_NAME = "device_name";
    public static final String SERVICE_HANDLER_MSG_KEY_DEVICE_ADDRESS = "device_address";
    public static final String SERVICE_HANDLER_MSG_KEY_TOAST = "toast";



    // Name for the SDP record when creating server socket
    private static final String NAME = "BluetoothManager"; //추가됨

    // Unique UUID for this application
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    private static final long RECONNECT_DELAY_MAX = 60*60*1000;  //추가됨

    private long mReconnectDelay = 15*1000;  //추가됨
    private Timer mConnectTimer = null;  //추가됨
    private boolean mIsServiceStopped = false;  //추가됨 //자동연결때 자동연결 서비스를 계속 돌릴건지 판단할때쓰이는거같음




    ////////////////////////////////////////////////////////////MHS
    // working
    byte[] RcvBuffer = new byte[1024];
    int pRcvBuffer;
    ////////////////////////////////////////////////////////////MHE



    /**
     * Constructor. Prepares a new BluetoothChat session.
     * @param context  The UI Activity Context
     * @param handler  A Handler to send messages back to the UI Activity
     */
    public BluetoothManager(Context context, Handler handler) { //비티서비스가 context고 비티서비스핸들러가 핸들러다
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE; //가진코드에서는 함수를 썻지만 여기는 그냥이군..
        //setState(STATE_NONE); //가진코드처럼해보겠다.//이거쓰면 안드로이드 죽어요 ㅋㅋㅋㅋㅋ생성자니까..함수는 ㄴㄴ해
        mHandler = handler;
    }

    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        if(mState == STATE_CONNECTED) {
            cancelRetryConnect();
        }

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
        //블루투스상태메세지와,블루투스상태와, -1 을 비티 서비스에 보내는거져 비티서비스핸들러에서 찾아봅시다.//근데이건 그냥 액티비티 유아이 변환을 위한 애들이었다...
    }

    /**
     * Return the current connection state. */
    public synchronized int getState() {
        return mState;
    }

    //체크끝

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume() */
    public synchronized void start() {
        Log.d(TAG, "Starting BluetoothManager...");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;} //다시시작하기전에 정리해주는거지

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;} //다시시작하기전에 이것도 정리

        // Start the thread to listen on a BluetoothServerSocket //추가됨 //얘는 받아들이는 스레드가 널이면 받아들이기시작해야하니까 스레드시작해주고
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(STATE_LISTEN);  //받아들이는 스레드 실행중이니까 상태 듣는중으로 바꾸고
        mIsServiceStopped = false;  //추가됨
    }

    //체크끝

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        Log.d(TAG, "Connecting to: " + device);

        if (mState == STATE_CONNECTED)  //추가됨
            return;

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }
    //체크끝

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) { //매개변수에 소켓타입이 빠짐//수정됨
        Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Cancel the accept thread because we only want to connect to one device //실제에서 주석처리된건데 여긴 안주석되있음 //수정됨
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);//수정됨//비티서비스핸들러에 메세지 추가한다.
        Bundle bundle = new Bundle();
        bundle.putString(SERVICE_HANDLER_MSG_KEY_DEVICE_ADDRESS, device.getAddress()); //추가됨
        bundle.putString(SERVICE_HANDLER_MSG_KEY_DEVICE_NAME, device.getName()); //수정됨
        msg.setData(bundle);
        mHandler.sendMessage(msg); //각각 이름에 해당하는 값들을 추가해서 비티서비스 핸들러에 메세지를 보내는거죠.

        setState(STATE_CONNECTED);

        pRcvBuffer = 0;//MH 내가 추가함

    }
    //체크끝
    /**
     * Stop all threads
     */
    public synchronized void stop() {
        Log.d(TAG, "stop");
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;} //추가됨
        setState(STATE_NONE);

        mIsServiceStopped = true; //추가됨
        cancelRetryConnect(); //추가됨 //다시 돌리는걸 멈추시오
    }
    //체크끝

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) { //이건 블루투스 메니저의 라이트고
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out); //여기서부르는건 커넥티드 스레드의 라이트임
    }
    //체크끝
    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        Log.d(TAG, "BluetoothManager :: connectionFailed()");
        setState(STATE_LISTEN); //추가됨

        // Send a failure message back to the Activity
        //그냥 끈겼다고 알려주는거니까 안해도문제는 없다.
       /* Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(SERVICE_HANDLER_MSG_KEY_TOAST, "장치와연결실패했어요");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        */

        // Reserve re-connect timer
        reserveRetryConnect(); //수정됨
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        Log.d(TAG, "BluetoothManager :: connectionLost()");
        setState(STATE_LISTEN); //추가됨

        // Send a failure message back to the Activity
        // WARNING: This makes too many toast.
        //이것도 어차피 연결잃었다고하는거 알려주는거라 안해도 상관 없다.
        /*
        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(SERVICE_HANDLER_MSG_KEY_TOAST, "연결되있던장치와 연결을 잃었어요");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        */
        // Reserve re-connect timer
        reserveRetryConnect(); //수정됨
    }

    private void reserveRetryConnect() {  //함수가추가됨 //다시시도 예약연결
        if(mIsServiceStopped) //서비스가 멈추라고하는게 true면 (연결되서 커넷티드스레드실행중일때 트루임)
            return; //그냥 함수실행하지마



        //여기왔다는건 아직 블투기기와 연결이 안됬다는거죠
        mReconnectDelay = mReconnectDelay * 2;
        if(mReconnectDelay > RECONNECT_DELAY_MAX)
            mReconnectDelay = RECONNECT_DELAY_MAX;

        if(mConnectTimer != null) {
            try {
                mConnectTimer.cancel();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        mConnectTimer = new Timer(); //타이머를 시작하고
        mConnectTimer.schedule(new ConnectTimerTask(), mReconnectDelay); //딜레이 타임...스케줄관리...?
       /* schedule(TimerTask task , long delay)
        "TimerTask를 delay 시간 만큼 후에 실행 시켜라." 라는 메서드 입니다.
                만약 delay를 3초를 지정해 주면,  " 3초 후에 Task를 한번만 실행 해라. "  라는 메서드가 됩니다.

                schedule(TimerTask task , long delay , long period)
        그리고, 매개인자가 3개인  schedule 함수가 있는데요, 이 함수의 기능은,

        "TimerTask를 delay 시간 만큼 후에 실행 시키고,
        period만큼 후에 계속 반복 해라." 라는 메서드 입니다.
        만약 delay와 3초와 period 5초를 지정해 주면, " 3초 후에 Task를 실행하고 5초마다 반복 해라. " 라는 메서드가 되는 것 입니다.
   */
    }

    private void cancelRetryConnect() { //함수가추가됨

        if(mConnectTimer != null) {
            try {
                mConnectTimer.cancel();
                mConnectTimer.purge();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mConnectTimer = null;
            //mReconnectDelay = 0;//이라고되잇는데 수정된 템플릿에서는
            mReconnectDelay = 15*1000; //이거로 되잇음.
        }
    }



    ///////////////////////////////////////////////////////////////////////////////MHS
    private void sendMyTimeMessage() {  //시간 보내기
        // 현재 시간을 msec으로 구한다.
        long now = System.currentTimeMillis();

        // 현재 시간을 저장 한다.
        Date date = new Date(now);

        SimpleDateFormat sdfnow = new SimpleDateFormat("yyyyMMddHHmmss");
        String strnow = "T"+sdfnow.format(date)+ Character.toString((char)0x0d)+ Character.toString((char)0x0a); //뒤에는 아두이노에 보내주는 뭐시기어쩌구 이걸 해보자
        byte[] bStrByte = strnow.getBytes();


        write(bStrByte);
    }

    private void sendConnectMessage() {  //연결됬다는 신호 보내주기
        String strnow = "C"+Character.toString((char)0x0d)+ Character.toString((char)0x0a); //뒤에는 아두이노에 보내주는 뭐시기어쩌구 이걸 해보자
        byte[] bStrByte = strnow.getBytes();

        write(bStrByte);
    }
    ///////////////////////////////////////////////////////////////////////////////////MHE




    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread { //클래스가추가됨
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "listen() failed" + e.toString());
            }
            mmServerSocket = tmp;
        }

        public void run() { //억셉 스래드의 런
            Log.d(TAG, "BEGIN mAcceptThread" + this);
            setName("AcceptThread");
            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    if(mmServerSocket != null)
                        socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothManager.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice()); //getRemoteDevice란 맥주소를 이용해 디바이스객체를 만들어주는애라고한다
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close(); //이미연결되있으니까 그냥 있는거써야지..
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "END mAcceptThread");
        }

        public void cancel() {
            Log.d(TAG, "cancel " + this);
            try {
                if(mmServerSocket != null)
                    mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed" + e.toString());
            }
        }
    }	// End of class AcceptThread


    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        //소켓타입이없음

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() { //커넥트스레드의 런
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                connectionFailed();
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                // Start the service over to restart listening mode
                BluetoothManager.this.start(); //추가됨 //커넷션에 실패하면 다시 블투매니저 를 스타트해서 다시시도하게끔하는거죠
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothManager.this) {   //연결하고는 스레드 멈춰야하니까...
                mConnectThread = null;
            }
            // Start the connected thread
            connected(mmSocket, mmDevice); //이제 연결된상태의 스레드를 실행해줘야겠죵
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }	// End of class ConnectThread
    //체크함

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) { //소켓에는 맥주소를통해 생성된 디바이스와의 소켓이 열린것이다.
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream(); //들어오는애를 받고
                tmpOut = socket.getOutputStream(); //나가는애를 주고
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        /* 이거했더니 에러가 나서..늅
        public void run() { //새로 따온 커넥티드스레드의 런
            Log.i(TAG, "BEGIN mConnectedThread");
            int bytes;

            // Keep listening to the InputStream while connected //이건 읽어오는거 는 커넥션을 잃지않는이상 계속 쭉쭉ㅉ구
            while (true) {
                try {
                    // Read from the InputStream
                    byte[] buffer = new byte[1024];
                    Arrays.fill(buffer, (byte) 0x00);
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the main thread
                    mHandler.obtainMessage(MESSAGE_READ, bytes-2, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }
    */


        //원래 있던 커넥티드스레드의 런이고 위에께 새로운 템플릿에서의 런
        public void run() { //기기와 연결됬을때 실행되는 커넥티드스레드의 메인같은 친구죠.
            /////////////////////////////////////////////MHS/////////////////////////////////////////////////////////////////
            ///////////////////////////////////////연결되었답니다. 뿌잉 ///////////////////////////////여기서 보내주는거 작업할겁니다.

            sendMyTimeMessage(); //시간보내주기
            sendConnectMessage(); //연결됬다는거 알려주기 //안되는거같음

            /////////////////////////////////////////////MHE/////////////////////////////////////////////////////////////////

            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024]; //버퍼생성후
            int bytes;

            byte chr; //MH
            int i; //MH

            //////////////////////////////////////////////////////////////////////////////////////////////MHS //아무래도 이거 써야될거같긴해..//체크
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    i = 0;
                    while(i < bytes){
                        chr = buffer[i++];
                        RcvBuffer[pRcvBuffer++] = chr;
                        if(chr == 0x0a){
                            // Send the obtained bytes to the UI Activity
                            if(pRcvBuffer > 2)
                            {
                                mHandler.obtainMessage(MESSAGE_READ, pRcvBuffer - 2, -1, RcvBuffer).sendToTarget();
                                //비티서비스에 읽는중이라는 메시지를 보내고,버퍼를 보내고,..그니까 메세지안에 여러 요소가있는데 처음은
                                //msg.what   mag.arg1  msg.obj    //이거를 보내주는거죠

                            }
                            pRcvBuffer = 0;
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    // Start the service over to restart listening mode

                    break;
                }
            }
            //////////////////////////////////////////////////////////////////////////////////////////////MHE

          /* //이게 위에꺼 while 원본
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer); //들어온애를 읽어요 바이트형태로

                    // Send the obtained bytes to the main thread
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();//what  arg1 arg2  obj

                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();

                    break;
                }
            } *///여기까지
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */

        public void write(byte[] buffer) { //이건 보내주는거
            try {
                mmOutStream.write(buffer);

                // Disabled: Share the sent message back to the main thread
                mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer).sendToTarget(); //MH주석풀엇음 //what arg1, arg2  obj

            } catch (IOException e) {
                Log.e(TAG, "Exception during write");
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed");
            }
        }

    }	// End of class ConnectedThread

    /**
     * Auto connect timer
     */
    private class ConnectTimerTask extends TimerTask {
        public ConnectTimerTask() {}

        public void run() {


            if(mIsServiceStopped) //시간관련 서비스가 멈춰야할때를 알려주는애지...
                return;

            mHandler.post(new Runnable() {
                public void run() {

                    if(getState() == STATE_CONNECTED || getState() == STATE_CONNECTING)
                        return;

                    //////////////////////////////////////////////////////////MHS////////////////////////////////어차피 시간마다 자동연결스레드는 실행되니까..여기에 하니까 위에 if(mIsServiceStopped) 에서 먼저 끈겼다..
                   /* 실패
                   if(getState() == STATE_CONNECTING) return;
                    if(getState() == STATE_CONNECTED){ //연결됬을때는 그냥 아두이노에 보내줄것좀 보내주고 리턴할래..
                        sendMyTimeMessage();//시간보내주기
                        return;
                    }
                    */
                    //////////////////////////////////////////////////////////MHE////////////////////////////////

                    Log.d(TAG, "ConnectTimerTask :: Retry connect()");

                    ConnectionInfo cInfo = ConnectionInfo.getInstance(null);
                    if(cInfo != null) {
                        String addrs = cInfo.getDeviceAddress();
                        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
                        if(ba != null && addrs != null) {
                            BluetoothDevice device = ba.getRemoteDevice(addrs);

                            if(device != null) {
                                connect(device);
                            }
                        }
                    }
                }	// End of run()
            });
        }
    }


}
