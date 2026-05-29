package com.flomio.ndef.helper.utils.iso15693;

import android.content.Context;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.AsyncTask;
import android.util.Log;

import com.flomio.ndef.helper.utils.FlomioNdefHelper;

import java.io.IOException;

/**
 * Created by Udayan on 5/29/13.
 */
public class Iso15693ConstructAndSendCommands extends AsyncTask<byte[], Void, byte[]> {


    protected Tag mTag;
    protected Context mContext;
    static final String LOG_TAG = Iso15693ConstructAndSendCommands.class.getSimpleName();
/*
The complete ISO15693 commands for TI's tags

    public static final byte[] mCommandInventoryRequest = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x04, (byte) 0x14, (byte) 0x04, (byte) 0x03, (byte) 0x00, (byte) 0x0B, (byte) 0x01};
    public static final byte[] mCommandInventoryRequestL = {(byte) 0x01, (byte) 0x0B, (byte) 0x00, (byte) 0x03, (byte) 0x04, (byte) 0x14, (byte) 0x24, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00};
    public static final byte[] mCommandWriteSingleBlockL = {0x42, 0x21, (byte) 0x3F, 0x04, 0x05, 0x02, (byte) 0xff};
    public static final byte[] mCommandReadSingleBlockL = {0x02, 0x20, 0x3F};
    public static final byte[] mCommandReadMultipleBlockL = {0x02, 0x23, 0x00, 0x3F};
    public static final byte[] mCommandGetSystemInfoL = {0x00, (byte) 0x2B};
*/

    public static final byte ISO15693_GET_SYSTEM_INFO = 0x00;
    public static final byte ISO15693_READ_SINGLE_BLOCK_COMMAND = 0x02;
    public static final byte ISO15693_WRITE_SINGLE_BLOCK = 0x03;
    public static final byte ISO15693_READ_MULTIPLE_BLOCKS = 0x04;
    public static final byte ISO15693_ISSUE_INVENTORY_COMMAND_SINGLE_SLOT = 0x05;
    public static final byte ISO15693_ISSUE_INVENTORY_COMMAND_16_SLOT = 0x06;
    public static final byte ISO15693_WRITE_MULTIPLE_BLOCKS = 0x07;
    public static final byte ISO15693_GET_SECURITY_STATUS = 0x08;


    byte[] mResponse = null;
    private byte mCurrentCommand;
    private OnCommandExecutedCallBack mOnCommandExecutedCallBack;

    public Iso15693ConstructAndSendCommands(Context context, Tag tag, OnCommandExecutedCallBack onCommandExecutedCallBack) {
       try{
    	mTag = tag;
        mContext = context;
        mOnCommandExecutedCallBack = onCommandExecutedCallBack;}
       catch(Exception e){}
    }

    @Override
    protected byte[] doInBackground(byte[]... params) {

        if (mTag != null) {
            NfcV mNfcVObject = NfcV.get(mTag);
            byte[] commandToExecute = null;

            mCurrentCommand = params[0][0];
            switch (params[0][0]) {
                case ISO15693_READ_SINGLE_BLOCK_COMMAND:
                    commandToExecute = new byte[]{0x02, 0x20, params[1][0]};
                    break;
                case ISO15693_READ_MULTIPLE_BLOCKS:
                    commandToExecute = new byte[]{0x42, 0x23, params[1][0], params[2][0]};
                    break;
                case ISO15693_WRITE_SINGLE_BLOCK:
                	commandToExecute = new byte[]{0x42, 0x21, (byte) params[1][0], params[2][0], params[2][1], params[2][2], params[2][3], params[2][4], params[2][5], params[2][6], params[2][7]};                	
                	break;
                case ISO15693_GET_SYSTEM_INFO:
                    commandToExecute = new byte[]{0x00, (byte) 0x2B};
                    break;
                case ISO15693_ISSUE_INVENTORY_COMMAND_SINGLE_SLOT:
                    commandToExecute = new byte[]{0x26, (byte) 0x01, 0x00};
                    break;
                case ISO15693_ISSUE_INVENTORY_COMMAND_16_SLOT:
                    break;
                case ISO15693_GET_SECURITY_STATUS:
                    commandToExecute = new byte[]{0x02, (byte) 0x2C};
                    break;
            }


            if (mNfcVObject != null) {
                try {
                    mNfcVObject.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, e.toString());
                }

                if (mNfcVObject.isConnected()) {
                    int i = 0;

                    try {
                        mResponse = mNfcVObject.transceive(commandToExecute);
                        String responseString = FlomioNdefHelper.mBytesToHexString(mResponse);
                        //Log.d(String.format(LOG_TAG + getCurrentCommand(mCurrentCommand) + " Response %d", i), responseString);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(LOG_TAG, e.toString());
                    }

                    try {
                        mNfcVObject.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(LOG_TAG, e.toString());
                    }
                }
            }
        }
        return mResponse;
    }

    @Override
    protected void onPostExecute(byte[] response) {
        super.onPostExecute(response);
        mOnCommandExecutedCallBack.onCommandExecuted(response);
        return;
    }

    @Override
    protected void onCancelled() {
        mOnCommandExecutedCallBack.onCommandExecuted(mResponse);
    }

    public String getCurrentCommand(byte command) {
        String commandString = null;
        switch (command) {
            case ISO15693_GET_SYSTEM_INFO:
                commandString = "ISO15693_GET_SYSTEM_INFO_COMMAND";
                break;
            case ISO15693_ISSUE_INVENTORY_COMMAND_16_SLOT:
                commandString = "ISO15693_ISSUE_INVENTORY_COMMAND_16_SLOT";
                break;
            case ISO15693_ISSUE_INVENTORY_COMMAND_SINGLE_SLOT:
                commandString = "ISO15693_ISSUE_INVENTORY_COMMAND_SINGLE_SLOT";
                break;
            case ISO15693_READ_SINGLE_BLOCK_COMMAND:
                commandString = "ISO15693_READ_SINGLE_BLOCK_COMMAND";
                break;
            case ISO15693_READ_MULTIPLE_BLOCKS:
                commandString = "ISO15693_READ_MULTIPLE_BLOCKS_COMMAND";
                break;
            case ISO15693_WRITE_SINGLE_BLOCK:
                commandString = "ISO15693_WRITE_SINGLE_BLOCK";
                break;
            case ISO15693_WRITE_MULTIPLE_BLOCKS:
                commandString = "ISO15693_WRITE_MULTIPLE_BLOCKS";
                break;
        }
        return commandString;
    }
}
