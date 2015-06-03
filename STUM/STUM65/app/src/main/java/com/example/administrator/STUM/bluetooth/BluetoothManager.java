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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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

    ParseUser user = ParseUser.getCurrentUser();
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
    private void sendMyMessage() {  //시간 보내기
        Log.d("플래그보내주기","C,T,시간");
        // 현재 시간을 msec으로 구한다.
        long now = System.currentTimeMillis();

        // 현재 시간을 저장 한다.
        Date date = new Date(now);

        SimpleDateFormat sdfnow = new SimpleDateFormat("yyyyMMddHHmmss");
        String strM = "C"+"T"+sdfnow.format(date)+ Character.toString((char)0x0d)+ Character.toString((char)0x0a); //뒤에는 아두이노에 보내주는 뭐시기어쩌구 이걸 해보자

        byte[] bStrByteM = strM.getBytes();

        write(bStrByteM);

    }



    synchronized private void CalcDrunkWater(){

        Log.d("함수확인","CalcDrunkWater들어왔음");
        ///////////////////////////////////////MHS임시 테스트
/*     ParseObject testDrunk = new ParseObject("testDrunk");
        //유저추가
        testDrunk.put("year", 20);
        testDrunk.put("month", 5);
        testDrunk.put("day", 26);
        testDrunk.put("hour", 23);
        testDrunk.put("min", 20);
        testDrunk.put("drunk", 350);
        testDrunk.put("watertemp", 40.5);
        testDrunk.saveInBackground();
        Log.d("마신양파스테스트","어디보자..");
*/
        ///////////////////////////////////////MHE임시 테스트

        //ParseUser user = ParseUser.getCurrentUser(); //현재사용자 구하기
        //사용자에대해서 먼저 구하고
        //그다음 업데이트 시간으로 정렬하고
        // 차례대로 훑으면서 D인거 찾고 그 위아래 N으로 마신양 저장하자.




        ParseQuery<ParseObject> query = ParseQuery.getQuery("dataTestMH"); //클래스선택
        query.whereNotEqualTo("year", 0); //year이 0인건 아예 값을 못받아드렸을 경우이기때문에 제외한다.
        //query.whereEqualTo("User", user); //현재사용자의 데이터만 고르기
        query.addDescendingOrder("createdAt"); //만들어진순서로정렬후
        // query.whereEqualTo("month", 5);
        query.setLimit(700); //갯수를 제한하고

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> drinkList, ParseException e) {
                Log.d("마신거 등록하기", "done들어와짐");
                //Log.d("쿼리사이즈", String.valueOf(drinkList.size()));
                //if(drinkList.size()<3) return;//아예 작업후 개체를 삭제했기 때문에 너무 적은 숫자라면 그냥 작업안하는게 나을거같아

                int drunk_year = 0, year1 = 0, year2 = 0;
                int drunk_month = 0, month1 = 0, month2 = 0;
                int drunk_day = 0, day1 = 0, day2 = 0;
                int drunk_hour = 0, hour1 = 0, hour2 = 0;
                int drunk_min = 0, min1 = 0, min2 = 0;
                int drunk_volume = 0, drunk1 = 0, drunk2 = 0;
                float drunk_temp = 0, temp1 = 0, temp2 = 0;


                if (e == null) {
                    Log.d("done안임", "에러안났음");
                    for (int i = 0; i < drinkList.size(); i++) { //설정한 갯수만큼 반복문을 돌겟지
                        //Log.d("done안for문안임","for문 돌고있음");
                        ParseObject course = drinkList.get(i);
                        String flag = course.getString("drinkflag"); //먼저  플래그를 검사해가면서 D를 찾을거야

                        if (i == 0 && flag.equals("D")) { //쿼리의첫번째가 D면 이전 N을 찾을수 없으므로 그다음 D를 다 무시하고 그다음 N에 대하여 진행하도록한다...
                            while (drinkList.get(i + 1).getString("drinkflag").equals("D")) {
                                i++;
                                course = drinkList.get(i);
                                flag = course.getString("drinkflag"); //먼저  플래그를 검사해가면서 D를 찾을거야
                            }
                        }
                        if (flag.equals("D")) { //D를 찾으면
                            ParseObject ob1 = drinkList.get(i - 1);//이전 N에대한 값을 저장할거다
                            year1 = ob1.getInt("year");
                            month1 = ob1.getInt("month");
                            day1 = ob1.getInt("day");
                            hour1 = ob1.getInt("hour");
                            min1 = ob1.getInt("min");
                            temp1 = ob1.getInt("watertemp");
                            drunk1 = ob1.getInt("watervolume");
                            Log.d("D 이전 찾은 애들", year1 + "년 " + month1 + "월 " + day1 + "일 " + hour1 + "시 " + min1 + "분 " + temp1 + "도 " + drunk1 + "ml");

                            while (drinkList.get(i + 1).getString("drinkflag").equals("D"))
                                i++;//다음 플래그도 D면 무시해야하기때문에 다음 플래그가 D면 안저장하고 넘어가준다.

                            ParseObject ob2 = drinkList.get(i + 1);//D이후 바로나오는 N에대한 값을 저장할거다
                            year2 = ob2.getInt("year");
                            month2 = ob2.getInt("month");
                            day2 = ob2.getInt("day");
                            hour2 = ob2.getInt("hour");
                            min2 = ob2.getInt("min");
                            temp2 = ob2.getInt("watertemp");
                            drunk2 = ob2.getInt("watervolume");
                            Log.d("D이후 찾은 애들", year2 + "년 " + month2 + "월 " + day2 + "일 " + hour2 + "시 " + min2 + "분 " + temp2 + "도 " + drunk2 + "ml");

                            drunk_year = year1;
                            drunk_month = month1;
                            drunk_day = day1;
                            drunk_hour = hour1;
                            drunk_min = min1;
                            drunk_temp = (temp1 + temp2) / 2;
                            drunk_volume = drunk2 - drunk1;//1이 최신 2가 이전

                            if(drunk_volume<0 || day1-day2>1 || day1-day2<-1 ) {
                                Log.d("저장못할값이있나","이건 그냥 무시해 ");
                            }else{

                                ParseObject testDrunk = new ParseObject("testDrunk3");
                                testDrunk.put("year", drunk_year);
                                testDrunk.put("month", drunk_month);
                                testDrunk.put("day", drunk_day);
                                testDrunk.put("hour", drunk_hour);
                                testDrunk.put("min", drunk_min);
                                testDrunk.put("drunk", drunk_volume);
                                testDrunk.put("watertemp", drunk_temp);
                                testDrunk.saveInBackground();
                                Log.d("조건건 파스테스트", "어디보자..");
                            }

                        } else {
                            Log.d("포문에서", "플래그가 D가 아님");
                        }


                    }//정해준 갯수만큼의 for문 끝
                    drinkList.clear(); //이건 내가 행을 지우려고햇으나 안되는걸 보니..다하고 그냥 클래스 드랍해버리는게 좋을듯.
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }


            }

        }); //쿼리문 done함수 끝

        //클래스삭제..


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
    }   // End of class AcceptThread


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
    }   // End of class ConnectThread
    //체크함

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        int j=0;

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




        //원래 있던 커넥티드스레드의 런이고 위에께 새로운 템플릿에서의 런
        public void run() { //기기와 연결됬을때 실행되는 커넥티드스레드의 메인같은 친구죠.
            /////////////////////////////////////////////MHS/////////////////////////////////////////////////////////////////
            ///////////////////////////////////////연결되었답니다. 뿌잉 ///////////////////////////////여기서 보내주는거 작업할겁니다.


            sendMyMessage(); //플래그들과 시간보내주기 //연결되자마자 보낸거임
            // CalcDrunkWater();//연결되자마자 드렁크클래스에 추가도 해준다.

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


                // 너무 많이 돌아 그냥 하나만 돌게 위에다 둘게
                if(j==100){
                    //if(skipNumber<7){
                    sendMyMessage();// 일정한 간격으로 시간을 보내주고싶어.
                    Log.d("Calc 제한위한 j 확인 ", String.valueOf(j));
                    CalcDrunkWater(); //이렇게만 하니까 계속 run이 돌아서 너무 많이 들어가...일단 구현 해보겠다..
                    //ParseObject myobj = new ParseObject("testDrunk");
                    //myobj.deleteInBackground();
                    //insertTable(); //그냥 디비 노가다 넣기



                    j=0;

                }
                j++;


            }
            //////////////////////////////////////////////////////////////////////////////////////////////MHE


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

    }   // End of class ConnectedThread

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
                }   // End of run()
            });
        }
    }


}