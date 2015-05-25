package com.example.administrator.STUM.bluetooth;

/**
 * Created by Administrator on 2015-05-22.
 */

import android.os.Handler;

import java.util.ArrayList;

public class TransactionReceiver {



    private static final String TAG = "TransactionReceiver";

    private static final int PARSE_MODE_ERROR = 0;
    private static final int PARSE_MODE_WAIT_START_BYTE = 1;
    private static final int PARSE_MODE_WAIT_COMMAND = 2;
    private static final int PARSE_MODE_WAIT_DATA = 3;
    private static final int PARSE_MODE_WAIT_END_BYTE = 4;
    private static final int PARSE_MODE_COMPLETED = 101; //추가해봄...아마 END와 같을거같은데

    private Handler mHandler = null;


    private ArrayList<Transaction> mTransactionQueue = new ArrayList<Transaction>();

    private int mParseMode = PARSE_MODE_WAIT_START_BYTE;
    private int mCommand = Transaction.COMMAND_TYPE_NONE;
    private Transaction mTransaction = null;

    public TransactionReceiver(Handler h) {
        mHandler = h;
        mParseMode = PARSE_MODE_WAIT_START_BYTE;
    }

	/*public void setByteArray(byte[] buffer) {
		parseStream(buffer);
	}*/
    /**
     * Set bytes to parse
     * This method automatically calls parseStream()
     * @param buffer
     * @param count
     */
    public void setByteArray(byte[] buffer, int count) {
        parseStream(buffer, count);
    }

    public void popTransaction() {
        // TODO:
    }

	/*private void parseStream(byte[] buffer) {
		if(buffer != null && buffer.length > 0) {
			for(int i=0; i<buffer.length; i++) {

				switch(mParseMode) {
				case PARSE_MODE_WAIT_START_BYTE:
					parseStartByte(buffer[i]);
					break;

				case PARSE_MODE_WAIT_COMMAND:
					parseCommand(buffer[i]);
					break;
				case PARSE_MODE_WAIT_DATA:
					parseData(buffer[i]);
					break;
				case PARSE_MODE_WAIT_END_BYTE:
					parseEndByte(buffer[i]);
					break;
				}
			}	// End of for loop
		}	// End of if()
	}*/
    /**
     * Caching received stream and parse byte array
     * @param buffer		byte array to parse
     * @param count			byte array size
     */
    //위에껀데 추가해봄..
    public void parseStream(byte[] buffer, int count) {
        if(buffer != null && buffer.length > 0 && count > 0) {
            for(int i=0; i < buffer.length && i < count; i++) {

                // Parse received data
                // Protocol description -----------------------------------------------------------
                // Describe brief info about protocol

                // TODO: parse buffer



            }	// End of for loop
        }	// End of if()
    }	// End of parseStream()

    private void parseStartByte(byte packet) {
        if(packet == Transaction.TRANSACTION_START_BYTE) {
            mParseMode = PARSE_MODE_WAIT_COMMAND;
            mTransaction = new Transaction();
        }
    }

    private void parseCommand(byte cmd) {
        mCommand = cmd;
        switch(mCommand) {
            case Transaction.COMMAND_TYPE_PING:
                mParseMode = PARSE_MODE_WAIT_END_BYTE;
                break;

            // TODO:

            default:
                break;
        }	// End of switch()
    }	// End of parseCommand()

    private void parseData(byte packet) {
        if(packet == Transaction.TRANSACTION_END_BYTE) {
            mParseMode = PARSE_MODE_WAIT_START_BYTE;
            pushTransaction();
        }

        // TODO:
    }

    private void parseEndByte(byte packet) {
        if(packet == Transaction.TRANSACTION_END_BYTE) {
            mParseMode = PARSE_MODE_WAIT_START_BYTE;
            pushTransaction();
        }
    }

    private void pushTransaction() {
        if(mTransaction != null) {
            mTransactionQueue.add(mTransaction);
            mTransaction = null;
        }
    }


    public class Transaction {
        private static final byte TRANSACTION_START_BYTE = (byte)0xfc;
        private static final byte TRANSACTION_END_BYTE = (byte)0xfd;

        public static final int COMMAND_TYPE_NONE = 0x00;
        public static final int COMMAND_TYPE_PING = 0x01;

    }

    /** 추가해봄
     * After parsing bytes received, transaction receiver makes object instance.
     * This method returns parsed results
     * @return	Object		parsed object
     */
    public Object getObject() {
        // TODO: return what you want
        return null;
    }

}
