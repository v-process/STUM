package com.example.administrator.STUM.bluetooth;

/**
 * Created by Administrator on 2015-05-22.
 */

import android.os.Handler;
import android.util.Log;

import com.example.administrator.STUM.Constants;

public class TransactionBuilder {
    private static final String TAG = "TransactionBuilder";

    private BluetoothManager mBTManager = null;
    private Handler mHandler = null;

    public TransactionBuilder(BluetoothManager bm, Handler errorHandler) {
        mBTManager = bm;
        mHandler = errorHandler;
    }

    public Transaction makeTransaction() {
        return new Transaction();
    }

    public class Transaction {

        public static final int MAX_MESSAGE_LENGTH = 16;

        // Command types
        //public static final int COMMAND_TYPE_NONE = 0x00;

        // Transaction instance status
        private static final int STATE_NONE = 0;		// Instance created
        private static final int STATE_BEGIN = 1;		// Initialize transaction
        private static final int STATE_SETTING_FINISHED = 2;	// End of setting parameters
        private static final int STATE_TRANSFERED = 3;	// End of sending transaction data
        private static final int STATE_ERROR = -1;		// Error occurred

        // Transaction parameters
        private int mState = STATE_NONE;
        private byte[] mBuffer = null;
        private String mMsg = null;


        public void begin() {
            mState = STATE_BEGIN;
            mMsg = null;
            mBuffer = null;
        }

        /**
         * Set string message to send
         * @param \\id	Identifier - WARNING: use lower 1 byte only
         * @param msg	String to send
         */
        public void setMessage(String msg) {
            mMsg = msg;
        } //이리로 들어오는 메세지를 받아서 변수에 받지요.

        /**
         * Ready to send data to remote
         */
        public void settingFinished() {
            mState = STATE_SETTING_FINISHED;
            mBuffer = mMsg.getBytes(); //그 받은 바이트를 버퍼변수에 넣고..
        }

        /**
         * Send packet to remote
         * @return	boolean		is succeeded
         */
        public boolean sendTransaction() {
            if(mBuffer == null || mBuffer.length < 1) {
                Log.e(TAG, "##### Ooooooops!! No sending buffer!! Check command!!");
                return false;
            }

            // TODO: For debug. Comment out below lines if you want to see the packets
            //디버그를 위한거니까..일단 닫아두세
			/*if(mBuffer.length > 0) {
				StringBuilder sb = new StringBuilder();//변경가능한 문자열
				sb.append("Message : ");

				for(int i=0; i<mBuffer.length; i++) {
					sb.append(String.format("%02X, ", mBuffer[i])); //변경가능문자열에 버퍼에있는애를 차곡차곡 넣어주네요 //이걸 바꿔볼까요..
				}

				Log.d(TAG, " ");
				Log.d(TAG, sb.toString());
			}*/
            //여기까지가 디버그를 위한거임

            if(mState == STATE_SETTING_FINISHED) {
                if(mBTManager != null) {
                    // Check that we're actually connected before trying anything
                    if (mBTManager.getState() == BluetoothManager.STATE_CONNECTED) {
                        // Check that there's actually something to send
                        if (mBuffer.length > 0) {
                            // Get the message bytes and tell the BluetoothChatService to write
                            mBTManager.write(mBuffer); //메니저에서 보내는군요

                            mState = STATE_TRANSFERED;
                            return true;
                        }
                        mState = STATE_ERROR;
                    }
                    // Report result
                    mHandler.obtainMessage(Constants.MESSAGE_CMD_ERROR_NOT_CONNECTED).sendToTarget(); //액티비티핸들러를 오류핸들러로 받은거였으니.. 이건 메인액티비티로가겠지.
                }
            }
            return false;
        }

        /**
         * Get buffers to send to remote
         */
        public byte[] getPacket() {
            if(mState == STATE_SETTING_FINISHED) {
                return mBuffer;
            }
            return null;
        }

    }	// End of class Transaction

}
