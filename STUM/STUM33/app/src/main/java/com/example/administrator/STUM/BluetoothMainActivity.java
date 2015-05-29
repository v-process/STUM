package com.example.administrator.STUM;

/**
 * Created by Administrator on 2015-05-22.
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.administrator.STUM.service.BTService;

public class BluetoothMainActivity extends Activity {

    // Debugging
    private static final String TAG = "RetroWatchActivity";

    // Context, System
    private Context mContext;
    private BTService mService;
    private ActivityHandler mActivityHandler;

    // Global
    private boolean mStopService = true;	// If you want to stop background service when exit app, set this true.


    // UI stuff
    private ImageView mImageBT = null;
    private TextView mTextStatus = null;



    ////////////////////MHS/////////////////////////////////////////////////////////////
    private BluetoothAdapter mhbtAdapter;
    private boolean mBluetoothOn;// 블루 투스 켜져있으면 true 아니면 false
    ////////////////////MHE/////////////////////////////////////////////////////////////



    /*****************************************************
     *
     *	 Overrided methods
     *
     ******************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //----- System, Context
        mContext = this;//.getApplicationContext();
        mActivityHandler = new ActivityHandler();

        // Do data initialization after service started and binded
        doStartService(); //여기서 이전Main에서 했던 작업을 Service로 옮겨서 하는듯하다.

        // Setup views
        setContentView(R.layout.bluetooth_activity_main);

        // Enable Local Datastore.//MH
        //Parse.enableLocalDatastore(this);
        //Parse.initialize(this, "ZfjR3Gbh9Ly5JJJTop2oHMr3gSg2C9tSD0NNSs8O", "bohAfTs7aO1PXYOcpc1ucvIi30Hhu1B0SNBzky8Y"); //STUM
       // Parse.initialize(this, "0BdEpLyJrWh7qbRMK1G9ADRcsLflAX85cnZD2i1H", "AgEsxPRib5IPMUB1iId7zDSwKcxaxb6ynpzcBOx3"); //test

        mImageBT = (ImageView) findViewById(R.id.status_title);
        mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_invisible));
        mTextStatus = (TextView) findViewById(R.id.status_text);
        mTextStatus.setText(getResources().getString(R.string.bt_state_init));

/////////////////////////////////////////MHS  버튼 들과 그에 필요한 작업들...//////////////////////////////////////////////////////
        // BluetoothAdapter 얻기
        mhbtAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mhbtAdapter.isEnabled()) mBluetoothOn=true;
        else mBluetoothOn=false;

        //블루투스 켜기 끄기
        final ToggleButton mBtTb=(ToggleButton)this.findViewById(R.id.bt_onoff);
        mBtTb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBtTb.isChecked()) {
                    //블루투스 활성화 다이알로그
                    // (만약 블투가 켜져있는데 켜짐으로 설정하면) 아무일 없음, 켜져있는 플래그설정
                    //         (꺼져있었으면)블루투스 활성화 할건지 다이아로그 띄워서 물어보기(함수불러야되나..) //그리고 블루투스 플래그 설정도 함수서 해주자
                    if(mBluetoothOn==false){
                        initialize();//아래에 비티 초기화하는거있음. ㅋ
                    }
                }else{
                    mhbtAdapter.disable();//끄기
                    mBluetoothOn=false;
                }
            }
        });

        //기기 검색과 기기 연결
        final Button mConnectDeviceBt=(Button)this.findViewById(R.id.connect_device_button);
        mConnectDeviceBt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //아이템에있는거 가져오는게 좋을듯
                doScan();
            }
        });

        //내기기 보여주기 다이아로그
        final Button mShowMydeviceBt=(Button)this.findViewById(R.id.show_mydevice_button);
        mShowMydeviceBt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //아이템에있는거 가져오는게 좋을듯
                ensureDiscoverable();
            }
        });
        /////////////////////////////////////////MHE//////////////////////////////////////////////////////

        ////////////////////////////////////////MHS임시 테스트
         /*ParseObject testDBdata = new ParseObject("dataTestMH");
        testDBdata.put("drinkflag", "N");
        testDBdata.put("year", 2015);
        testDBdata.put("month", 5);
        testDBdata.put("day", 14);
        testDBdata.put("hour", 18);
        testDBdata.put("min", 2);
        testDBdata.put("sec", 3);
        testDBdata.put("watervolume", 350);
        testDBdata.put("watertemp", 25.5);
        testDBdata.saveInBackground();
        Log.d("파스테스트","제발파스테스트들어가져라");*/
        ///////////////////////////////////////MHE임시 테스트


    }

    @Override
    public synchronized void onStart() {
        super.onStart();
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
    }

    @Override //내가 추가했다.. 왜냐면 실행했다가 홈 눌러서 나갔다가 다시 들어오면 에러나고 죽고 그다음에 다시 눌러야 제대로 실행되기 때문..
    public synchronized void onResume(){
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finalizeActivity(); //추가됨
    }

    @Override
    public void onLowMemory (){ //추가됨
        super.onLowMemory();
        // onDestroy is not always called when applications are finished by Android system.
        finalizeActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan:
                // Launch the DeviceListActivity to see devices and do scan
                doScan(); //아래함수있음//장치검색
                return true;
            case R.id.action_discoverable:
                // Disabled: Ensure this device is discoverable by others
                ensureDiscoverable(); //아래함수있음 //내기기보이기
                return true;
        }
        return false;
    }



    @Override
    public void onBackPressed() { //추가됨 //뒤로가기 두르면 바로 정지되는거 방지하는거래요
        super.onBackPressed();		// TODO: Disable this line to run below code
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){ //추가됨 //가로세로방향바뀔때 하는거같음
        // This prevents reload after configuration changes
        super.onConfigurationChanged(newConfig);
    }




    /*****************************************************
     *
     *	Private methods
     *
     ******************************************************/

    /**
     * Service connection //추가됨
     */
    private ServiceConnection mServiceConn = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.d(TAG, "Activity - Service connected");

            mService = ((BTService.BTServiceBinder) binder).getService(); //만들어논 객체인걸 기억하시오... 거기에 설정을 해준다는걸 기억하시오...그니까 거기서 서비스만들어서 여기 엮어준거

            // Activity couldn't work with mService until connections are made
            // So initialize parameters and settings here, not while running onCreate()
            initialize(); //보면 액티비티의 핸들러(비티서비스에서 액티비티로 메세지를 보내서 움직이게하는거다.)랑 서비스를연결해준다.
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    private void doStartService() {
        Log.d(TAG, "# Activity - doStartService()");
        startService(new Intent(this, BTService.class));
        bindService(new Intent(this, BTService.class), mServiceConn, Context.BIND_AUTO_CREATE);
       /* bindService 는    서비스를 받는 쪽과  묶어서 (bind) 돌아가는 구조구요.
          startService는  서비스 받는 쪽과 상관없이 돌아가야 할 때 사용하면 됩니다.

          bindService는 unbind 할때,  서비스가 종료되니까   바인드 하는 active 가 없는  서비스가 돌지 않고,
          unbind를 해주지 않으면   바인드한 activity가 종료되지 않고 남아 있는 것으로 알고 있습니다.

          백을 누르면 바인딩이 풀리는건 당연한 거지요
          왜냐면 해당 액티비티에서 바인딩을 걸어논 상태기때문에 백을 해버리면
          액티비티가가 onDestroy 되면서 메모리에서 사라지니 액티비티가 가진 참조인 바인더도 같이 없어지죠
       */
        /*
        보통 이 bindService() 를 호출하는 녀석을 client 라 부르는데,
        이 client 와 service 는 IBinder interface 를 통해서 통신하게 된다.
        client 와 service 간의 통신은 unbindService() 로 끝낼 수 있다.
         이렇게 bindService 를 이용해서 여러 client 가 하나의 service 에 bind 해서 사용할 수 있다.
         그렇기 때문에 unbindService() 를 호출하는 것만으로는 system 이 service 를 destroy 하지 않으며,
         모든 bindService() 했던 client 가 unbind되어야만 비로서 service 를 destroy 하게 된다.
         */
    }

    private void doStopService() {
        Log.d(TAG, "# Activity - doStopService()");
        mService.finalizeService();
        stopService(new Intent(this, BTService.class));
    }

    /**
     * Initialization / Finalization
     */
    private void initialize() { //서비스 커넥션에서 불러줌
        Log.d(TAG, "# Activity - initialize()");
        mService.setupService(mActivityHandler);//거기그 서비스를 액티비티핸들러랑 연결해준다 ...
        // If BT is not on, request that it be enabled.
        // BTService.setupBT() will then be called during onActivityResult
        if(!mService.isBluetoothEnabled()) { //블루투스 비활성화면 블투 연결창...
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
        }
    }

    private void finalizeActivity() { //Ondestroy에서 호출함 //서비스끝내주는거..
        Log.d(TAG, "# Activity - finalizeActivity()");

        if(mStopService)
            doStopService();   //서비스 스탑하고
        /*
        startService() 로 시작된 service 는 혼자서 잘 돌다가
        stopSelf() 를 호출해서 스스로 멈춰야만 한다.
        다른 녀석이 stopService() 를 이용해서 이 service 를 멈출 수도 있다.
        여하튼 이렇게 멈추게 된 service 는 system 이 destroy 하게 된다.*/

        unbindService(mServiceConn); //서비스 연결까지 끊어지고...

        RecycleUtils.recursiveRecycle(getWindow().getDecorView()); //현재화면을 캡쳐해서 매개변수로 보내줌 // 각각 뷰들을 다 정리해주는 객체였음..
        System.gc(); //가비지콜렉션..//널로 정리하고 해야 효과있음.
    }

    /**
     * Launch the DeviceListActivity to see devices and do scan
     */
    private void doScan() { //장치검색하기//함수로분리된것뿐
        Intent intent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CONNECT_DEVICE);//정보를 같이보내는거지요..
    }

    /**
     * Ensure this device is discoverable by others
     */
    private void ensureDiscoverable() { //나보여주기
        if (mService.getBluetoothScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(intent);
        }
    }

    /**
     * Call this method to send messages to remote
     */
	/*BTService에잇어서 MH이 주석처리햇음
	private void sendMessageToRemote(String message) {
		mService.sendMessageToRemote(message);
	}
	*/

    /*****************************************************
     *
     *	Public classes
     *
     ******************************************************/

    /**
     * Receives result from external activity
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);

        switch(requestCode) {
            case Constants.REQUEST_CONNECT_DEVICE:  //doScan에서 DeviceListActivity 로 인텐트를 불럿다
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {  //블루투스활성화버튼 OK눌리면!
                    mBluetoothOn=true;//내가추가 그냥 토글 버튼 때매..
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Attempt to connect to the device
                    if(address != null && mService != null)
                        mService.connectDevice(address);
                }else  mBluetoothOn=false;
                break;

            case Constants.REQUEST_ENABLE_BT: //initialize에서 부른 블루투스 활성화창에서 보내는애지요
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a BT session
                    mService.setupBT(); //객체이용...비티서비스로가고 거기서 블투매니저 또 이용하고 쭉쭉
                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.e(TAG, "BT is not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                }
                break;
        }	// End of switch(requestCode)
    }



    /*****************************************************
     *
     *	Handler, Callback, Sub-classes
     *
     ******************************************************/
    //추가됨
    public class ActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what) {
                // BT state message
                case Constants.MESSAGE_BT_STATE_INITIALIZED:
                    mTextStatus.setText(getResources().getString(R.string.bt_title) + ": " +
                            getResources().getString(R.string.bt_state_init));
                    mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_invisible));
                    break;
                case Constants.MESSAGE_BT_STATE_LISTENING:
                    mTextStatus.setText(getResources().getString(R.string.bt_title) + ": " +
                            getResources().getString(R.string.bt_state_wait));
                    mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_invisible));
                    break;
                case Constants.MESSAGE_BT_STATE_CONNECTING:
                    mTextStatus.setText(getResources().getString(R.string.bt_title) + ": " +
                            getResources().getString(R.string.bt_state_connect));
                    mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_away));
                    break;
                case Constants.MESSAGE_BT_STATE_CONNECTED: //연결된 상태인데
                    if(mService != null) { //서비스가 없으면
                        String deviceName = mService.getDeviceName(); //서비스에서 디바이스 이름 얻어와서
                        if(deviceName != null) {
                            mTextStatus.setText(getResources().getString(R.string.bt_title) + ": " +
                                    getResources().getString(R.string.bt_state_connected) + " " + deviceName); //찍어주고
                            mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_online)); //이미지변환
                        }
                    }
                    break;
                case Constants.MESSAGE_BT_STATE_ERROR:
                    mTextStatus.setText(getResources().getString(R.string.bt_state_error));
                    mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_busy));
                    break;

                // BT Command status
                case Constants.MESSAGE_CMD_ERROR_NOT_CONNECTED:
                    mTextStatus.setText(getResources().getString(R.string.bt_cmd_sending_error));
                    mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_busy));
                    break;

                ////////////////////////////////////////////
                // Handle messages here.
                ////////////////////////////////////////////
//			case MESSAGE_xxx:
//			{
//				break;
//			}

                default:
                    break;
            }

            super.handleMessage(msg);
        }
    }	// End of class ActivityHandler



}
