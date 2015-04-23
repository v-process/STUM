package com.example.administrator.slidertest;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class BluetoothActivity extends ActionBarActivity {
    // Debugging
    private static final String TAG = "Main";
    private static final boolean D = true;
    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    // Intent request code
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    // Layout Views
    private ListView mConversationView;
    private TextView mStatus_view;
    private EditText mOutEditText;
    private Button mSendButton;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the BluetoothService
    private BluetoothService mBtService = null; //얘는 따로 생성해준 class의 객체임 중요하다는 얘기임
    // Name of the connected device
    private String mConnectedDeviceName = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        // Set up the window layout
        setContentView(R.layout.bluetooth_main);


        // Enable Local Datastore.
        // Parse.enableLocalDatastore(this);

        //Parse.initialize(this, "ZfjR3Gbh9Ly5JJJTop2oHMr3gSg2C9tSD0NNSs8O", "bohAfTs7aO1PXYOcpc1ucvIi30Hhu1B0SNBzky8Y");

        //파스먼저 초기화해주고 꼭해야해
        // ParseObject testObject = new ParseObject("MH");
        // testObject.put("ha", "ho");
        //testObject.saveInBackground();




        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) { //블루투스아답터가없으면 못씀

            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show(); //못써요 알려줌
            finish(); //끝냄
            return;
        }
    }

    @Override
    public void onStart() { //시작
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");



        if (!mBluetoothAdapter.isEnabled()) { //블루투스가 비활성화이면
            if(D) Log.d(TAG, "Bluetooth ON Request");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); //활성화창을 요청함
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT); //이게 활성화창부르는거
            // Otherwise, setup the chat session
        } else { //활성화이면
            if (mBtService == null) setupService(); //활성화이고 서비스가 비어있으면 setupService함수를 부른다. 아래있음
        }
    }
    @Override
    public void onRestart() { //멈췄다가 재시작할때
        super.onRestart();
        if(D) Log.e(TAG, "++ ON RESTART ++");
    }
    @Override
    public synchronized void onResume() { //멈췄을때..?
        super.onResume();
        if(D) Log.e(TAG, "++ ON RESUME ++");
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mBtService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBtService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                mBtService.start();
            }
        }
    }
    @Override
    public synchronized void onPause() {  //잠깐 멈췄을 때
        super.onPause();
        if(D) Log.e(TAG, "++ ON PAUSE ++");
    }
    @Override
    public void onStop() { //아예 멈출때
        super.onStop();
        if(D) Log.e(TAG, "++ ON STOP ++");
    }
    @Override
    public void onDestroy() { //앱파괴
        super.onDestroy();
        if(D) Log.e(TAG, "++ ON DESTROY ++");
        if (mBtService != null) mBtService.stop(); //블루투스 서비스가 널이아니면 비워줘야함
    }

    private void setupService() {	 //onStart()에서 블루투스 활성화인데 서비스가 null일때 호출됨 //기본 변수(위젯)들 세팅함
        if(D) Log.d(TAG, "setupChat()");
        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mConversationView = (ListView) findViewById(R.id.message_view);
        mConversationView.setAdapter(mConversationArrayAdapter);
        mConversationView.setDivider(null);

        mStatus_view = (TextView) findViewById(R.id.status_view);
        mStatus_view.setText("Start Now!");

        // Initialize the compose field with a listener for the return key
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() { //send버튼 눌렀을때 일어나는 일들
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message = view.getText().toString() + Character.toString((char)0x0d)+ Character.toString((char)0x0a); //edit_text_out에있는 글들을 스트링으로 바꿔서 message에 저장
                sendMessage(message); //string형인 message를 함수로 또 보내죠 아래구현되어있음
            }
        });

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");

        // Initialize the BluetoothService to perform bluetooth connections
        if(mBtService == null) {
            mBtService = new BluetoothService(this, mHandler);//핸들러에 연결해주어야지요
        }
    }




    //========================== Device .==========================
    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
            new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) { //edit_text창에 수정일어날때 얘가 알아서 일어나는거같음
                    // If the action is a key-up event on the return key, send the message
                    if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                        String message = view.getText().toString();
                        sendMessage(message);
                    }
                    if(D) Log.i(TAG, "END onEditorAction");
                    return true;
                }
            };


    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mBtService.getState() != BluetoothService.STATE_CONNECTED) { //블루투스가 연결되지 않았을때
            Toast.makeText(getApplicationContext(), R.string.not_connected, Toast.LENGTH_SHORT).show(); //연결되지 않았음을 보여준다
            return; //그리고 그냥 돌아간다
        }
        // Check that there's actually something to send
        if (message.length() > 0) { //에딧텍스트창에 있는 메세지가 0보다 크면
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes(); //메세지(스트링형)의 바이트를 얻어서 byte형배열에 차곡차곡 넣어준다. //아마 그럴거다 몰라
            mBtService.write(send); //그 바이트들을 블투서비스가 쓰도록 한다.

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0); //그리고 다시 길이는 0으로
            mOutEditText.setText(mOutStringBuffer); //에딧창을 깔끔하게 처리해준다
        }
    }


    //========================== Options Menu ==========================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
            case R.id.secure_connect_scan:
                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            case R.id.insecure_connect_scan:
                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            case R.id.discoverable:
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
        }
        return false;
    }
    /*

    */
    private void ensureDiscoverable() {  //남에게 나를 보여주는 작업인듯
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    //////////////////////////////////////여기부터 자세히 봅시다 /////////////////////////////////////////

    //========================== 'BluetoothService'  Message ==========================
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE: //상태변환
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);

                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED: //연결됬을때
                            setStatus(getString(R.string.status_connected_to, mConnectedDeviceName));
                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothService.STATE_CONNECTING: //연결중일때
                            setStatus(getString(R.string.status_connecting));
                            break;
                        case BluetoothService.STATE_LISTEN: //듣는중일때
                        case BluetoothService.STATE_NONE: //안연결됬을때
                            setStatus(getString(R.string.status_not_connected));
                            break;
                    }
                    break;

                case MESSAGE_WRITE: //메세지 쓸때
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Tx:" + writeMessage);
                    break;

                case MESSAGE_READ: //메세지 읽을때 !!!!!!!!!!!!!!이게 중요함 ///우리는 들어오는애를 잘읽어서 잘 저장해야함
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                    mConversationArrayAdapter.add("Rx:" + readMessage);
                    break;

                case MESSAGE_DEVICE_NAME: //장치이름
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;


                case MESSAGE_TOAST: //토스트~
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),Toast.LENGTH_LONG).show();
                    break;

            }
        }
    };


    private final void setStatus(String status) {
        mStatus_view.setText(status);
    } //상태변경해주기


    //========================== Intent Request Return ==========================
    public void onActivityResult(int requestCode, int resultCode, Intent data) { //인텐트결과로 오는애임 중요함
        if(D) Log.d(TAG, "onActivityResult " + requestCode + "," + resultCode);

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupService();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    if(D) Log.e(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mBtService.connect(device, secure);
    }
}
