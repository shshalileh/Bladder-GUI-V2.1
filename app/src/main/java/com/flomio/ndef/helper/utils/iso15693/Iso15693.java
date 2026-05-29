package com.flomio.ndef.helper.utils.iso15693;

import android.content.Context;
import android.nfc.Tag;

import java.nio.ByteBuffer;

/**
 * Created by Udayan on 6/4/13.
 */
public class Iso15693 implements OnCommandExecutedCallBack {


    protected Context mContext;
    protected Tag mTag;
    boolean mReturned = false;
    protected byte[] mResponse = null;
    protected OnCommandExecutedCallBack mOnCommandExecutedCallBack;


    public static final byte GetSystemInfo = 0x00;
    public static final byte ReadSingleBlock = 0x02;
    public static final byte WriteSingleBlock = 0x03;
    public static final byte ReadMultipleBlocks = 0x04;
    public static final byte IssueInventoryCommandSingleSlot = 0x05;
    public static final byte IssueInventoryCommand16Slot = 0x06;
    public static final byte WriteMultipleBlocks = 0x07;
    public static final byte GetSecurityStatus = 0x08;

    public static final byte ISO15693_GET_SYSTEM_INFO_COMMAND = 0x00;
    public static final byte ISO15693_READ_SINGLE_BLOCK_COMMAND = 0x02;
    public static final byte ISO15693_READ_MULTIPLE_BLOCKS_COMMAND = 0x04;

    byte mCurrentCommand;

    public Iso15693(Context context, Tag tag) {
        mContext = context;
        mTag = tag;
    }

    public void ReadSingleBlock(byte BlockId, OnCommandExecutedCallBack onCommandExecutedCallBack) {
        mResponse = null;
        Iso15693ConstructAndSendCommands mIso15693ConstructAndSendCommands = new Iso15693ConstructAndSendCommands(mContext, mTag, this);
        mIso15693ConstructAndSendCommands.execute(new byte[]{Iso15693ConstructAndSendCommands.ISO15693_READ_SINGLE_BLOCK_COMMAND}, new byte[]{BlockId});
        mOnCommandExecutedCallBack = onCommandExecutedCallBack;
        mCurrentCommand = ReadSingleBlock;
    }

    public void ReadMultipleBlocks(byte BlockOffset, byte NumberOfBlocks, OnCommandExecutedCallBack onCommandExecutedCallBack) {
        mResponse = null;
        Iso15693ConstructAndSendCommands mIso15693ConstructAndSendCommands = new Iso15693ConstructAndSendCommands(mContext, mTag, this);
        mIso15693ConstructAndSendCommands.execute(new byte[]{Iso15693ConstructAndSendCommands.ISO15693_READ_MULTIPLE_BLOCKS}, new byte[]{BlockOffset}, new byte[]{NumberOfBlocks});
        mOnCommandExecutedCallBack = onCommandExecutedCallBack;
        mCurrentCommand = ReadMultipleBlocks;
    }

    
    public void GetSystemInfo(OnCommandExecutedCallBack onCommandExecutedCallBack) {
        mResponse = null;
        Iso15693ConstructAndSendCommands mIso15693ConstructAndSendCommands = new Iso15693ConstructAndSendCommands(mContext, mTag, this);
        mIso15693ConstructAndSendCommands.execute(new byte[]{Iso15693ConstructAndSendCommands.ISO15693_GET_SYSTEM_INFO});
        mOnCommandExecutedCallBack = onCommandExecutedCallBack;
        mCurrentCommand = GetSystemInfo;
    }

    public void IssueInventoryCommandSingleSlot(OnCommandExecutedCallBack onCommandExecutedCallBack){
        mResponse = null;
        Iso15693ConstructAndSendCommands mIso15693ConstructAndSendCommands = new Iso15693ConstructAndSendCommands(mContext, mTag, this);
        mIso15693ConstructAndSendCommands.execute(new byte[]{Iso15693ConstructAndSendCommands.ISO15693_ISSUE_INVENTORY_COMMAND_SINGLE_SLOT});
        mOnCommandExecutedCallBack = onCommandExecutedCallBack;
        mCurrentCommand = IssueInventoryCommandSingleSlot;
    }

    public void GetSecurityStatus(OnCommandExecutedCallBack onCommandExecutedCallBack){
        mResponse = null;
        Iso15693ConstructAndSendCommands mIso15693ConstructAndSendCommands = new Iso15693ConstructAndSendCommands(mContext, mTag, this);
        mIso15693ConstructAndSendCommands.execute(new byte[]{Iso15693ConstructAndSendCommands.ISO15693_GET_SECURITY_STATUS});
        mOnCommandExecutedCallBack = onCommandExecutedCallBack;
        mCurrentCommand = GetSecurityStatus;
    }

    @Override
    public void onCommandExecuted(byte[] result) {
        mResponse = result;
        mReturned = true;

        if (mCurrentCommand == WriteSingleBlock || mCurrentCommand == WriteMultipleBlocks) {
            onWriteCommandExecuted(mResponse);
        } else {
            if (mOnCommandExecutedCallBack != null)
                mOnCommandExecutedCallBack.onCommandExecuted(mResponse);
        }
    }

    @Override
    public void onWriteCommandExecuted(byte[] response) {


    }


    public static byte[] getUidIso15693(byte[] systemInfo) {

        ByteBuffer uidBuffer = ByteBuffer.allocate(8);
        for (int i = 2; i < 10; i++) {
            uidBuffer.put(systemInfo[i]);
        }

        return uidBuffer.array();
    }

    public static byte getDsfid(byte[] systemInfo) {
        return systemInfo[10];
    }

    public static byte getAfi(byte[] systemInfo) {
        return systemInfo[11];
    }

    public static int getBlockSize(byte[] systemInfo) {
//        return ((int) systemInfo[13] + 1);
    	return ((int) systemInfo[11] + 1);
    }

    public static int getNumberOfBlocks(byte[] systemInfo) {
//        return ((int) systemInfo[12] + 1);
    	return ((int) systemInfo[10] + 1);
    }

    public static byte getIcReference(byte[] systemInfo) {
        return systemInfo[14];
    }

    public static String getManufacturer(byte[] uid) {
        String manufacturer = null;

        switch (uid[6]) {

            case 0x01:
                manufacturer = "Motorola";
                break;
            case 0x02:
                manufacturer = "ST Microelectronics";
                break;
            case 0x03:
                manufacturer = "Hitachi";
                break;
            case 0x04:
                manufacturer = "NXP Semiconductors";
                break;
            case 0x05:
                manufacturer = "Infineon Technologies";
                break;
            case 0x06:
                manufacturer = "Cylinc";
                break;
            case 0x07:
                manufacturer = "Texas Instruments Tag-it™ HF-I";
                break;
            case 0x08:
                manufacturer = "Fujitsu Limited";
                break;
            case 0x09:
                manufacturer = "Matsushita Electric Industrial";
                break;
            case 0x0A:
                manufacturer = "NEC";
                break;
            case 0x0B:
                manufacturer = "Oki Electric";
                break;
            case 0x0C:
                manufacturer = "Toshiba";
                break;
            case 0x0D:
                manufacturer = "Mitsubishi Electric";
                break;
            case 0x0E:
                manufacturer = "Samsung Electronics";
                break;
            case 0x0F:
                manufacturer = "Hyundai Electronics";
                break;
            case 0x10:
                manufacturer = "LG Semiconductors";
                break;
            case 0x16:
                manufacturer = "EM Microelectronic-Marin";
                break;
        }

        return manufacturer;

    }


}


